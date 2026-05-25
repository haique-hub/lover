package com.lovers.controller;

import com.lovers.common.Result;
import com.lovers.context.UserContext;
import com.lovers.dto.CoupleBindDTO;
import com.lovers.service.CoupleService;
import com.lovers.vo.CoupleInfoVO;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/couple")
@RequiredArgsConstructor
public class CoupleController {

    private final CoupleService coupleService;

    @PostMapping("/code")
    public Result<Map<String, String>> generateCode() {
        String bindCode = coupleService.generateBindCode(UserContext.getUserId());
        return Result.success(Map.of("bindCode", bindCode));
    }

    @PostMapping("/bind")
    public Result<CoupleInfoVO> bind(@Valid @RequestBody CoupleBindDTO dto) {
        return Result.success(coupleService.bindByCode(UserContext.getUserId(), dto.getBindCode()));
    }

    @GetMapping("/info")
    public Result<CoupleInfoVO> info() {
        return Result.success(coupleService.getCoupleInfo(UserContext.getUserId()));
    }

    @PostMapping("/unbind")
    public Result<Void> unbind() {
        coupleService.unbind(UserContext.getUserId());
        return Result.success();
    }
}
