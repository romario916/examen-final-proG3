package com.example.demo.model;

import java.time.LocalDate;

public class AssignmentInput {
    private int plannedDays;
    private long negotiatedDailyRate;
    private LocalDate startDate;
    private LocalDate endDate;

    public AssignmentInput() {}

    public AssignmentInput(int plannedDays, long negotiatedDailyRate, LocalDate startDate, LocalDate endDate) {
        this.plannedDays = plannedDays;
        this.negotiatedDailyRate = negotiatedDailyRate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getPlannedDays() {
        return plannedDays;
    }

    public void setPlannedDays(int plannedDays) {
        this.plannedDays = plannedDays;
    }

    public long getNegotiatedDailyRate() {
        return negotiatedDailyRate;
    }

    public void setNegotiatedDailyRate(long negotiatedDailyRate) {
        this.negotiatedDailyRate = negotiatedDailyRate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    
}
