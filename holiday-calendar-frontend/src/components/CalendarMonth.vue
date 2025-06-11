<template>
  <div class="calendar-month">
    <div class="month-title">{{ monthNames[month] }} {{ year }}</div>

    <div class="weekdays">
      <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
    </div>

    <div class="dates">
      <div
        v-for="(week, wIndex) in weeks"
        :key="'week-' + wIndex"
        class="week-row"
      >
        <div
          v-for="(day, dIndex) in week"
          :key="'day-' + wIndex + '-' + dIndex"
          :class="day ? dateClass(day) : 'date empty'"
        >
          <div v-if="day" class="date-number">{{ day }}</div>
          <div v-if="day && holidayNamesForDate(day).length" class="holidays">
            <span
              v-for="(h, i) in holidayNamesForDate(day)"
              :key="i"
              class="holiday-name"
            >
              {{ h }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CalendarMonth',
  props: {
    year: Number,
    month: Number,
    holidays: Array,
    showOnlyHolidayWeeks: Boolean
  },

  data() {
    return {
      weekdays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      monthNames: [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
      ]
    }
  },

  computed: {
    daysInMonth() {
      return new Date(this.year, this.month + 1, 0).getDate()
    },

    firstDayOfMonth() {
      return new Date(this.year, this.month, 1).getDay()
    },

    weeks() {
      const days = []
      for (let i = 0; i < this.firstDayOfMonth; i++) days.push(null)
      for (let d = 1; d <= this.daysInMonth; d++) days.push(d)
      while (days.length % 7 !== 0) days.push(null)

      const result = []
      for (let i = 0; i < days.length; i += 7) {
        const week = days.slice(i, i + 7)
        const hasHoliday = week.some(day => {
          if (!day) return false
          const dateStr = this.formatDate(this.year, this.month + 1, day)
          return this.holidays.some(h => h.date.iso === dateStr)
        })
        if (this.showOnlyHolidayWeeks) {
          if (hasHoliday) result.push(week)
        } else {
          result.push(week)
        }
      }

      return result
    },

    holidayWeeks() {
      const map = new Map()
      this.holidays.forEach(h => {
        const date = new Date(h.date.iso)
        if (date.getFullYear() === this.year && date.getMonth() === this.month) {
          const weekNum = this.getWeekNumber(date)
          if (!map.has(weekNum)) map.set(weekNum, [])
          map.get(weekNum).push(h)
        }
      })
      return map
    }
  },

  methods: {
    formatDate(year, month, day) {
      return `${year}-${month.toString().padStart(2, '0')}-${day
        .toString()
        .padStart(2, '0')}`
    },

    getWeekNumber(date) {
  const startOfYear = new Date(date.getFullYear(), 0, 1);
  const daysSinceStart = Math.floor((date - startOfYear) / (24 * 60 * 60 * 1000));
  const startDay = startOfYear.getDay(); // Sunday = 0

  // Adjust days since start to align week with Sunday as start
  const adjustedDays = daysSinceStart + startDay;

  return Math.floor(adjustedDays / 7) + 1;
},

    holidayNamesForDate(day) {
      const dateStr = this.formatDate(this.year, this.month + 1, day)
      return this.holidays
        .filter(h => h.date.iso === dateStr)
        .map(h => h.name)
    },

    dateClass(day) {
      const dateStr = this.formatDate(this.year, this.month + 1, day)
      const isHoliday = this.holidays.some(h => h.date.iso === dateStr)
      const weekNum = this.getWeekNumber(new Date(this.year, this.month, day))
	  const holidaysInWeek = this.holidayWeeks.get(weekNum) || [];
const distinctHolidayDates = new Set(holidaysInWeek.map(h => h.date.iso));
const count = distinctHolidayDates.size;


      return {
        date: true,
        'holiday-day': isHoliday,
        'week-light-green': count === 1,
        'week-dark-green': count > 1,
        blurred: this.showOnlyHolidayWeeks && count === 0
      }
    }
  }
}
</script>

<style scoped>

.calendar-month {
  max-width: 280px; /* increased size */
  border: 1px solid #ccc;
  margin-bottom: 15px;
}

.month-title {
  text-align: center;
  font-weight: bold;
  padding: 5px 0;
  background-color: #f0f0f0;
}

.weekdays {
  display: flex;
  background-color: #eee;
}

.weekday,
.date {
  width: calc(100% / 7);
  height: 70px; /* increased height */
  width: 50px;
  border: 1px solid #ddd;
  box-sizing: border-box;
  padding: 3px;
  position: relative;
  background-color: white;
  overflow: hidden;
}

.weekday {
  padding: 8px 0;
  font-weight: bold;
  font-size: 0.9em;
  border: 1px solid #ccc;
  background-color: #eee;
}

.dates {
  display: flex;
  flex-direction: column;
}

.date.empty {
  background-color: #f8f8f8;
}

.date.holiday-day .date-number {
  color: red;
  font-weight: bold;
}

.week-light-green {
  background-color: #b2d8b2; /* light green */
}

.week-dark-green {
  background-color: #4c974c; /* dark green */
  color: white;
}

.week-row {
  display: flex;
}

.date-number {
  font-size: 0.85em;
  font-weight: bold;
  margin-bottom: 4px;
}

.holidays {
  font-size: 0.65em;
  margin-top: 5px;
  max-height: 50px;
  overflow: hidden;
  white-space: normal;
}

.holiday-name {
  display: block;
  color: red;
  white-space: normal; /* allow wrapping */
  word-break: break-word; /* break long words if needed */
  line-height: 1.1;
}


</style>
