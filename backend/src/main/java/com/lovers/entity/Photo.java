package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import java.util.Arrays;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("photo")
@EqualsAndHashCode(callSuper = true)
public class Photo extends BaseEntity {

    private Long albumId;
    private Long userId;
    private String fileName;
    private String fileUrl;
    private String contentType;
    private String description;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(value = "file_data", select = false)
    @EqualsAndHashCode.Exclude
    private byte[] fileData;

    @Override
    public String toString() {
        return "Photo(id=" + getId()
            + ", albumId=" + albumId
            + ", userId=" + userId
            + ", fileName=" + fileName
            + ", fileUrl=" + fileUrl
            + ", contentType=" + contentType
            + ", description=" + description
            + ", sortOrder=" + sortOrder
            + ", fileDataLength=" + (fileData == null ? 0 : fileData.length)
            + ")";
    }
}
