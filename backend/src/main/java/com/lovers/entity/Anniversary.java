package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("anniversary")
@EqualsAndHashCode(callSuper = true)
public class Anniversary extends BaseEntity {

    private Long userId;
    private String title;
    private String type;
    private String calendarType;
    private LocalDate anniversaryDate;
    private Integer lunarYear;
    private Integer lunarMonth;
    private Integer lunarDay;
    private Boolean lunarLeapMonth;
    private String relationScope;
    private String repeatType;
    private Integer reminderDays;
    private Long coverPhotoId;
    private String description;
}
