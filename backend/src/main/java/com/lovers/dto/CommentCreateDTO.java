package com.lovers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDTO {

    @NotNull(message = "照片ID不能为空")
    private Long photoId;

    private Long parentId;

    private Long replyUserId;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    private String content;
}
