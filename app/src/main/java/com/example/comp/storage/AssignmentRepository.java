package com.example.comp.storage;

import android.content.Context;
import com.example.comp.model.Assignment;

import java.io.IOException;
import java.util.ArrayList;

public class AssignmentRepository {

    private final FileStorage storage;
    private static final String FILE = "assignments.dat";

    public AssignmentRepository(Context context) {
        storage = new FileStorage(context);
    }

    public ArrayList<Assignment> load()
            throws IOException, ClassNotFoundException {
        return storage.loadList(FILE, Assignment.class);
    }

    public void save(ArrayList<Assignment> assignments)
            throws IOException {
        storage.saveList(FILE, assignments);
    }

    public synchronized void add(Assignment assignment)
            throws IOException, ClassNotFoundException {

        ArrayList<Assignment> assignments = load();
        assignments.add(assignment);
        save(assignments);
    }

    public synchronized void update(Assignment updated)
            throws IOException, ClassNotFoundException {

        ArrayList<Assignment> assignments = load();

        for (int i = 0; i < assignments.size(); i++) {
            if (assignments.get(i).getId()
                    .equals(updated.getId())) {

                assignments.set(i, updated);
                save(assignments);
                return;
            }
        }

        throw new IllegalArgumentException(
                "Assignment not found");
    }

    public synchronized void delete(String id)
            throws IOException, ClassNotFoundException {

        ArrayList<Assignment> assignments = load();

        assignments.removeIf(
                item -> item.getId().equals(id)
        );

        save(assignments);
    }
}