package com.example.comp.storage;

import android.content.Context;
import com.example.comp.model.ScoreRecord;

import java.io.IOException;
import java.util.ArrayList;

public class LeaderboardManager {

    private final ScoreRepository repository;

    public LeaderboardManager(Context context) {
        repository = new ScoreRepository(context);
    }

    public ArrayList<ScoreRecord> getLeaderboard()
            throws IOException, ClassNotFoundException {

        ArrayList<ScoreRecord> scores = repository.load();

        scores.sort((a, b) -> {
            int result = Integer.compare(
                    b.getTotalPoints(),
                    a.getTotalPoints()
            );

            if (result != 0) return result;

            return a.getOwnerName()
                    .compareToIgnoreCase(b.getOwnerName());
        });

        return scores;
    }

    public synchronized boolean awardAssignment(
            String ownerId,
            String ownerName,
            String assignmentId,
            int points,
            boolean onTime
    ) throws IOException, ClassNotFoundException {

        ArrayList<ScoreRecord> scores = repository.load();

        ScoreRecord target = null;

        for (ScoreRecord record : scores) {
            if (record.getOwnerId().equals(ownerId)) {
                target = record;
                break;
            }
        }

        if (target == null) {
            target = new ScoreRecord(ownerId, ownerName);
            scores.add(target);
        }

        boolean awarded = target.awardAssignmentOnce(
                assignmentId, points, onTime);

        if (awarded) {
            repository.save(scores);
        }

        return awarded;
    }

    public synchronized void updateBuddyScore(
            String ownerId,
            String ownerName,
            int totalPoints,
            int completed
    ) throws IOException, ClassNotFoundException {

        ArrayList<ScoreRecord> scores = repository.load();
        ScoreRecord target = null;

        for (ScoreRecord record : scores) {
            if (record.getOwnerId().equals(ownerId)) {
                target = record;
                break;
            }
        }

        if (target == null) {
            target = new ScoreRecord(ownerId, ownerName);
            scores.add(target);
        }

        target.updateTotals(totalPoints, completed);
        repository.save(scores);
    }
}