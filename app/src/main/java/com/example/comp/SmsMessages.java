package com.example.comp;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * All SMS text formats in one place: builders for outgoing messages and a parser for incoming ones.
 * Pure Java (no Android classes) so it can be unit tested on the computer.
 */
public final class SmsMessages {
    private SmsMessages() {}

    public static final String HELP_REPLY =
            "Commands: PROGRESS, PROGRESS <title>, NUDGE, LEADERBOARD, HELP";

    // ---------- outgoing (sent automatically to buddies) ----------

    public static String started(String user, String title) {
        return user + " started: " + title + ".";
    }

    public static String milestone(String user, String title, int percent) {
        return user + " is " + percent + "% done with " + title + ".";
    }

    /** The only status message another phone parses, so keep the format fixed. */
    public static String finished(String user, String title, int points, int total) {
        return user + " finished " + title + "! +" + points + " pts. Total: " + total + ".";
    }

    // ---------- incoming ----------

    public enum Type { PROGRESS, NUDGE, LEADERBOARD, HELP, FINISHED, OTHER }

    public static final class Parsed {
        public final Type type;
        public final String arg;        // PROGRESS <title>: the title, otherwise null
        public final String name;       // FINISHED: buddy name
        public final String title;      // FINISHED: assignment title
        public final int points;        // FINISHED
        public final int total;         // FINISHED

        Parsed(Type type, String arg, String name, String title, int points, int total) {
            this.type = type;
            this.arg = arg;
            this.name = name;
            this.title = title;
            this.points = points;
            this.total = total;
        }
    }

    private static final Pattern FINISHED = Pattern.compile(
            "^(.+?) finished (.+)! \\+(\\d+) pts\\. Total: (\\d+)\\.$", Pattern.DOTALL);

    public static Parsed parse(String body) {
        if (body == null) return new Parsed(Type.OTHER, null, null, null, 0, 0);
        String text = body.trim();
        String upper = text.toUpperCase(Locale.ROOT);

        if (upper.equals("PROGRESS")) return new Parsed(Type.PROGRESS, null, null, null, 0, 0);
        if (upper.startsWith("PROGRESS ")) {
            String arg = text.substring("PROGRESS ".length()).trim();
            return new Parsed(Type.PROGRESS, arg.isEmpty() ? null : arg, null, null, 0, 0);
        }
        if (upper.equals("NUDGE")) return new Parsed(Type.NUDGE, null, null, null, 0, 0);
        if (upper.equals("LEADERBOARD")) return new Parsed(Type.LEADERBOARD, null, null, null, 0, 0);
        if (upper.equals("HELP")) return new Parsed(Type.HELP, null, null, null, 0, 0);

        Matcher m = FINISHED.matcher(text);
        if (m.matches()) {
            try {
                return new Parsed(Type.FINISHED, null, m.group(1), m.group(2),
                        Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
            } catch (NumberFormatException e) {
                // fall through to OTHER
            }
        }
        return new Parsed(Type.OTHER, null, null, null, 0, 0);
    }

    // ---------- phone number matching ----------

    /**
     * Emulator numbers are short (5554) but the sender may arrive as +15555215554, and real numbers
     * may or may not include the country code, so compare the last digits only.
     */
    public static boolean samePhone(String a, String b) {
        String da = digits(a);
        String db = digits(b);
        if (da.isEmpty() || db.isEmpty()) return false;
        int n = Math.min(Math.min(da.length(), db.length()), 10);
        return da.regionMatches(da.length() - n, db, db.length() - n, n);
    }

    private static String digits(String s) {
        if (s == null) return "";
        return s.replaceAll("[^0-9]", "");
    }
}
