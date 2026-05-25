package com.lovers.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CoupleInfoVO {

    private Boolean bound;
    private String bindCode;
    private LocalDateTime bindTime;
    private UserInfoVO self;
    private UserInfoVO partner;
}
