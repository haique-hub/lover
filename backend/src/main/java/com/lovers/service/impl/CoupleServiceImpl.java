package com.lovers.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.entity.CoupleRelation;
import com.lovers.entity.User;
import com.lovers.enums.CoupleStatusEnum;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.CoupleRelationMapper;
import com.lovers.service.CoupleService;
import com.lovers.service.UserService;
import com.lovers.vo.CoupleInfoVO;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CoupleServiceImpl implements CoupleService {

    private final CoupleRelationMapper coupleRelationMapper;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generateBindCode(Long userId) {
        if (getActiveRelation(userId) != null) {
            throw new BusinessException("你已经绑定了情侣，无法重复生成绑定码");
        }
        CoupleRelation pendingRelation = getPendingRelationByUserId(userId);
        String bindCode = generateUniqueCode();
        if (pendingRelation != null) {
            pendingRelation.setBindCode(bindCode);
            coupleRelationMapper.updateById(pendingRelation);
            return bindCode;
        }
        CoupleRelation relation = new CoupleRelation();
        relation.setUserId(userId);
        relation.setBindCode(bindCode);
        relation.setStatus(CoupleStatusEnum.PENDING.getCode());
        coupleRelationMapper.insert(relation);
        return bindCode;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CoupleInfoVO bindByCode(Long userId, String bindCode) {
        if (getActiveRelation(userId) != null) {
            throw new BusinessException("请先解除当前绑定状态后再操作");
        }
        CoupleRelation selfPendingRelation = getPendingRelationByUserId(userId);
        CoupleRelation relation = coupleRelationMapper.selectOne(
            Wrappers.<CoupleRelation>lambdaQuery()
                .eq(CoupleRelation::getBindCode, bindCode)
                .eq(CoupleRelation::getStatus, CoupleStatusEnum.PENDING.getCode())
        );
        if (relation == null) {
            throw new BusinessException("绑定码无效或已失效");
        }
        if (relation.getUserId().equals(userId)) {
            throw new BusinessException("不能绑定自己生成的绑定码");
        }
        if (getActiveRelation(relation.getUserId()) != null) {
            throw new BusinessException("发起方已存在有效情侣关系");
        }
        if (selfPendingRelation != null) {
            selfPendingRelation.setStatus(CoupleStatusEnum.UNBOUND.getCode());
            selfPendingRelation.setBindCode(null);
            selfPendingRelation.setUnbindTime(LocalDateTime.now());
            coupleRelationMapper.updateById(selfPendingRelation);
        }
        relation.setPartnerId(userId);
        relation.setStatus(CoupleStatusEnum.ACTIVE.getCode());
        relation.setBindTime(LocalDateTime.now());
        coupleRelationMapper.updateById(relation);
        return getCoupleInfo(userId);
    }

    @Override
    public CoupleInfoVO getCoupleInfo(Long userId) {
        CoupleInfoVO vo = new CoupleInfoVO();
        User self = userService.getByIdOrThrow(userId);
        vo.setSelf(userService.toUserInfoVO(self));
        CoupleRelation activeRelation = getActiveRelation(userId);
        if (activeRelation != null) {
            Long partnerId = activeRelation.getUserId().equals(userId) ? activeRelation.getPartnerId() : activeRelation.getUserId();
            vo.setBound(Boolean.TRUE);
            vo.setBindTime(activeRelation.getBindTime());
            vo.setPartner(userService.toUserInfoVO(userService.getByIdOrThrow(partnerId)));
            return vo;
        }
        CoupleRelation pendingRelation = getPendingRelationByUserId(userId);
        vo.setBound(Boolean.FALSE);
        if (pendingRelation != null) {
            vo.setBindCode(pendingRelation.getBindCode());
        }
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(Long userId) {
        CoupleRelation relation = getActiveRelation(userId);
        if (relation == null) {
            throw new BusinessException("当前没有有效的情侣关系");
        }
        relation.setStatus(CoupleStatusEnum.UNBOUND.getCode());
        relation.setUnbindTime(LocalDateTime.now());
        relation.setBindCode(null);
        coupleRelationMapper.updateById(relation);
    }

    @Override
    public Long getPartnerUserId(Long userId) {
        CoupleRelation relation = getActiveRelation(userId);
        if (relation == null) {
            return null;
        }
        return relation.getUserId().equals(userId) ? relation.getPartnerId() : relation.getUserId();
    }

    @Override
    public Set<Long> listAccessibleUserIds(Long userId) {
        Set<Long> userIds = new LinkedHashSet<>();
        userIds.add(userId);
        Long partnerUserId = getPartnerUserId(userId);
        if (partnerUserId != null) {
            userIds.add(partnerUserId);
        }
        return userIds;
    }

    @Override
    public boolean canAccessUser(Long currentUserId, Long targetUserId) {
        return listAccessibleUserIds(currentUserId).contains(targetUserId);
    }

    private CoupleRelation getActiveRelation(Long userId) {
        return coupleRelationMapper.selectOne(
            Wrappers.<CoupleRelation>lambdaQuery()
                .eq(CoupleRelation::getStatus, CoupleStatusEnum.ACTIVE.getCode())
                .and(wrapper -> wrapper.eq(CoupleRelation::getUserId, userId).or().eq(CoupleRelation::getPartnerId, userId))
        );
    }

    private CoupleRelation getPendingRelationByUserId(Long userId) {
        return coupleRelationMapper.selectOne(
            Wrappers.<CoupleRelation>lambdaQuery()
                .eq(CoupleRelation::getUserId, userId)
                .eq(CoupleRelation::getStatus, CoupleStatusEnum.PENDING.getCode())
        );
    }

    private String generateUniqueCode() {
        String bindCode;
        do {
            bindCode = RandomUtil.randomStringUpper(8);
        } while (coupleRelationMapper.selectCount(
            Wrappers.<CoupleRelation>lambdaQuery().eq(CoupleRelation::getBindCode, bindCode)
        ) > 0);
        return bindCode;
    }
}
