package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("album")
@EqualsAndHashCode(callSuper = true)
public class Album extends BaseEntity {

    private Long userId;
    private String name;
    private String coverUrl;
    private String description;
}
