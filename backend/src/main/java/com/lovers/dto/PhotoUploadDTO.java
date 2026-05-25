package com.lovers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PhotoUploadDTO {

    @NotNull(message = "相册ID不能为空")
    private Long albumId;

    @Size(max = 255, message = "照片描述不能超过255位")
    private String description;

    private Integer sortOrder;

    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;
}
