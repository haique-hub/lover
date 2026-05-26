package com.lovers.service.impl;

import cn.hutool.core.date.ChineseDate;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lovers.dto.AnniversaryCreateDTO;
import com.lovers.dto.AnniversaryUpdateDTO;
import com.lovers.entity.Anniversary;
import com.lovers.entity.Photo;
import com.lovers.entity.User;
import com.lovers.exception.BusinessException;
import com.lovers.mapper.AnniversaryMapper;
import com.lovers.mapper.PhotoMapper;
import com.lovers.service.AnniversaryService;
import com.lovers.service.CoupleService;
import com.lovers.service.PhotoService;
import com.lovers.service.UserService;
import com.lovers.vo.AnniversarySummaryVO;
import com.lovers.vo.AnniversaryVO;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnniversaryServiceImpl implements AnniversaryService {

    private static final Set<String> ALLOWED_TYPES = Set.of("LOVE_DAY", "BIRTHDAY", "MEMORY_DAY", "CUSTOM");
    private static final Set<String> ALLOWED_CALENDAR_TYPES = Set.of("SOLAR", "LUNAR");
    private static final Set<String> ALLOWED_SCOPES = Set.of("SELF", "PARTNER", "BOTH");
    private static final Set<String> ALLOWED_REPEAT_TYPES = Set.of("ONCE", "YEARLY");

    private final AnniversaryMapper anniversaryMapper;
    private final PhotoMapper photoMapper;
    private final CoupleService coupleService;
    private final PhotoService photoService;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnniversaryVO createAnniversary(Long userId, AnniversaryCreateDTO dto) {
        validatePayload(dto, userId);
        Anniversary anniversary = new Anniversary();
        applyPayload(anniversary, dto);
        anniversary.setUserId(userId);
        anniversaryMapper.insert(anniversary);
        return toAnniversaryVO(anniversary, userService.getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AnniversaryVO updateAnniversary(Long userId, Long anniversaryId, AnniversaryUpdateDTO dto) {
        validatePayload(dto, userId);
        Anniversary anniversary = getByIdOrThrow(anniversaryId);
        if (!anniversary.getUserId().equals(userId)) {
            throw new BusinessException("只有创建者可以编辑纪念日");
        }
        applyPayload(anniversary, dto);
        anniversaryMapper.updateById(anniversary);
        return toAnniversaryVO(anniversary, userService.getByIdOrThrow(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAnniversary(Long userId, Long anniversaryId) {
        Anniversary anniversary = getByIdOrThrow(anniversaryId);
        if (!anniversary.getUserId().equals(userId)) {
            throw new BusinessException("只有创建者可以删除纪念日");
        }
        anniversaryMapper.deleteById(anniversaryId);
    }

    @Override
    public List<AnniversaryVO> listAnniversaries(Long userId) {
        Set<Long> accessibleUserIds = coupleService.listAccessibleUserIds(userId);
        List<Anniversary> anniversaries = anniversaryMapper.selectList(
            Wrappers.<Anniversary>lambdaQuery()
                .in(Anniversary::getUserId, accessibleUserIds)
                .orderByDesc(Anniversary::getCreateTime)
        );
        Map<Long, User> userMap = userService.getUserMap(
            anniversaries.stream().map(Anniversary::getUserId).collect(Collectors.toSet())
        );
        return anniversaries.stream()
            .map(anniversary -> toAnniversaryVO(anniversary, userMap.get(anniversary.getUserId())))
            .sorted(anniversaryComparator())
            .toList();
    }

    @Override
    public AnniversaryVO getAnniversaryDetail(Long userId, Long anniversaryId) {
        Anniversary anniversary = getByIdOrThrow(anniversaryId);
        if (!coupleService.canAccessUser(userId, anniversary.getUserId())) {
            throw new BusinessException(403, "无权查看该纪念日");
        }
        return toAnniversaryVO(anniversary, userService.getByIdOrThrow(anniversary.getUserId()));
    }

    @Override
    public AnniversarySummaryVO getSummary(Long userId) {
        List<AnniversaryVO> anniversaries = listAnniversaries(userId);
        List<AnniversaryVO> upcomingAll = anniversaries.stream()
            .filter(item -> !Boolean.TRUE.equals(item.getExpired()))
            .filter(item -> item.getDaysUntilNext() != null)
            .toList();
        List<AnniversaryVO> upcoming = upcomingAll.stream()
            .limit(3)
            .toList();
        AnniversarySummaryVO summary = new AnniversarySummaryVO();
        summary.setTotalCount(anniversaries.size());
        summary.setTodayCount((int) anniversaries.stream().filter(item -> Boolean.TRUE.equals(item.getToday())).count());
        summary.setUpcomingCount(upcomingAll.size());
        summary.setNextAnniversary(upcoming.isEmpty() ? null : upcoming.getFirst());
        summary.setUpcomingAnniversaries(upcoming);
        return summary;
    }

    @Override
    public Anniversary getByIdOrThrow(Long anniversaryId) {
        Anniversary anniversary = anniversaryMapper.selectById(anniversaryId);
        if (anniversary == null) {
            throw new BusinessException("纪念日不存在");
        }
        return anniversary;
    }

    private void validatePayload(AnniversaryCreateDTO dto, Long userId) {
        if (!ALLOWED_TYPES.contains(dto.getType())) {
            throw new BusinessException("不支持的纪念日类型");
        }
        if (!ALLOWED_CALENDAR_TYPES.contains(dto.getCalendarType())) {
            throw new BusinessException("不支持的日期类型");
        }
        if (!ALLOWED_SCOPES.contains(dto.getRelationScope())) {
            throw new BusinessException("不支持的纪念日归属");
        }
        if (!ALLOWED_REPEAT_TYPES.contains(dto.getRepeatType())) {
            throw new BusinessException("不支持的重复方式");
        }
        if ("SOLAR".equals(dto.getCalendarType())) {
            if (dto.getAnniversaryDate() == null) {
                throw new BusinessException("请选择阳历日期");
            }
            if (dto.getAnniversaryDate().isAfter(LocalDate.now().plusYears(50))) {
                throw new BusinessException("纪念日日期超出合理范围");
            }
        } else {
            if (dto.getLunarYear() == null || dto.getLunarMonth() == null || dto.getLunarDay() == null) {
                throw new BusinessException("请选择完整的农历日期");
            }
            if (dto.getLunarMonth() < 1 || dto.getLunarMonth() > 12) {
                throw new BusinessException("农历月份不合法");
            }
            if (dto.getLunarDay() < 1 || dto.getLunarDay() > 30) {
                throw new BusinessException("农历日期不合法");
            }
            try {
                toGregorianDate(dto.getLunarYear(), dto.getLunarMonth(), dto.getLunarDay(), Boolean.TRUE.equals(dto.getLunarLeapMonth()));
            } catch (Exception exception) {
                throw new BusinessException("所选农历日期不存在，请重新选择");
            }
        }
        if (dto.getCoverPhotoId() != null) {
            photoService.verifyPhotoAccessible(userId, dto.getCoverPhotoId());
        }
    }

    private void applyPayload(Anniversary anniversary, AnniversaryCreateDTO dto) {
        anniversary.setTitle(dto.getTitle().trim());
        anniversary.setType(dto.getType());
        anniversary.setCalendarType(dto.getCalendarType());
        if ("LUNAR".equals(dto.getCalendarType())) {
            LocalDate gregorianDate = toGregorianDate(
                dto.getLunarYear(),
                dto.getLunarMonth(),
                dto.getLunarDay(),
                Boolean.TRUE.equals(dto.getLunarLeapMonth())
            );
            anniversary.setAnniversaryDate(gregorianDate);
            anniversary.setLunarYear(dto.getLunarYear());
            anniversary.setLunarMonth(dto.getLunarMonth());
            anniversary.setLunarDay(dto.getLunarDay());
            anniversary.setLunarLeapMonth(Boolean.TRUE.equals(dto.getLunarLeapMonth()));
        } else {
            ChineseDate chineseDate = new ChineseDate(dto.getAnniversaryDate());
            anniversary.setAnniversaryDate(dto.getAnniversaryDate());
            anniversary.setLunarYear(chineseDate.getChineseYear());
            anniversary.setLunarMonth(chineseDate.getMonth());
            anniversary.setLunarDay(chineseDate.getDay());
            anniversary.setLunarLeapMonth(chineseDate.isLeapMonth());
        }
        anniversary.setRelationScope(dto.getRelationScope());
        anniversary.setRepeatType(dto.getRepeatType());
        anniversary.setReminderDays(dto.getReminderDays());
        anniversary.setCoverPhotoId(dto.getCoverPhotoId());
        String description = dto.getDescription() == null ? null : dto.getDescription().trim();
        anniversary.setDescription(description == null || description.isEmpty() ? null : description);
    }

    private AnniversaryVO toAnniversaryVO(Anniversary anniversary, User owner) {
        LocalDate today = LocalDate.now();
        LocalDate nextOccurrenceDate = resolveNextOccurrenceDate(
            anniversary.getAnniversaryDate(),
            anniversary.getLunarMonth(),
            anniversary.getLunarDay(),
            anniversary.getLunarLeapMonth(),
            anniversary.getCalendarType(),
            anniversary.getRepeatType(),
            today
        );
        boolean expired = nextOccurrenceDate == null;
        AnniversaryVO vo = new AnniversaryVO();
        BeanUtils.copyProperties(anniversary, vo);
        if (owner != null) {
            vo.setOwnerNickname(owner.getNickname());
            vo.setOwnerAvatar(owner.getAvatar());
        }
        if (anniversary.getCoverPhotoId() != null) {
            Photo coverPhoto = photoMapper.selectById(anniversary.getCoverPhotoId());
            if (coverPhoto != null) {
                vo.setCoverPhotoUrl(coverPhoto.getFileUrl());
            }
        }
        vo.setCalendarLabel(buildCalendarLabel(anniversary));
        vo.setNextOccurrenceDate(nextOccurrenceDate);
        vo.setExpired(expired);
        vo.setToday(nextOccurrenceDate != null && ChronoUnit.DAYS.between(today, nextOccurrenceDate) == 0);
        vo.setDaysUntilNext(nextOccurrenceDate == null ? null : ChronoUnit.DAYS.between(today, nextOccurrenceDate));
        long daysSinceStart = ChronoUnit.DAYS.between(anniversary.getAnniversaryDate(), today);
        vo.setDaysSinceStart(daysSinceStart < 0 ? null : daysSinceStart);
        return vo;
    }

    private LocalDate resolveNextOccurrenceDate(LocalDate anniversaryDate, String repeatType, LocalDate today) {
        return resolveNextOccurrenceDate(anniversaryDate, null, null, null, "SOLAR", repeatType, today);
    }

    private LocalDate resolveNextOccurrenceDate(
        LocalDate anniversaryDate,
        Integer lunarMonth,
        Integer lunarDay,
        Boolean lunarLeapMonth,
        String calendarType,
        String repeatType,
        LocalDate today
    ) {
        if ("ONCE".equals(repeatType)) {
            return anniversaryDate.isBefore(today) ? null : anniversaryDate;
        }
        if ("LUNAR".equals(calendarType) && lunarMonth != null && lunarDay != null) {
            LocalDate candidate = resolveNextLunarOccurrence(today.getYear(), lunarMonth, lunarDay, Boolean.TRUE.equals(lunarLeapMonth), today);
            if (candidate == null) {
                return anniversaryDate;
            }
            if (candidate.isBefore(today)) {
                return resolveNextLunarOccurrence(today.getYear() + 1, lunarMonth, lunarDay, Boolean.TRUE.equals(lunarLeapMonth), today);
            }
            return candidate;
        }
        LocalDate candidate = alignYear(anniversaryDate, today.getYear());
        if (candidate.isBefore(today)) {
            candidate = alignYear(anniversaryDate, today.getYear() + 1);
        }
        return candidate;
    }

    private LocalDate alignYear(LocalDate source, int year) {
        try {
            return source.withYear(year);
        } catch (DateTimeException exception) {
            return LocalDate.of(year, source.getMonth(), YearMonth.of(year, source.getMonth()).lengthOfMonth());
        }
    }

    private Comparator<AnniversaryVO> anniversaryComparator() {
        return Comparator
            .comparing((AnniversaryVO item) -> item.getDaysUntilNext() == null ? Long.MAX_VALUE : item.getDaysUntilNext())
            .thenComparing(item -> item.getNextOccurrenceDate() == null ? LocalDate.MAX : item.getNextOccurrenceDate())
            .thenComparing(AnniversaryVO::getCreateTime, Comparator.reverseOrder());
    }

    private LocalDate resolveNextLunarOccurrence(int startYear, int lunarMonth, int lunarDay, boolean leapMonth, LocalDate today) {
        for (int year = startYear; year <= startYear + 200; year++) {
            try {
                LocalDate candidate = toGregorianDate(year, lunarMonth, lunarDay, leapMonth);
                if (!candidate.isBefore(today)) {
                    return candidate;
                }
            } catch (Exception ignored) {
                // Some leap-month lunar dates do not exist every year; keep searching forward until one exists.
            }
        }
        return null;
    }

    private String buildCalendarLabel(Anniversary anniversary) {
        if (!"LUNAR".equals(anniversary.getCalendarType())) {
            return "阳历";
        }
        ChineseDate chineseDate = new ChineseDate(
            anniversary.getLunarYear() == null ? anniversary.getAnniversaryDate().getYear() : anniversary.getLunarYear(),
            anniversary.getLunarMonth(),
            anniversary.getLunarDay(),
            Boolean.TRUE.equals(anniversary.getLunarLeapMonth())
        );
        return "农历 " + chineseDate.getChineseMonthName() + chineseDate.getChineseDay();
    }

    private LocalDate toGregorianDate(int lunarYear, int lunarMonth, int lunarDay, boolean leapMonth) {
        return new ChineseDate(lunarYear, lunarMonth, lunarDay, leapMonth).getGregorianDate()
            .toInstant()
            .atZone(java.time.ZoneId.systemDefault())
            .toLocalDate();
    }
}
