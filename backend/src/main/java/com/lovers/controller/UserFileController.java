package com.lovers.controller;

import com.lovers.entity.User;
import com.lovers.service.UserService;
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
public class UserFileController {

    private final UserService userService;

    @GetMapping("/files/avatars/{userId}")
    public ResponseEntity<byte[]> avatar(@PathVariable Long userId) {
        User user = userService.getAvatarContentByIdOrThrow(userId);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (user.getAvatarContentType() != null && !user.getAvatarContentType().isBlank()) {
            mediaType = MediaType.parseMediaType(user.getAvatarContentType());
        }
        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"avatar-" + userId + "\"")
            .cacheControl(CacheControl.noCache())
            .body(user.getAvatarData());
    }
}
