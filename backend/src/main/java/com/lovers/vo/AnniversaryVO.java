package com.lovers.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AnniversaryVO {

    private Long id;
    private Long userId;
    private String ownerNickname;
    private String ownerAvatar;
    private String title;
    private String type;
    private String calendarType;
    private LocalDate anniversaryDate;
    private Integer lunarYear;
    private Integer lunarMonth;
    private Integer lunarDay;
    private Boolean lunarLeapMonth;
    private String calendarLabel;
    private String relationScope;
    private String repeatType;
    private Integer reminderDays;
    private Long coverPhotoId;
    private String coverPhotoUrl;
    private String description;
    private LocalDate nextOccurrenceDate;
    private Long daysUntilNext;
    private Long daysSinceStart;
    private Boolean today;
    private Boolean expired;
    private LocalDateTime createTime;
}
