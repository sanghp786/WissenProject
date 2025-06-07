# 🌍 Holiday Calendar App

A full-stack web application that displays public holidays for selected countries using data from the [Nager.Date API](https://date.nager.at/).

## 📌 Features

- Select from 5 supported countries: 🇺🇸 US, 🇷🇺 RU, 🇩🇪 DE, 🇵🇱 PL, 🇺🇦 UA
- Toggle between **1-month** or **3-month** calendar views
- Navigate through months with next/previous buttons
- Visual highlights:
  - 🟥 Holidays marked in red with names shown
  - 🟩 Week with 1 holiday = light green
  - 🟩🟩 Week with >1 holidays = dark green

## ⚙️ Tech Stack

### Backend (Spring Boot)
- Java, Spring Boot
- REST API integration with Nager.Date
- CORS enabled for local frontend development

### Frontend (Vue.js + Vite)
- Vue 3 with JavaScript
- Axios for API calls
- Responsive calendar UI

## 🚀 Getting Started

### 📦 Backend Setup

1. Clone the backend repository or directory
2. Navigate to backend folder and run:

```bash
./mvnw spring-boot:run
