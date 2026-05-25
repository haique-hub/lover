package com.lovers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 20, message = "昵称长度不能超过20位")
    private String nickname;

    private Integer gender;

    @Size(max = 255, message = "个性签名长度不能超过255位")
    private String signature;
}
