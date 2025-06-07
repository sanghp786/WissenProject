<template>
  <div class="calendar-month">
    <div class="month-title">{{ monthNames[month] }} {{ year }}</div>

    <div class="weekdays">
      <div v-for="day in weekdays" :key="day" class="weekday">{{ day }}</div>
    </div>

    <div class="dates">
      <!-- empty slots before first day -->
      <div
        v-for="n in firstDayOfMonth"
        :key="'empty-' + n"
        class="date empty"
      ></div>

      <!-- actual dates -->
      <div
        v-for="date in daysInMonth"
        :key="date"
        :class="dateClass(date)"
      >
        <div class="date-number">{{ date }}</div>
        <div v-if="holidayNamesForDate(date).length > 0" class="holidays">
          <span
            v-for="(h, i) in holidayNamesForDate(date)"
            :key="i"
            class="holiday-name"
            >{{ h }}</span
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CalendarMonth',
  props: {
    year: { type: Number, required: true },
    month: { type: Number, required: true }, // 0 based
    holidays: { type: Array, required: true }
  },
  data() {
    return {
      weekdays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      monthNames: [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
      ]
    };
  },
  computed: {
    daysInMonth() {
      return new Date(this.year, this.month + 1, 0).getDate();
    },
    firstDayOfMonth() {
      return new Date(this.year, this.month, 1).getDay();
    },
    holidayWeeks() {
      const map = new Map();

      this.holidays.forEach(holiday => {
        const date = new Date(holiday.date);
        // Only count holidays for this month & year
        if (date.getFullYear() === this.year && date.getMonth() === this.month) {
          const weekNum = this.getWeekNumber(date);
          if (!map.has(weekNum)) {
            map.set(weekNum, []);
          }
          map.get(weekNum).push(holiday);
        }
      });

      return map;
    }
  },
  methods: {
    getWeekNumber(date) {
      const d = new Date(date.getTime());
      d.setHours(0, 0, 0, 0);
      d.setDate(d.getDate() + 3 - ((d.getDay() + 6) % 7));
      const week1 = new Date(d.getFullYear(), 0, 4);
      return (
        1 +
        Math.round(
          ((d.getTime() - week1.getTime()) / 86400000 - 3 + ((week1.getDay() + 6) % 7)) /
            7
        )
      );
    },
    holidayNamesForDate(date) {
      const dateStr = this.formatDate(this.year, this.month + 1, date);
      return this.holidays
        .filter(h => h.date === dateStr)
        .map(h => h.localName);
    },
    formatDate(year, month, day) {
      const m = month < 10 ? '0' + month : month;
      const d = day < 10 ? '0' + day : day;
      return `${year}-${m}-${d}`;
    },
    dateClass(date) {
      const dateStr = this.formatDate(this.year, this.month + 1, date);
      const holidayCount = this.holidays.filter(h => h.date === dateStr).length;

      const weekNum = this.getWeekNumber(new Date(this.year, this.month, date));
      const weekHolidayCount = this.holidayWeeks.get(weekNum)?.length || 0;

      return {
        date: true,
        holiday: holidayCount > 0,
        'holiday-day': holidayCount > 0,
        'week-light-green': weekHolidayCount === 1,
        'week-dark-green': weekHolidayCount > 1,
      };
    }
  }
};
</script>

<style scoped>
.calendar-month {
  max-width: 220px;
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

.weekday {
  flex: 1;
  padding: 8px 0;
  text-align: center;
  font-weight: bold;
  font-size: 0.9em;
}

.dates {
  display: flex;
  flex-wrap: wrap;
}

.date {
  width: calc(100% / 7);
  height: 70px;
  border: 1px solid #ddd;
  box-sizing: border-box;
  padding: 3px;
  background-color: white;
  color: black;
  position: relative;
}

.date.empty {
  border: none;
  background: none;
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

.holidays {
  font-size: 0.65em;
  margin-top: 5px;
  overflow: hidden;
  height: 40px;
  overflow-y: auto;
}

.holiday-name {
  display: block;
  color: red;
}
</style>
