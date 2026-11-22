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
 * The real SmsDataProvider: answers the SMS layer's questions from Member 2's repositories.
 * Registered in App.onCreate() with SmsHub.setProvider(...).
 */
public class BuddyDataProvider implements SmsDataProvider {

    private final Context ctx;

    public BuddyDataProvider(Context context) {
        this.ctx = context.getApplicationContext();
    }

    @Override
    public String getUserName() {
        return UserProfile.getName(ctx);
    }

    @Override
    public boolean isBuddyNumber(String number) {
        try {
            for (Buddy b : new BuddyRepository(ctx).load()) {
                if (SmsMessages.samePhone(b.getPhoneNumber(), number)) return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            // unreadable file: treat as "not a buddy"
        }
        return false;
    }

    @Override
    public String getProgressReply(String titleOrNull) {
        try {
            ArrayList<Assignment> all = new AssignmentRepository(ctx).load();
            String me = getUserName();

            if (titleOrNull != null) {
                for (Assignment a : all) {
                    if (a.getTitle().equalsIgnoreCase(titleOrNull)) {
                        return me + ": " + a.getTitle() + " " + a.getPercentComplete()
                                + "% (" + a.getStatus() + ")";
                    }
                }
                return null;   // receiver sends "No matching assignment found."
            }

            // no title: report up to 3 assignments that are not finished
            StringBuilder sb = new StringBuilder();
            int count = 0;
            for (Assignment a : all) {
                if ("Completed".equals(a.getStatus())) continue;
                if (count == 3) break;
                sb.append(count == 0 ? me + ": " : ", ")
                  .append(a.getTitle()).append(' ')
                  .append(a.getPercentComplete()).append('%');
                count++;
            }
            return count == 0 ? me + ": no active assignments" : sb.toString();

        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public String getLeaderboardReply() {
        try {
            ArrayList<ScoreRecord> board = new LeaderboardManager(ctx).getLeaderboard();
            if (board.isEmpty()) return "Leaderboard: no scores yet";

            StringBuilder sb = new StringBuilder("Leaderboard:");
            for (int i = 0; i < board.size() && i < 5; i++) {
                ScoreRecord r = board.get(i);
                sb.append(' ').append(i + 1).append(") ")
                  .append(r.getOwnerName()).append(' ').append(r.getTotalPoints());
            }
            return sb.toString();

        } catch (IOException | ClassNotFoundException e) {
            return "Leaderboard: unavailable";
        }
    }

    @Override
    public void onBuddyFinished(String senderNumber, String buddyName,
                                String title, int points, int total) {
        try {
            BuddyRepository buddyRepo = new BuddyRepository(ctx);
            ArrayList<Buddy> buddies = buddyRepo.load();

            Buddy match = null;
            for (Buddy b : buddies) {
                if (SmsMessages.samePhone(b.getPhoneNumber(), senderNumber)) {
                    match = b;
                    break;
                }
            }
            if (match == null) return;

            // The text only carries the new total, so count one more completed assignment.
            LeaderboardManager leaderboard = new LeaderboardManager(ctx);
            int completed = 1;
            for (ScoreRecord r : leaderboard.getLeaderboard()) {
                if (r.getOwnerId().equals(match.getPhoneNumber())) {
                    completed = r.getAssignmentsCompleted() + 1;
                    break;
                }
            }

            leaderboard.updateBuddyScore(match.getPhoneNumber(), match.getName(), total, completed);
            match.updateScore(total, completed);
            buddyRepo.save(buddies);

        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            // ignore: a bad message must never crash the receiver
        }
    }

    @Override
    public void onMessageLogged(boolean incoming, String number, String body) {
        try {
            new SmsLogRepository(ctx).add(
                    new SmsLogEntry(incoming, number, body, System.currentTimeMillis()));
        } catch (IOException | ClassNotFoundException e) {
            // logging is optional
        }
    }
}
