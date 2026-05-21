package com.example.demo.model;

public class InvoiceCreationRequest {
    private String missionId;
    private String period; // Format YYYY-MM

    public InvoiceCreationRequest() {}

    public InvoiceCreationRequest(String missionId, String period) {
        this.missionId = missionId;
        this.period = period;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    
}
