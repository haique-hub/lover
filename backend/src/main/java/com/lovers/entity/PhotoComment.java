package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("photo_comment")
@EqualsAndHashCode(callSuper = true)
public class PhotoComment extends BaseEntity {

    private Long photoId;
    private Long userId;
    private Long replyUserId;
    private Long parentId;
    private String content;
}
