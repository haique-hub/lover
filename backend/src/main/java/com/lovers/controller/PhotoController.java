package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.PhotoUploadDTO;
import com.lovers.service.PhotoService;
import com.lovers.vo.PhotoVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/upload")
    public Result<PhotoVO> upload(@Valid @ModelAttribute PhotoUploadDTO dto) {
        return Result.success(photoService.uploadPhoto(UserContext.getUserId(), dto));
    }

    @DeleteMapping("/{photoId}")
    public Result<Void> delete(@PathVariable Long photoId) {
        photoService.deletePhoto(UserContext.getUserId(), photoId);
        return Result.success();
    }

    @GetMapping("/album/{albumId}")
    public Result<List<PhotoVO>> list(@PathVariable Long albumId) {
        return Result.success(photoService.listPhotosByAlbum(UserContext.getUserId(), albumId));
    }

    @GetMapping("/recent")
    public Result<List<PhotoVO>> recent() {
        return Result.success(photoService.listRecentPhotos(UserContext.getUserId(), 12));
    }

    @GetMapping("/{photoId}")
    public Result<PhotoVO> detail(@PathVariable Long photoId) {
        return Result.success(photoService.getPhotoDetail(UserContext.getUserId(), photoId));
    }
}
