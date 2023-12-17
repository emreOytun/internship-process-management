package com.teamaloha.internshipprocessmanagement.jobs;

import com.teamaloha.internshipprocessmanagement.service.InternshipProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InternshipProcessJobs {
    private final InternshipProcessService internshipProcessService;

    @Autowired
    public InternshipProcessJobs(InternshipProcessService internshipProcessService) {
        this.internshipProcessService = internshipProcessService;
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs every day at 12:00 AM
    public void checkReportEditLastDates() {
        internshipProcessService.checkReportEditLastDates();
    }

    @Scheduled(cron = "0 0 0 * * *") // Runs every day at 12:00 AM
    public void finishInternshipProcesses() {
        internshipProcessService.finishInternshipProcesses();
    }
}
