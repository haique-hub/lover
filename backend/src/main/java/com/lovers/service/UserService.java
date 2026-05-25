package com.lovers.service;

import com.lovers.dto.LoginDTO;
import com.lovers.dto.RegisterDTO;
import com.lovers.dto.UserUpdateDTO;
import com.lovers.entity.User;
import com.lovers.vo.LoginVO;
import com.lovers.vo.UserInfoVO;
import java.util.Collection;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    LoginVO register(RegisterDTO dto);

    LoginVO login(LoginDTO dto);

    UserInfoVO getCurrentUserInfo(Long userId);

    UserInfoVO updateProfile(Long userId, UserUpdateDTO dto);

    UserInfoVO uploadAvatar(Long userId, MultipartFile file);

    User getByIdOrThrow(Long userId);

    User getAvatarContentByIdOrThrow(Long userId);

    Map<Long, User> getUserMap(Collection<Long> userIds);

    UserInfoVO toUserInfoVO(User user);
}
