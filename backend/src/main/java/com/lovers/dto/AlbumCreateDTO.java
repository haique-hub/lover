package com.lovers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AlbumCreateDTO {

    @NotBlank(message = "相册名称不能为空")
    @Size(max = 100, message = "相册名称不能超过100位")
    private String name;

    @Size(max = 255, message = "相册描述不能超过255位")
    private String description;
}
