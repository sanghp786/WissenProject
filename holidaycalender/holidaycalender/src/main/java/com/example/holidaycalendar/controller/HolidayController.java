package com.example.holidaycalendar.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class HolidayController {

    private final RestTemplate restTemplate;

    public HolidayController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/holidays")
    public ResponseEntity<List<Map<String, Object>>> getHolidays(@RequestParam String country) {
        int year = LocalDate.now().getYear();
        String apiKey = "duG4MS7qGIylqGrOdVMUf2VwxrqksSUL"; // Replace with your actual Calendarific API key

        String url = "https://calendarific.com/api/v2/holidays"
                + "?api_key=" + apiKey
                + "&country=" + country
                + "&year=" + year;

        // 1. First get the full JSON response as a Map
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // 2. Navigate to the 'holidays' list inside 'response'
        Map<String, Object> inner = (Map<String, Object>) response.get("response");
        List<Map<String, Object>> holidays = (List<Map<String, Object>>) inner.get("holidays");

        return ResponseEntity.ok(holidays);
    }

}
