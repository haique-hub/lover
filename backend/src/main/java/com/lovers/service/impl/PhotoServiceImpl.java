package com.lovers.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.dto.PhotoUploadDTO;
import com.lovers.entity.Album;
import com.lovers.entity.Photo;
import com.lovers.entity.PhotoComment;
import com.lovers.entity.User;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.AlbumMapper;
import com.lovers.mapper.PhotoCommentMapper;
import com.lovers.mapper.PhotoMapper;
import com.lovers.service.CoupleService;
import com.lovers.service.PhotoService;
import com.lovers.service.UserService;
import com.lovers.vo.PhotoVO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final PhotoMapper photoMapper;
    private final AlbumMapper albumMapper;
    private final PhotoCommentMapper photoCommentMapper;
    private final CoupleService coupleService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PhotoVO uploadPhoto(Long userId, PhotoUploadDTO dto) {
        Album album = getAlbumOrThrow(dto.getAlbumId());
        if (!coupleService.canAccessUser(userId, album.getUserId())) {
            throw new BusinessException(403, "无权上传到该相册");
        }
        MultipartFile file = dto.getFile();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("文件名不能为空");
        }
        String extension = extractExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持上传jpg、jpeg、png、gif、webp格式图片");
        }
        byte[] fileData;
        try {
            fileData = file.getBytes();
        } catch (IOException exception) {
            log.error("Failed to read uploaded photo bytes. albumId={}, userId={}, fileName={}",
                dto.getAlbumId(), userId, originalFilename, exception);
            throw new BusinessException(500, "图片上传失败");
        }
        Photo photo = new Photo();
        photo.setAlbumId(dto.getAlbumId());
        photo.setUserId(userId);
        photo.setFileName(originalFilename);
        photo.setFileUrl("");
        photo.setContentType(resolveContentType(file, extension));
        photo.setDescription(dto.getDescription());
        photo.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        photo.setFileData(fileData);
        photoMapper.insert(photo);
        photo.setFileUrl(buildPhotoFileUrl(photo.getId()));
        photoMapper.updateById(photo);
        if (album.getCoverUrl() == null || album.getCoverUrl().isBlank()) {
            album.setCoverUrl(photo.getFileUrl());
            albumMapper.updateById(album);
        }
        return toPhotoVO(photo, userService.getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePhoto(Long userId, Long photoId) {
        Photo photo = getByIdOrThrow(photoId);
        Album album = getAlbumOrThrow(photo.getAlbumId());
        if (!photo.getUserId().equals(userId) && !album.getUserId().equals(userId)) {
            throw new BusinessException("只有上传者或相册创建者可以删除照片");
        }
        photoCommentMapper.delete(Wrappers.<PhotoComment>lambdaQuery().eq(PhotoComment::getPhotoId, photoId));
        photoMapper.deleteById(photoId);
        if (photo.getFileUrl().equals(album.getCoverUrl())) {
            refreshAlbumCover(album.getId());
        }
    }

    @Override
    public List<PhotoVO> listPhotosByAlbum(Long userId, Long albumId) {
        Album album = getAlbumOrThrow(albumId);
        if (!coupleService.canAccessUser(userId, album.getUserId())) {
            throw new BusinessException(403, "无权查看该相册照片");
        }
        List<Photo> photos = photoMapper.selectList(
            Wrappers.<Photo>lambdaQuery()
                .eq(Photo::getAlbumId, albumId)
                .orderByAsc(Photo::getSortOrder)
                .orderByDesc(Photo::getCreateTime)
        );
        Map<Long, User> userMap = userService.getUserMap(
            photos.stream().map(Photo::getUserId).collect(Collectors.toSet())
        );
        return photos.stream().map(photo -> toPhotoVO(photo, userMap.get(photo.getUserId()))).toList();
    }

    @Override
    public List<PhotoVO> listRecentPhotos(Long userId, Integer limit) {
        int safeLimit = limit == null ? 12 : Math.max(1, Math.min(limit, 30));
        Set<Long> accessibleUserIds = coupleService.listAccessibleUserIds(userId);
        List<Photo> photos = photoMapper.selectList(
            Wrappers.<Photo>lambdaQuery()
                .in(Photo::getUserId, accessibleUserIds)
                .orderByDesc(Photo::getCreateTime)
                .last("limit " + safeLimit)
        );
        Map<Long, User> userMap = userService.getUserMap(
            photos.stream().map(Photo::getUserId).collect(Collectors.toSet())
        );
        return photos.stream().map(photo -> toPhotoVO(photo, userMap.get(photo.getUserId()))).toList();
    }

    @Override
    public PhotoVO getPhotoDetail(Long userId, Long photoId) {
        Photo photo = getByIdOrThrow(photoId);
        Album album = getAlbumOrThrow(photo.getAlbumId());
        if (!coupleService.canAccessUser(userId, album.getUserId())) {
            throw new BusinessException(403, "无权查看该照片");
        }
        return toPhotoVO(photo, userService.getByIdOrThrow(photo.getUserId()));
    }

    @Override
    public Photo getByIdOrThrow(Long photoId) {
        Photo photo = photoMapper.selectById(photoId);
        if (photo == null) {
            throw new BusinessException("照片不存在");
        }
        return photo;
    }

    @Override
    public Photo getContentByIdOrThrow(Long photoId) {
        Photo photo = photoMapper.selectContentById(photoId);
        if (photo == null) {
            throw new BusinessException("照片不存在");
        }
        if (photo.getFileData() == null || photo.getFileData().length == 0) {
            throw new BusinessException(404, "图片内容不存在");
        }
        return photo;
    }

    @Override
    public void verifyPhotoAccessible(Long currentUserId, Long photoId) {
        Photo photo = getByIdOrThrow(photoId);
        Album album = getAlbumOrThrow(photo.getAlbumId());
        if (!coupleService.canAccessUser(currentUserId, album.getUserId())) {
            throw new BusinessException(403, "无权访问该照片");
        }
    }

    private Album getAlbumOrThrow(Long albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new BusinessException("相册不存在");
        }
        return album;
    }

    private PhotoVO toPhotoVO(Photo photo, User user) {
        PhotoVO vo = new PhotoVO();
        BeanUtils.copyProperties(photo, vo);
        if (user != null) {
            vo.setUploaderNickname(user.getNickname());
            vo.setUploaderAvatar(user.getAvatar());
        }
        return vo;
    }

    private void refreshAlbumCover(Long albumId) {
        Album album = getAlbumOrThrow(albumId);
        Photo firstPhoto = photoMapper.selectOne(
            Wrappers.<Photo>lambdaQuery()
                .eq(Photo::getAlbumId, albumId)
                .orderByAsc(Photo::getSortOrder)
                .orderByDesc(Photo::getCreateTime)
                .last("limit 1")
        );
        album.setCoverUrl(firstPhoto == null ? null : firstPhoto.getFileUrl());
        albumMapper.updateById(album);
    }

    private String extractExtension(String originalFilename) {
        int dotIndex = originalFilename.lastIndexOf('.');
        return dotIndex >= 0 ? originalFilename.substring(dotIndex + 1).toLowerCase() : "";
    }

    private String resolveContentType(MultipartFile file, String extension) {
        String contentType = file.getContentType();
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private String buildPhotoFileUrl(Long photoId) {
        return "/files/photos/" + photoId;
    }
}
