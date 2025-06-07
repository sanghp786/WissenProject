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
        String url = "https://date.nager.at/api/v3/PublicHolidays/" + year + "/" + country;

        List<Map<String, Object>> holidays = restTemplate.getForObject(url, List.class);

        return ResponseEntity.ok(holidays);
    }
}
