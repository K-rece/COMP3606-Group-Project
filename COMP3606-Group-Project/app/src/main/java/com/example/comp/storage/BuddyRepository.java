package com.example.comp.storage;

import android.content.Context;
import com.example.comp.model.Buddy;

import java.io.IOException;
import java.util.ArrayList;

public class BuddyRepository {

    private final FileStorage storage;
    private static final String FILE = "buddies.dat";

    public BuddyRepository(Context context) {
        storage = new FileStorage(context);
    }

    public ArrayList<Buddy> load()
            throws IOException, ClassNotFoundException {
        return storage.loadList(FILE, Buddy.class);
    }

    public void save(ArrayList<Buddy> buddies)
            throws IOException {
        storage.saveList(FILE, buddies);
    }

    public synchronized void add(Buddy buddy)
            throws IOException, ClassNotFoundException {

        ArrayList<Buddy> buddies = load();

        for (Buddy existing : buddies) {
            if (existing.getPhoneNumber()
                    .equals(buddy.getPhoneNumber())) {
                throw new IllegalArgumentException(
                        "Buddy already exists");
            }
        }

        buddies.add(buddy);
        save(buddies);
    }

    public synchronized void remove(String phoneNumber)
            throws IOException, ClassNotFoundException {

        ArrayList<Buddy> buddies = load();

        buddies.removeIf(
                buddy -> buddy.getPhoneNumber()
                        .equals(phoneNumber)
        );

        save(buddies);
    }
}