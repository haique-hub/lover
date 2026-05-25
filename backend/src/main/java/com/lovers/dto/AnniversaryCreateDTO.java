package com.lovers.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Data;

@Data
public class AnniversaryCreateDTO {

    @NotBlank(message = "纪念日名称不能为空")
    @Size(max = 100, message = "纪念日名称不能超过100位")
    private String title;

    @NotBlank(message = "纪念日类型不能为空")
    @Size(max = 32, message = "纪念日类型格式不正确")
    private String type;

    @NotBlank(message = "请选择日期类型")
    @Size(max = 16, message = "日期类型格式不正确")
    private String calendarType;

    private LocalDate anniversaryDate;

    private Integer lunarYear;

    private Integer lunarMonth;

    private Integer lunarDay;

    private Boolean lunarLeapMonth;

    @NotBlank(message = "纪念日归属不能为空")
    @Size(max = 32, message = "纪念日归属格式不正确")
    private String relationScope;

    @NotBlank(message = "重复方式不能为空")
    @Size(max = 32, message = "重复方式格式不正确")
    private String repeatType;

    @Min(value = 0, message = "提醒天数不能小于0")
    @Max(value = 30, message = "提醒天数不能超过30天")
    private Integer reminderDays;

    private Long coverPhotoId;

    @Size(max = 255, message = "纪念日说明不能超过255位")
    private String description;
}
