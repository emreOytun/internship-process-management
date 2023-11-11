package com.teamaloha.internshipprocessmanagement.controller;

import com.teamaloha.internshipprocessmanagement.dto.authentication.HolidayAddRequest;
import com.teamaloha.internshipprocessmanagement.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    public void addHoliday(@RequestBody HolidayAddRequest holidayAddRequest) {
        holidayService.addHoliday(holidayAddRequest);
    }
}
