<template>
  <div class="calendar-app">
    <h1>Holiday Calendar</h1>

    <!-- Country selector -->
    <div class="controls">
      <label for="country">Select Country:</label>
      <select id="country" v-model="selectedCountry" @change="fetchHolidays">
        <option value="">-- Choose --</option>
        <option value="RU">Russia</option>
        <option value="DE">Germany</option>
        <option value="PL">Poland</option>
        <option value="UA">Ukraine</option>
        <option value="IN">India</option>
      </select>

      <label class="switch" v-if="selectedCountry">
  <input type="checkbox" v-model="showOnlyHolidayWeeks" />
  <span class="slider"></span>
  <span class="switch-label">{{ showOnlyHolidayWeeks ? 'Only Holiday Weeks' : 'All Weeks' }}</span>
</label>

    </div>

    <!-- View mode toggle -->
    <div class="view-mode-toggle" v-if="selectedCountry">
      <label><input type="radio" value="1" v-model="viewMode" /> 1 Month</label>
      <label><input type="radio" value="3" v-model="viewMode" /> 3 Months</label>
    </div>

    <!-- Month navigation -->
    <div class="month-navigation" v-if="selectedCountry">
      <button @click="prevMonth">← Previous</button>
      <span>
        {{ monthNames[currentMonth] }} {{ currentYear }}
        <template v-if="viewMode === '3'">
          - {{ monthNames[nextMonths[1].month] }} {{ nextMonths[1].year }} -
          {{ monthNames[nextMonths[2].month] }} {{ nextMonths[2].year }}
        </template>
      </span>
      <button @click="nextMonth">Next →</button>

      <p v-if="nextHolidayMessage" style="margin-top: 10px; font-weight: bold;">
        {{ nextHolidayMessage }}
      </p>
    </div>

    <!-- Loading indicator -->
    <div v-if="loading">Loading holidays...</div>

    <!-- Calendar -->
    <div v-if="!loading && selectedCountry" class="calendar-multi">
      <div v-if="viewMode === '1'">
        <CalendarMonth
          :year="currentYear"
          :month="currentMonth"
          :holidays="holidays"
          :showOnlyHolidayWeeks="showOnlyHolidayWeeks"
        />
      </div>

      <div v-else class="three-months-container">
        <CalendarMonth
          v-for="(m, idx) in nextMonths"
          :key="idx"
          :year="m.year"
          :month="m.month"
          :holidays="holidays"
          :showOnlyHolidayWeeks="showOnlyHolidayWeeks"
        />
      </div>
    </div>
  </div>
</template>

<script>
import CalendarMonth from './components/CalendarMonth.vue'
import { getHolidays } from './api/holidayApi'

export default {
  components: { CalendarMonth },

  data() {
    return {
      selectedCountry: '',
      holidays: [],
      loading: false,
      currentYear: new Date().getFullYear(),
      currentMonth: new Date().getMonth(),
      viewMode: '1',
      showOnlyHolidayWeeks: false,
      nextHolidayMessage: '',
      monthNames: [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
      ]
    }
  },

  computed: {
    nextMonths() {
      const months = []
      for (let i = 0; i < 3; i++) {
        let year = this.currentYear
        let month = this.currentMonth + i
        if (month > 11) {
          year += 1
          month = month % 12
        }
        months.push({ year, month })
      }
      return months
    }
  },

  methods: {
    async fetchHolidays() {
      if (!this.selectedCountry) return
      this.loading = true
      try {
        const response = await getHolidays(this.selectedCountry)
        this.holidays = response.data
        this.calculateNextHoliday()
      } catch (err) {
        console.error('Error fetching holidays', err)
        this.holidays = []
        this.nextHolidayMessage = ''
      } finally {
        this.loading = false
      }
    },

    calculateNextHoliday() {
      const today = new Date()
      const todayStr = today.toISOString().split('T')[0]
      const upcoming = this.holidays
        .filter(h => h.date.iso >= todayStr)
        .sort((a, b) => new Date(a.date.iso) - new Date(b.date.iso))

      if (upcoming.length > 0) {
        const next = new Date(upcoming[0].date.iso)
        const diff = Math.ceil((next - today) / (1000 * 60 * 60 * 24))
        this.nextHolidayMessage = `🎉 Next holiday "${upcoming[0].name}" is in ${diff} day(s)!`
      } else {
        this.nextHolidayMessage = 'No more holidays this year.'
      }
    },

    prevMonth() {
      if (this.currentMonth === 0) {
        this.currentMonth = 11
        this.currentYear--
      } else {
        this.currentMonth--
      }
    },

    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentMonth = 0
        this.currentYear++
      } else {
        this.currentMonth++
      }
    }
  }
}
</script>

<style scoped>
.controls {
  margin-bottom: 15px;
}

.view-mode-toggle {
  margin: 10px 0;
}

.month-navigation {
  margin: 10px 0;
}

.calendar-multi {
  margin-top: 20px;
}

.three-months-container {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 10px;
}
</style>
