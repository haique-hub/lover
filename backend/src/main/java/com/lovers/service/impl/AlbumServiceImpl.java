package com.lovers.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.dto.AlbumCreateDTO;
import com.lovers.dto.AlbumUpdateDTO;
import com.lovers.entity.Album;
import com.lovers.entity.Photo;
import com.lovers.entity.PhotoComment;
import com.lovers.entity.User;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.AlbumMapper;
import com.lovers.mapper.PhotoCommentMapper;
import com.lovers.mapper.PhotoMapper;
import com.lovers.service.AlbumService;
import com.lovers.service.CoupleService;
import com.lovers.service.UserService;
import com.lovers.vo.AlbumVO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final AlbumMapper albumMapper;
    private final PhotoMapper photoMapper;
    private final PhotoCommentMapper photoCommentMapper;
    private final CoupleService coupleService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlbumVO createAlbum(Long userId, AlbumCreateDTO dto) {
        Album album = new Album();
        album.setUserId(userId);
        album.setName(dto.getName());
        album.setDescription(dto.getDescription());
        albumMapper.insert(album);
        return toAlbumVO(album, userService.getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AlbumVO updateAlbum(Long userId, Long albumId, AlbumUpdateDTO dto) {
        Album album = getByIdOrThrow(albumId);
        if (!album.getUserId().equals(userId)) {
            throw new BusinessException("只有相册创建者可以编辑相册");
        }
        album.setName(dto.getName());
        album.setDescription(dto.getDescription());
        albumMapper.updateById(album);
        return toAlbumVO(album, userService.getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAlbum(Long userId, Long albumId) {
        Album album = getByIdOrThrow(albumId);
        if (!album.getUserId().equals(userId)) {
            throw new BusinessException("只有相册创建者可以删除相册");
        }
        List<Photo> photos = photoMapper.selectList(
            Wrappers.<Photo>lambdaQuery().eq(Photo::getAlbumId, albumId)
        );
        List<Long> photoIds = photos.stream().map(Photo::getId).toList();
        if (!photoIds.isEmpty()) {
            photoCommentMapper.delete(Wrappers.<PhotoComment>lambdaQuery().in(PhotoComment::getPhotoId, photoIds));
            photoMapper.delete(Wrappers.<Photo>lambdaQuery().in(Photo::getId, photoIds));
        }
        albumMapper.deleteById(albumId);
    }

    @Override
    public List<AlbumVO> listAlbums(Long userId) {
        Set<Long> accessibleUserIds = coupleService.listAccessibleUserIds(userId);
        List<Album> albums = albumMapper.selectList(
            Wrappers.<Album>lambdaQuery()
                .in(Album::getUserId, accessibleUserIds)
                .orderByDesc(Album::getCreateTime)
        );
        Map<Long, User> userMap = userService.getUserMap(
            albums.stream().map(Album::getUserId).collect(Collectors.toSet())
        );
        return albums.stream().map(album -> toAlbumVO(album, userMap.get(album.getUserId()))).toList();
    }

    @Override
    public AlbumVO getAlbumDetail(Long userId, Long albumId) {
        Album album = getByIdOrThrow(albumId);
        if (!coupleService.canAccessUser(userId, album.getUserId())) {
            throw new BusinessException(403, "无权查看该相册");
        }
        return toAlbumVO(album, userService.getByIdOrThrow(album.getUserId()));
    }

    @Override
    public Album getByIdOrThrow(Long albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new BusinessException("相册不存在");
        }
        return album;
    }

    private AlbumVO toAlbumVO(Album album, User owner) {
        AlbumVO vo = new AlbumVO();
        BeanUtils.copyProperties(album, vo);
        if (owner != null) {
            vo.setOwnerNickname(owner.getNickname());
            vo.setOwnerAvatar(owner.getAvatar());
        }
        return vo;
    }
}
