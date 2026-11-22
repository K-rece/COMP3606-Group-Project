package com.example.comp.storage;

import android.content.Context;
import com.example.comp.model.ScoreRecord;

import java.io.IOException;
import java.util.ArrayList;

public class ScoreRepository {

    private final FileStorage storage;
    private static final String FILE = "scores.dat";

    public ScoreRepository(Context context) {
        storage = new FileStorage(context);
    }

    public ArrayList<ScoreRecord> load()
            throws IOException, ClassNotFoundException {
        return storage.loadList(FILE, ScoreRecord.class);
    }

    public void save(ArrayList<ScoreRecord> scores)
            throws IOException {
        storage.saveList(FILE, scores);
    }

    public synchronized void saveOrUpdate(ScoreRecord record)
            throws IOException, ClassNotFoundException {

        ArrayList<ScoreRecord> scores = load();

        for (int i = 0; i < scores.size(); i++) {
            if (scores.get(i).getOwnerId()
                    .equals(record.getOwnerId())) {

                scores.set(i, record);
                save(scores);
                return;
            }
        }

        scores.add(record);
        save(scores);
    }
}