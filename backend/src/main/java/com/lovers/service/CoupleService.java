package com.lovers.service;

import com.lovers.vo.CoupleInfoVO;
import java.util.Set;

public interface CoupleService {

    String generateBindCode(Long userId);

    CoupleInfoVO bindByCode(Long userId, String bindCode);

    CoupleInfoVO getCoupleInfo(Long userId);

    void unbind(Long userId);

    Long getPartnerUserId(Long userId);

    Set<Long> listAccessibleUserIds(Long userId);

    boolean canAccessUser(Long currentUserId, Long targetUserId);
}
