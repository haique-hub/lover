package com.lovers.service;

import com.lovers.dto.AlbumCreateDTO;
import com.lovers.dto.AlbumUpdateDTO;
import com.lovers.entity.Album;
import com.lovers.vo.AlbumVO;
import java.util.List;

public interface AlbumService {

    AlbumVO createAlbum(Long userId, AlbumCreateDTO dto);

    AlbumVO updateAlbum(Long userId, Long albumId, AlbumUpdateDTO dto);

    void deleteAlbum(Long userId, Long albumId);

    List<AlbumVO> listAlbums(Long userId);

    AlbumVO getAlbumDetail(Long userId, Long albumId);

    Album getByIdOrThrow(Long albumId);
}
