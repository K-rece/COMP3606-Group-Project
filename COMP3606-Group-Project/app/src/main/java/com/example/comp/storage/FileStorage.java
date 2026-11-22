package com.example.comp.storage;

import android.content.Context;
import android.util.AtomicFile;

import java.io.*;
import java.util.ArrayList;

public class FileStorage {

    private final Context context;

    public FileStorage(Context context) {
        this.context = context.getApplicationContext();
    }

    public synchronized <T extends Serializable> void saveList(
            String filename, ArrayList<T> items)
            throws IOException {

        AtomicFile file = new AtomicFile(
                new File(context.getFilesDir(), filename)
        );

        FileOutputStream output = file.startWrite();

        try {
            ObjectOutputStream objectOutput =
                    new ObjectOutputStream(output);

            objectOutput.writeObject(items);
            objectOutput.flush();

            file.finishWrite(output);
        } catch (IOException | RuntimeException e) {
            file.failWrite(output);
            throw e;
        }
    }

    public synchronized <T> ArrayList<T> loadList(
            String filename, Class<T> type)
            throws IOException, ClassNotFoundException {

        AtomicFile file = new AtomicFile(
                new File(context.getFilesDir(), filename)
        );

        if (!file.getBaseFile().exists()) {
            return new ArrayList<>();
        }

        try (InputStream input = file.openRead();
             ObjectInputStream objectInput =
                     new ObjectInputStream(input)) {

            Object result = objectInput.readObject();

            if (!(result instanceof ArrayList<?>)) {
                throw new IOException("Invalid file contents");
            }

            ArrayList<T> loaded = new ArrayList<>();

            for (Object item : (ArrayList<?>) result) {
                if (!type.isInstance(item)) {
                    throw new IOException(
                            "Unexpected object in file");
                }

                loaded.add(type.cast(item));
            }

            return loaded;
        }
    }
}
