package com.example.demo.model;

public class Consultant {
    private String id;
    private String name;
    private Grade grade;

    public Consultant() {}

    public Consultant(String id, String name, Grade grade) {
        this.id = id;
        this.name = name;
        this.grade = grade;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }


    public long getDefaultTJM() {
        if (this.grade == null) return 0L;
        switch (this.grade) {
            case JUNIOR: return 75000L;
            case SENIOR: return 500000L;
            case MANAGER: return 1000000L;
            case PARTNER: return 2000000L;
            default: return 0L;
        }
    }
    
}
