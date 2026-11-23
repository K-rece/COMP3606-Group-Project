package com.example.comp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String id;
    private String title;
    private String course;
    private long dueDate;
    private ArrayList<Milestone> milestones;
    private int percentComplete;
    private String status;
    private int pointsValue;
    private boolean pointsAwarded;
    private boolean started = false;

    public Assignment(String title, String course,
                      long dueDate, int pointsValue) {

        if (pointsValue < 0) {
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        }

        id = UUID.randomUUID().toString();
        this.title = title;
        this.course = course;
        this.dueDate = dueDate;
        this.pointsValue = pointsValue;
        milestones = new ArrayList<>();
        percentComplete = 0;
        status = "Not Started";
        pointsAwarded = false;
    }

    public void addMilestone(Milestone milestone) {
        milestones.add(milestone);
        updateProgress();
    }

    public void markStarted() {
        started = true;
        updateProgress();
    }

    public void updateProgress() {
        if (milestones.isEmpty()) {
            percentComplete = 0;
            status = "Not Started";
            return;
        }

        int finished = 0;

        for (Milestone m : milestones) {
            if (m.isDone()) finished++;
        }

        percentComplete =
                finished * 100 / milestones.size();

        if (finished == milestones.size()) {
            status = "Completed";
        } else if (finished > 0 || started) {
            status = "In Progress";
        } else {
            status = "Not Started";
        }
    }

    public String getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse() { return course; }
    public void setCourse(String course) {
        this.course = course;
    }

    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) {
        this.dueDate = dueDate;
    }

    public ArrayList<Milestone> getMilestones() {
        return milestones;
    }

    public int getPercentComplete() {
        return percentComplete;
    }

    public String getStatus() { return status; }

    public int getPointsValue() {
        return pointsValue;
    }

    public void setPointsValue(int pointsValue) {
        if (pointsValue < 0)
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        this.pointsValue = pointsValue;
    }

    public boolean isPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(boolean awarded) {
        pointsAwarded = awarded;
    }

    @Override
    public String toString() {
        return title + " (" + course + ")";
    }
}