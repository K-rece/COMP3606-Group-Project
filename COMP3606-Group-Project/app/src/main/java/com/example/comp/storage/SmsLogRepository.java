package com.example.comp.storage;

import android.content.Context;
import com.example.comp.model.SmsLogEntry;

import java.io.IOException;
import java.util.ArrayList;

/** Saves the SMS history to a file (same style as the other repositories). Keeps the newest 200. */
public class SmsLogRepository {

    private static final String FILE = "sms_log.dat";
    private static final int MAX_ENTRIES = 200;

    private final FileStorage storage;

    public SmsLogRepository(Context context) {
        storage = new FileStorage(context);
    }

    public synchronized ArrayList<SmsLogEntry> load()
            throws IOException, ClassNotFoundException {
        return storage.loadList(FILE, SmsLogEntry.class);
    }

    public synchronized void add(SmsLogEntry entry)
            throws IOException, ClassNotFoundException {

        ArrayList<SmsLogEntry> entries = load();
        entries.add(entry);

        while (entries.size() > MAX_ENTRIES) {
            entries.remove(0);
        }

        storage.saveList(FILE, entries);
    }

    public synchronized void clear() throws IOException {
        storage.saveList(FILE, new ArrayList<SmsLogEntry>());
    }
}
