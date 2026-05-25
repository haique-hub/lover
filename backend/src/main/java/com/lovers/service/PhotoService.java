package com.lovers.service;

import com.lovers.dto.PhotoUploadDTO;
import com.lovers.entity.Photo;
import com.lovers.vo.PhotoVO;
import java.util.List;

public interface PhotoService {

    PhotoVO uploadPhoto(Long userId, PhotoUploadDTO dto);

    void deletePhoto(Long userId, Long photoId);

    List<PhotoVO> listPhotosByAlbum(Long userId, Long albumId);

    List<PhotoVO> listRecentPhotos(Long userId, Integer limit);

    PhotoVO getPhotoDetail(Long userId, Long photoId);

    Photo getByIdOrThrow(Long photoId);

    Photo getContentByIdOrThrow(Long photoId);

    void verifyPhotoAccessible(Long currentUserId, Long photoId);
}
