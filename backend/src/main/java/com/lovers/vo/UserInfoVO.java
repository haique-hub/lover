package com.lovers.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserInfoVO {

    private Long id;
    private String username;
    private String nickname;
    private String avatar;
    private Integer gender;
    private String signature;
    private LocalDateTime createTime;
}
