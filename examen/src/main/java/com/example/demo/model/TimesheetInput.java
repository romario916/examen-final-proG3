package com.example.demo.model;

import java.util.List;

public class TimesheetInput {
    private List<TimesheetEntry> entries;

    public TimesheetInput() {}

    public TimesheetInput(List<TimesheetEntry> entries) {
        this.entries = entries;
    }

    public List<TimesheetEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TimesheetEntry> entries) {
        this.entries = entries;
    }
}
