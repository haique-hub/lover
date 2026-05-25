package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.AnniversaryCreateDTO;
import com.lovers.dto.AnniversaryUpdateDTO;
import com.lovers.service.AnniversaryService;
import com.lovers.vo.AnniversarySummaryVO;
import com.lovers.vo.AnniversaryVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/anniversaries")
@RequiredArgsConstructor
public class AnniversaryController {

    private final AnniversaryService anniversaryService;

    @PostMapping
    public Result<AnniversaryVO> create(@Valid @RequestBody AnniversaryCreateDTO dto) {
        return Result.success(anniversaryService.createAnniversary(UserContext.getUserId(), dto));
    }

    @PutMapping("/{anniversaryId}")
    public Result<AnniversaryVO> update(@PathVariable Long anniversaryId, @Valid @RequestBody AnniversaryUpdateDTO dto) {
        return Result.success(anniversaryService.updateAnniversary(UserContext.getUserId(), anniversaryId, dto));
    }

    @DeleteMapping("/{anniversaryId}")
    public Result<Void> delete(@PathVariable Long anniversaryId) {
        anniversaryService.deleteAnniversary(UserContext.getUserId(), anniversaryId);
        return Result.success();
    }

    @GetMapping
    public Result<List<AnniversaryVO>> list() {
        return Result.success(anniversaryService.listAnniversaries(UserContext.getUserId()));
    }

    @GetMapping("/summary")
    public Result<AnniversarySummaryVO> summary() {
        return Result.success(anniversaryService.getSummary(UserContext.getUserId()));
    }

    @GetMapping("/{anniversaryId}")
    public Result<AnniversaryVO> detail(@PathVariable Long anniversaryId) {
        return Result.success(anniversaryService.getAnniversaryDetail(UserContext.getUserId(), anniversaryId));
    }
}
