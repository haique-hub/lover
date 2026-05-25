package com.lovers.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class CommentVO {

    private Long id;
    private Long photoId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private Long replyUserId;
    private String replyUserNickname;
    private Long parentId;
    private String content;
    private LocalDateTime createTime;
    private List<CommentVO> children = new ArrayList<>();
}
