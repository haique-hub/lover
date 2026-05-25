package com.lovers.controller;

import com.lovers.entity.Photo;
import com.lovers.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PhotoFileController {

    private final PhotoService photoService;

    @GetMapping("/files/photos/{photoId}")
    public ResponseEntity<byte[]> content(@PathVariable Long photoId) {
        Photo photo = photoService.getContentByIdOrThrow(photoId);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (photo.getContentType() != null && !photo.getContentType().isBlank()) {
            mediaType = MediaType.parseMediaType(photo.getContentType());
        }
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + photo.getFileName() + "\"")
            .cacheControl(CacheControl.noCache())
            .body(photo.getFileData());
    }
}
