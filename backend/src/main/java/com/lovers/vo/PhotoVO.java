package com.lovers.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class PhotoVO {

    private Long id;
    private Long albumId;
    private Long userId;
    private String uploaderNickname;
    private String uploaderAvatar;
    private String fileName;
    private String fileUrl;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createTime;
}
