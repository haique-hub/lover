package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Data;

@Data
@TableName("user")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String avatarContentType;

    @TableField(value = "avatar_data", select = false)
    @EqualsAndHashCode.Exclude
    private byte[] avatarData;

    private Integer gender;
    private String signature;
}
