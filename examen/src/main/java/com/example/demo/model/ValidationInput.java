package com.example.demo.model;

public class ValidationInput {
    private String outcome; // VALIDATED ou REJECTED
    private String comment;

    public ValidationInput() {}

    public ValidationInput(String outcome, String comment) {
        this.outcome = outcome;
        this.comment = comment;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    
}
