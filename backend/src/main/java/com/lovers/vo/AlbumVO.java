package com.lovers.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AlbumVO {

    private Long id;
    private Long userId;
    private String ownerNickname;
    private String ownerAvatar;
    private String name;
    private String coverUrl;
    private String description;
    private LocalDateTime createTime;
}
