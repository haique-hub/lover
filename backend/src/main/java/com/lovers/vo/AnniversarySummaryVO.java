package com.lovers.vo;

import java.util.List;
import lombok.Data;

@Data
public class AnniversarySummaryVO {

    private Integer totalCount;
    private Integer upcomingCount;
    private Integer todayCount;
    private AnniversaryVO nextAnniversary;
    private List<AnniversaryVO> upcomingAnniversaries;
}
