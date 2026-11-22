package com.example.comp.model;

import java.io.Serializable;
import java.util.HashSet;

public class ScoreRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String ownerId;
    private String ownerName;
    private int totalPoints;
    private int assignmentsCompleted;
    private int onTimeCount;

    private HashSet<String> rewardedAssignmentIds;

    public ScoreRecord(String ownerId, String ownerName) {
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        totalPoints = 0;
        assignmentsCompleted = 0;
        onTimeCount = 0;
        rewardedAssignmentIds = new HashSet<>();
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getAssignmentsCompleted() {
        return assignmentsCompleted;
    }

    public int getOnTimeCount() {
        return onTimeCount;
    }

    public boolean awardAssignmentOnce(
            String assignmentId,
            int points,
            boolean onTime) {

        if (points < 0) {
            throw new IllegalArgumentException(
                    "Points cannot be negative");
        }

        if (!rewardedAssignmentIds.add(assignmentId)) {
            return false;
        }

        totalPoints += points;
        assignmentsCompleted++;

        if (onTime) onTimeCount++;

        return true;
    }

    public void updateTotals(int totalPoints,
                             int assignmentsCompleted) {

        if (totalPoints < 0 || assignmentsCompleted < 0) {
            throw new IllegalArgumentException(
                    "Invalid score");
        }

        this.totalPoints = totalPoints;
        this.assignmentsCompleted = assignmentsCompleted;
    }
}