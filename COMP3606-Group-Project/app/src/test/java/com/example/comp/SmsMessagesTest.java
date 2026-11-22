package com.example.comp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SmsMessagesTest {

    @Test
    public void finishedMessageRoundTrips() {
        String text = SmsMessages.finished("Sam", "COMP3606 Project", 50, 180);
        assertEquals("Sam finished COMP3606 Project! +50 pts. Total: 180.", text);
        SmsMessages.Parsed p = SmsMessages.parse(text);
        assertEquals(SmsMessages.Type.FINISHED, p.type);
        assertEquals("Sam", p.name);
        assertEquals("COMP3606 Project", p.title);
        assertEquals(50, p.points);
        assertEquals(180, p.total);
    }

    @Test
    public void commandsAreCaseInsensitive() {
        assertEquals(SmsMessages.Type.PROGRESS, SmsMessages.parse("progress").type);
        assertEquals(SmsMessages.Type.NUDGE, SmsMessages.parse("  Nudge ").type);
        assertEquals(SmsMessages.Type.LEADERBOARD, SmsMessages.parse("LEADERBOARD").type);
        assertEquals(SmsMessages.Type.HELP, SmsMessages.parse("help").type);
    }

    @Test
    public void progressWithTitleKeepsTitleCase() {
        SmsMessages.Parsed p = SmsMessages.parse("PROGRESS COMP3606 Project");
        assertEquals(SmsMessages.Type.PROGRESS, p.type);
        assertEquals("COMP3606 Project", p.arg);
    }

    @Test
    public void statusTextsAndRepliesAreNotCommands() {
        assertEquals(SmsMessages.Type.OTHER, SmsMessages.parse(SmsMessages.started("Sam", "Essay")).type);
        assertEquals(SmsMessages.Type.OTHER, SmsMessages.parse(SmsMessages.milestone("Sam", "Essay", 60)).type);
        assertEquals(SmsMessages.Type.OTHER, SmsMessages.parse(SmsMessages.HELP_REPLY).type);
        assertEquals(SmsMessages.Type.OTHER, SmsMessages.parse("Leaderboard: 1) Sam 120").type);
        assertEquals(SmsMessages.Type.OTHER, SmsMessages.parse(null).type);
    }

    @Test
    public void samePhoneHandlesEmulatorAndCountryCodes() {
        assertTrue(SmsMessages.samePhone("5554", "+15555215554"));
        assertTrue(SmsMessages.samePhone("868-345-6789", "+18683456789"));
        assertFalse(SmsMessages.samePhone("5554", "5556"));
        assertFalse(SmsMessages.samePhone("", "5554"));
    }
}
