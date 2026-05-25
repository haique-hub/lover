package com.lovers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CoupleBindDTO {

    @NotBlank(message = "绑定码不能为空")
    private String bindCode;
}
