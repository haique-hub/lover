package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.AlbumCreateDTO;
import com.lovers.dto.AlbumUpdateDTO;
import com.lovers.service.AlbumService;
import com.lovers.vo.AlbumVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public Result<AlbumVO> create(@Valid @RequestBody AlbumCreateDTO dto) {
        return Result.success(albumService.createAlbum(UserContext.getUserId(), dto));
    }

    @PutMapping("/{albumId}")
    public Result<AlbumVO> update(@PathVariable Long albumId, @Valid @RequestBody AlbumUpdateDTO dto) {
        return Result.success(albumService.updateAlbum(UserContext.getUserId(), albumId, dto));
    }

    @DeleteMapping("/{albumId}")
    public Result<Void> delete(@PathVariable Long albumId) {
        albumService.deleteAlbum(UserContext.getUserId(), albumId);
        return Result.success();
    }

    @GetMapping
    public Result<List<AlbumVO>> list() {
        return Result.success(albumService.listAlbums(UserContext.getUserId()));
    }

    @GetMapping("/{albumId}")
    public Result<AlbumVO> detail(@PathVariable Long albumId) {
        return Result.success(albumService.getAlbumDetail(UserContext.getUserId(), albumId));
    }
}
