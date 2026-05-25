const typeLabelMap = {
  LOVE_DAY: '恋爱纪念日',
  BIRTHDAY: '生日',
  MEMORY_DAY: '特别回忆日',
  CUSTOM: '自定义纪念日'
}

const scopeLabelMap = {
  SELF: '只属于我',
  PARTNER: '为对方准备',
  BOTH: '属于我们'
}

const repeatLabelMap = {
  ONCE: '仅一次',
  YEARLY: '每年重复'
}

const calendarLabelMap = {
  SOLAR: '阳历',
  LUNAR: '农历'
}

const lunarMonthNames = ['正月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '冬月', '腊月']
const lunarDayNames = [
  '初一', '初二', '初三', '初四', '初五',
  '初六', '初七', '初八', '初九', '初十',
  '十一', '十二', '十三', '十四', '十五',
  '十六', '十七', '十八', '十九', '二十',
  '廿一', '廿二', '廿三', '廿四', '廿五',
  '廿六', '廿七', '廿八', '廿九', '三十'
]

export const anniversaryTypeOptions = [
  { label: '恋爱纪念日', value: 'LOVE_DAY' },
  { label: '生日', value: 'BIRTHDAY' },
  { label: '特别回忆日', value: 'MEMORY_DAY' },
  { label: '自定义纪念日', value: 'CUSTOM' }
]

export const anniversaryScopeOptions = [
  { label: '属于我们', value: 'BOTH' },
  { label: '只属于我', value: 'SELF' },
  { label: '为对方准备', value: 'PARTNER' }
]

export const anniversaryRepeatOptions = [
  { label: '每年重复', value: 'YEARLY' },
  { label: '仅一次', value: 'ONCE' }
]

export const anniversaryReminderOptions = [
  { label: '当天提醒', value: 0 },
  { label: '提前 1 天', value: 1 },
  { label: '提前 3 天', value: 3 },
  { label: '提前 7 天', value: 7 }
]

export const anniversaryCalendarOptions = [
  { label: '阳历', value: 'SOLAR' },
  { label: '农历', value: 'LUNAR' }
]

export const lunarMonthOptions = Array.from({ length: 12 }, (_, index) => ({
  label: lunarMonthNames[index],
  value: index + 1
}))

export const lunarDayOptions = Array.from({ length: 30 }, (_, index) => ({
  label: lunarDayNames[index],
  value: index + 1
}))

export function buildLunarYearOptions(baseYear = new Date().getFullYear(), total = 40) {
  return Array.from({ length: total }, (_, index) => {
    const year = baseYear - 15 + index
    return {
      label: `${year} 年`,
      value: year
    }
  })
}

export function getLunarMonthLabel(month, isLeapMonth = false) {
  if (!month || month < 1 || month > 12) {
    return '--'
  }
  return `${isLeapMonth ? '闰' : ''}${lunarMonthNames[month - 1]}`
}

export function getLunarDayLabel(day) {
  if (!day || day < 1 || day > 30) {
    return '--'
  }
  return lunarDayNames[day - 1]
}

export function formatLunarSelection({ lunarYear, lunarMonth, lunarDay, lunarLeapMonth }) {
  if (!lunarYear || !lunarMonth || !lunarDay) {
    return '尚未选择完整的农历日期'
  }
  return `${lunarYear} 年 · ${getLunarMonthLabel(lunarMonth, lunarLeapMonth)}${getLunarDayLabel(lunarDay)}`
}

export function getAnniversaryTypeLabel(type) {
  return typeLabelMap[type] || '纪念日'
}

export function getAnniversaryScopeLabel(scope) {
  return scopeLabelMap[scope] || '属于你们'
}

export function getAnniversaryRepeatLabel(repeatType) {
  return repeatLabelMap[repeatType] || '每年重复'
}

export function getAnniversaryCalendarLabel(calendarType) {
  return calendarLabelMap[calendarType] || '阳历'
}

export function formatAnniversaryDate(date) {
  if (!date) {
    return '--'
  }
  if (typeof date === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(date)) {
    const [year, month, day] = date.split('-').map(Number)
    return new Intl.DateTimeFormat('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    }).format(new Date(year, month - 1, day))
  }
  const value = new Date(date)
  if (Number.isNaN(value.getTime())) {
    return date
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  }).format(value)
}

export function formatAnniversaryDisplay(item, withCalendar = true) {
  if (!item) {
    return '--'
  }
  const base = formatAnniversaryDate(item.nextOccurrenceDate || item.anniversaryDate)
  if (item.calendarLabel) {
    return withCalendar ? `${base} · ${item.calendarLabel}` : base
  }
  const calendarText = getAnniversaryCalendarLabel(item.calendarType)
  return withCalendar ? `${base} · ${calendarText}` : base
}

export function formatDaysText(days) {
  if (days == null) {
    return '已过期'
  }
  if (days === 0) {
    return '就是今天'
  }
  return `还有 ${days} 天`
}

export function getAnniversaryHeroText(item) {
  if (!item) {
    return '把重要的日期认真留住，往后每次翻开都会更有意义。'
  }
  if (item.today) {
    return `${item.title} 就在今天，记得留下一张新的照片或一句新的话。`
  }
  if (item.daysUntilNext == null) {
    return `${item.title} 已经走进回忆里，仍然值得被好好记住。`
  }
  return `${item.title} ${formatDaysText(item.daysUntilNext)}，刚好适合提前准备一点心意。`
}
