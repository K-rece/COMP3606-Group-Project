package com.example.comp;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.comp.model.*;
import com.example.comp.storage.*;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class Member2StorageTest {

    @Test
    public void testAssignmentStorage() throws Exception {

        Context context = InstrumentationRegistry
                .getInstrumentation().getTargetContext();

        AssignmentRepository repo =
                new AssignmentRepository(context);

        Assignment assignment = new Assignment(
                "COMP3606 Project",
                "COMP3606",
                System.currentTimeMillis(),
                50
        );

        Milestone first = new Milestone("Introduction");
        Milestone second = new Milestone("SMS System");

        first.setDone(true);

        assignment.addMilestone(first);
        assignment.addMilestone(second);

        ArrayList<Assignment> data = new ArrayList<>();
        data.add(assignment);

        repo.save(data);

        // Use a new repository instance to reload.
        AssignmentRepository reopened =
                new AssignmentRepository(context);

        ArrayList<Assignment> loaded = reopened.load();

        assertEquals(1, loaded.size());
        assertEquals("COMP3606 Project",
                loaded.get(0).getTitle());
        assertEquals(50,
                loaded.get(0).getPercentComplete());
    }

    @Test
    public void testBuddyStorage() throws Exception {

        Context context = InstrumentationRegistry
                .getInstrumentation().getTargetContext();

        BuddyRepository repo = new BuddyRepository(context);

        ArrayList<Buddy> buddies = new ArrayList<>();
        buddies.add(new Buddy("Sarah", "5556"));

        repo.save(buddies);

        BuddyRepository reopened =
                new BuddyRepository(context);

        assertEquals("Sarah",
                reopened.load().get(0).getName());
    }

    @Test
    public void testLeaderboard() throws Exception {

        Context context = InstrumentationRegistry
                .getInstrumentation().getTargetContext();

        LeaderboardManager manager =
                new LeaderboardManager(context);

        ScoreRepository repo = new ScoreRepository(context);
        repo.save(new ArrayList<>());

        manager.awardAssignment(
                "SELF", "You", "task-1", 50, true);

        manager.awardAssignment(
                "SELF", "You", "task-1", 50, true);

        manager.updateBuddyScore(
                "5556", "Sarah", 100, 2);

        ArrayList<ScoreRecord> scores =
                manager.getLeaderboard();

        assertEquals("Sarah",
                scores.get(0).getOwnerName());

        assertEquals(50,
                scores.get(1).getTotalPoints());
    }
}