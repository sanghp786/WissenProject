import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api'; // Your backend URL

export function getHolidays(country) {
  return axios.get(`${API_BASE_URL}/holidays`, {
    params: { country }
  });
}