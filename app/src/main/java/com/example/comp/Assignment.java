package com.example.comp;

import java.io.Serializable;

public class Assignment implements Serializable {
    private String title;
    private String course;

    private long dueMillis;
    private int progressCount;
    private int percentComplete = 0;
    private String status = "NOT STARTED";

    public Assignment(String title, String course, long dueMillis, int progressCount) {
        this.title = title;
        this.course = course;
        this.dueMillis = dueMillis;
        this.progressCount = progressCount;
    }

    public String getTitle() { return title; }
    public String getCourse() { return course; }
    public long getDueMillis() { return dueMillis; }
    public int getProgressCount() { return progressCount; }
    public int getPercentComplete() { return percentComplete; }
    public void setPercentComplete(int p) { percentComplete = p; }
    public String getStatus() { return status; }
    public void setStatus(String s) { status = s; }

    @Override
    public String toString() {
        return title + " (" + course + ")";
    }
}
