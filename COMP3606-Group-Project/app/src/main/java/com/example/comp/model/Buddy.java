package com.example.comp.model;

import java.io.Serializable;

public class Buddy implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String phoneNumber;
    private int totalPoints;
    private int assignmentsCompleted;

    public Buddy(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.totalPoints = 0;
        this.assignmentsCompleted = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public void updateScore(int points, int completed) {
        this.totalPoints = points;
        this.assignmentsCompleted = completed;
    }
}