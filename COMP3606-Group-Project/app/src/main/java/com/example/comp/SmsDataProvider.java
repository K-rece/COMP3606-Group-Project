package com.example.comp;

/**
 * The contract between the SMS layer (Member 1) and the data layer (Member 2).
 * SmsReceiver never touches storage directly; it asks this interface.
 * Member 2: implement this on top of your DataManager and register it with SmsHub.setProvider(...).
 */
public interface SmsDataProvider {

    /** The current user's name (the one entered on the first screen). */
    String getUserName();

    /** True if this sender number belongs to a saved buddy. Use SmsMessages.samePhone(...) to compare. */
    boolean isBuddyNumber(String number);

    /**
     * Text for a PROGRESS reply, for the given assignment title, or for the current assignment(s) when
     * titleOrNull is null. Return null if nothing matches. Must NOT start with "PROGRESS".
     * Example: "Sam: COMP3606 Project 60% (due in 2d 5h)".
     */
    String getProgressReply(String titleOrNull);

    /** Text for a LEADERBOARD reply, e.g. LeaderboardManager.toSmsText(...). Must start with "Leaderboard:". */
    String getLeaderboardReply();

    /** A buddy finished an assignment: update that buddy's ScoreRecord to 'total' points. */
    void onBuddyFinished(String senderNumber, String buddyName, String title, int points, int total);

    /** Optional: called for every accepted incoming or outgoing message (for the Message Log screen). */
    default void onMessageLogged(boolean incoming, String number, String body) {}

    /** Optional: called when a buddy sends NUDGE. */
    default void onNudge(String senderNumber) {}
}
