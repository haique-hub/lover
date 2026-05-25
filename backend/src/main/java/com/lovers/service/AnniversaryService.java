package com.lovers.service;

import com.lovers.dto.AnniversaryCreateDTO;
import com.lovers.dto.AnniversaryUpdateDTO;
import com.lovers.entity.Anniversary;
import com.lovers.vo.AnniversarySummaryVO;
import com.lovers.vo.AnniversaryVO;
import java.util.List;

public interface AnniversaryService {

    AnniversaryVO createAnniversary(Long userId, AnniversaryCreateDTO dto);

    AnniversaryVO updateAnniversary(Long userId, Long anniversaryId, AnniversaryUpdateDTO dto);

    void deleteAnniversary(Long userId, Long anniversaryId);

    List<AnniversaryVO> listAnniversaries(Long userId);

    AnniversaryVO getAnniversaryDetail(Long userId, Long anniversaryId);

    AnniversarySummaryVO getSummary(Long userId);

    Anniversary getByIdOrThrow(Long anniversaryId);
}
