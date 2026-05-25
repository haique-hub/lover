package com.lovers.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.dto.LoginDTO;
import com.lovers.dto.RegisterDTO;
import com.lovers.dto.UserUpdateDTO;
import com.lovers.entity.User;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.UserMapper;
import com.lovers.service.UserService;
import com.lovers.utils.JwtUtils;
import com.lovers.vo.LoginVO;
import com.lovers.vo.UserInfoVO;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginVO register(RegisterDTO dto) {
        User existingUser = userMapper.selectOne(
            Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())
        );
        if (existingUser != null) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setGender(0);
        userMapper.insert(user);
        return buildLoginVO(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
            Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return buildLoginVO(user);
    }

    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        return toUserInfoVO(getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UserUpdateDTO dto) {
        User user = getByIdOrThrow(userId);
        user.setNickname(dto.getNickname());
        user.setGender(dto.getGender());
        user.setSignature(dto.getSignature());
        userMapper.updateById(user);
        return toUserInfoVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO uploadAvatar(Long userId, MultipartFile file) {
        User user = getByIdOrThrow(userId);
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("文件名不能为空");
        }
        String extension = extractExtension(originalFilename);
        if (!ALLOWED_IMAGE_EXTENSIONS.contains(extension)) {
            throw new BusinessException("仅支持上传jpg、jpeg、png、gif、webp格式图片");
        }
        byte[] avatarData;
        try {
            avatarData = file.getBytes();
        } catch (IOException exception) {
            log.error("Failed to read uploaded avatar bytes. userId={}, fileName={}",
                userId, originalFilename, exception);
            throw new BusinessException(500, "头像上传失败");
        }
        user.setAvatar(buildAvatarUrl(userId));
        user.setAvatarContentType(resolveContentType(file, extension));
        user.setAvatarData(avatarData);
        userMapper.updateById(user);
        return toUserInfoVO(user);
    }

    @Override
    public User getByIdOrThrow(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    @Override
    public User getAvatarContentByIdOrThrow(Long userId) {
        User user = userMapper.selectAvatarContentById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (user.getAvatarData() == null || user.getAvatarData().length == 0) {
            throw new BusinessException(404, "头像不存在");
        }
        return user;
    }

    @Override
    public Map<Long, User> getUserMap(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userMapper.selectBatchIds(userIds).stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toMap(User::getId, Function.identity(), (left, right) -> left));
    }

    @Override
    public UserInfoVO toUserInfoVO(User user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    private LoginVO buildLoginVO(User user) {
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtUtils.generateToken(user.getId(), user.getUsername()));
        loginVO.setUserInfo(toUserInfoVO(user));
        return loginVO;
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

    private String buildAvatarUrl(Long userId) {
        return "/files/avatars/" + userId;
    }
}
