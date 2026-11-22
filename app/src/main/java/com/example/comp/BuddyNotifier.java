package com.example.comp;

import android.content.Context;

import com.example.comp.model.Assignment;
import com.example.comp.model.Buddy;
import com.example.comp.model.ScoreRecord;
import com.example.comp.model.SmsLogEntry;
import com.example.comp.storage.AssignmentRepository;
import com.example.comp.storage.BuddyRepository;
import com.example.comp.storage.LeaderboardManager;
import com.example.comp.storage.SmsLogRepository;

import java.io.IOException;
import java.util.ArrayList;

/**
 * What the screens call to text buddies. Member 3: call these from your buttons.
 *   start button          -> BuddyNotifier.notifyStarted(this, assignment)
 *   milestone ticked      -> BuddyNotifier.notifyMilestone(this, assignment)   (after updateProgress + save)
 *   finish button         -> BuddyNotifier.finishAndNotify(this, assignment)
 *   buddy screen buttons  -> requestProgress / requestLeaderboard / sendNudge
 * Each status method returns how many buddies were texted.
 */
public final class BuddyNotifier {

    /** Owner id of the current user's ScoreRecord on the leaderboard. */
    public static final String ME_ID = "me";

    private BuddyNotifier() {}

    // ---------- status texts to all buddies ----------

    public static int notifyStarted(Context ctx, Assignment a) {
        return broadcast(ctx, SmsMessages.started(UserProfile.getName(ctx), a.getTitle()));
    }

    public static int notifyMilestone(Context ctx, Assignment a) {
        return broadcast(ctx, SmsMessages.milestone(
                UserProfile.getName(ctx), a.getTitle(), a.getPercentComplete()));
    }

    public static int notifyFinished(Context ctx, Assignment a, int pointsAwarded, int newTotal) {
        return broadcast(ctx, SmsMessages.finished(
                UserProfile.getName(ctx), a.getTitle(), pointsAwarded, newTotal));
    }

    /**
     * Awards the assignment's points once (using Member 2's LeaderboardManager), marks it as awarded,
     * and texts the buddies. Call it when the assignment is complete.
     * Returns the points awarded, or -1 if it was already awarded or something failed.
     */
    public static int finishAndNotify(Context ctx, Assignment a) {
        try {
            String me = UserProfile.getName(ctx);
            boolean onTime = System.currentTimeMillis() <= a.getDueDate();
            int points = a.getPointsValue();   // Member 4: add the on-time bonus here if the group wants it

            LeaderboardManager leaderboard = new LeaderboardManager(ctx);
            if (!leaderboard.awardAssignment(ME_ID, me, a.getId(), points, onTime)) return -1;

            int total = points;
            for (ScoreRecord r : leaderboard.getLeaderboard()) {
                if (ME_ID.equals(r.getOwnerId())) total = r.getTotalPoints();
            }

            a.setPointsAwarded(true);
            new AssignmentRepository(ctx).update(a);

            notifyFinished(ctx, a, points, total);
            return points;

        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            return -1;
        }
    }

    // ---------- commands to one buddy ----------

    public static boolean requestProgress(Context ctx, String buddyNumber) {
        return sendLogged(ctx, buddyNumber, "PROGRESS");
    }

    public static boolean requestProgress(Context ctx, String buddyNumber, String title) {
        return sendLogged(ctx, buddyNumber, "PROGRESS " + title);
    }

    public static boolean requestLeaderboard(Context ctx, String buddyNumber) {
        return sendLogged(ctx, buddyNumber, "LEADERBOARD");
    }

    public static boolean sendNudge(Context ctx, String buddyNumber) {
        return sendLogged(ctx, buddyNumber, "NUDGE");
    }

    // ---------- helpers ----------

    private static int broadcast(Context ctx, String text) {
        int sent = 0;
        try {
            ArrayList<Buddy> buddies = new BuddyRepository(ctx).load();
            for (Buddy b : buddies) {
                if (sendLogged(ctx, b.getPhoneNumber(), text)) sent++;
            }
        } catch (IOException | ClassNotFoundException e) {
            // no buddies readable: nothing sent
        }
        return sent;
    }

    private static boolean sendLogged(Context ctx, String number, String text) {
        boolean ok = SmsSender.send(ctx, number, text);
        if (ok) {
            try {
                new SmsLogRepository(ctx).add(
                        new SmsLogEntry(false, number, text, System.currentTimeMillis()));
            } catch (IOException | ClassNotFoundException e) {
                // logging is optional
            }
        }
        return ok;
    }
}
