package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.UserUpdateDTO;
import com.lovers.service.UserService;
import com.lovers.vo.UserInfoVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UserUpdateDTO dto) {
        return Result.success(userService.updateProfile(UserContext.getUserId(), dto));
    }

    @PostMapping("/avatar")
    public Result<UserInfoVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return Result.success(userService.uploadAvatar(UserContext.getUserId(), file));
    }
}
