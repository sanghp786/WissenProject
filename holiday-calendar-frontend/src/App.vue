<template>
  <div class="calendar-app">
    <h1>Holiday Calendar</h1>

    <!-- Country selector -->
    <div class="controls">
      <label for="country">Select Country:</label>
      <select id="country" v-model="selectedCountry" @change="fetchHolidays">
        <option value="">-- Choose --</option>
        <option value="US">United States</option>
        <option value="RU">Russia</option>
        <option value="DE">Germany</option>
        <option value="PL">Poland</option>
        <option value="UA">Ukraine</option>
      </select>
    </div>

    <!-- View mode toggle -->
    <div class="view-mode-toggle" v-if="selectedCountry">
      <label>
        <input type="radio" value="1" v-model="viewMode" /> 1 Month
      </label>
      <label>
        <input type="radio" value="3" v-model="viewMode" /> 3 Months
      </label>
    </div>

    <!-- Month navigation -->
    <div class="month-navigation" v-if="selectedCountry">
      <button @click="prevMonth">← Previous</button>
      <span>
        {{ monthNames[currentMonth] }} {{ currentYear }}
        <template v-if="viewMode === '3'">
          - {{ monthNames[nextMonths[1].month] }} {{ nextMonths[1].year }} - {{ monthNames[nextMonths[2].month] }} {{ nextMonths[2].year }}
        </template>
      </span>
      <button @click="nextMonth">Next →</button>
    </div>

    <!-- Loading indicator -->
    <div v-if="loading">Loading holidays...</div>

    <!-- Calendar -->
    <div v-if="!loading && selectedCountry" class="calendar-multi">
      <!-- For 1 month view, show one month calendar -->
      <div v-if="viewMode === '1'">
        <CalendarMonth
          :year="currentYear"
          :month="currentMonth"
          :holidays="holidays"
        />
      </div>

      <!-- For 3 months view, show 3 months side by side -->
      <div v-else class="three-months-container">
        <CalendarMonth
          v-for="(m, idx) in nextMonths"
          :key="idx"
          :year="m.year"
          :month="m.month"
          :holidays="holidays"
        />
      </div>
    </div>
  </div>
</template>

<script>
// Import the month calendar component (we will create it next)
import CalendarMonth from './components/CalendarMonth.vue';

import { getHolidays } from './api/holidayApi';

export default {
  components: { CalendarMonth },

  data() {
    return {
      selectedCountry: '',
      holidays: [],
      loading: false,
      currentYear: new Date().getFullYear(),
      currentMonth: new Date().getMonth(), // 0-based
      monthNames: [
        'January', 'February', 'March', 'April', 'May', 'June',
        'July', 'August', 'September', 'October', 'November', 'December'
      ],
      viewMode: '1', // '1' or '3' months
    };
  },

  computed: {
    // Calculate the next 3 months for 3-month view
    nextMonths() {
      let months = [];
      for (let i = 0; i < 3; i++) {
        let year = this.currentYear;
        let month = this.currentMonth + i;
        if (month > 11) {
          year += 1;
          month = month % 12;
        }
        months.push({ year, month });
      }
      return months;
    }
  },

  methods: {
    async fetchHolidays() {
      if (!this.selectedCountry) {
        this.holidays = [];
        return;
      }
      this.loading = true;
      try {
        const response = await getHolidays(this.selectedCountry);
        this.holidays = response.data;
      } catch (error) {
        console.error('Error fetching holidays:', error);
        this.holidays = [];
      } finally {
        this.loading = false;
      }
    },

    prevMonth() {
      if (this.currentMonth === 0) {
        this.currentMonth = 11;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
    },

    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentMonth = 0;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
    }
  }
};
</script>

<style scoped>
/* ... Your existing styles ... */

.three-months-container {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 10px;
}
</style>
