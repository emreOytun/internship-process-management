package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.holiday.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.dto.holiday.IsValidRangeRequest;
import com.teamaloha.internshipprocessmanagement.service.HolidayService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/holiday")
public class HolidayController {

    private final HolidayService holidayService;

    @Autowired
    public HolidayController(HolidayService holidayService) {
        this.holidayService = holidayService;
    }

    @GetMapping("/isHolidayExistsByDate")
    @ResponseStatus(HttpStatus.OK)
    public boolean isHolidayExistsByDate(@RequestBody String date) {
        return holidayService.isHolidayExistsByDate(date);
    }

    @PostMapping("/addHoliday")
    @ResponseStatus(HttpStatus.OK)
    public void addHoliday(@RequestBody @Valid HolidayAddRequest holidayAddRequest) {
        holidayService.addHoliday(holidayAddRequest);
    }

    @GetMapping("/isGivenWorkDayTrue")
    @ResponseStatus(HttpStatus.OK)
    public boolean isGivenWorkDayTrue(@RequestBody IsValidRangeRequest dates) {
        return holidayService.isGivenWorkDayTrue(dates);
    }
}
