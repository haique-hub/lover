package com.lovers.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lovers.common.BaseEntity;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@TableName("couple_relation")
@EqualsAndHashCode(callSuper = true)
public class CoupleRelation extends BaseEntity {

    private Long userId;
    private Long partnerId;
    private String bindCode;
    private Integer status;
    private LocalDateTime bindTime;
    private LocalDateTime unbindTime;
}
