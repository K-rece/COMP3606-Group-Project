package com.example.comp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comp.model.Buddy;
import com.example.comp.storage.BuddyRepository;

import java.io.IOException;
import java.util.ArrayList;

public class BuddiesActivity extends AppCompatActivity {
    private EditText buddyName, buddyPhone;
    private final ArrayList<Buddy> buddies = new ArrayList<>();
    private final ArrayList<String> labels = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private BuddyRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_buddies);

        repo = new BuddyRepository(this);
        buddyName = findViewById(R.id.buddyName);
        buddyPhone = findViewById(R.id.buddyPhone);
        Button add = findViewById(R.id.addBuddyButton);
        ListView list = findViewById(R.id.buddyList);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels);
        list.setAdapter(adapter);

        add.setOnClickListener(v -> addBuddy());

        list.setOnItemLongClickListener((parent, view, position, id) -> {
            try {
                repo.remove(buddies.get(position).getPhoneNumber());
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(this, "Could not remove buddy", Toast.LENGTH_SHORT).show();
            }
            reload();
            return true;
        });

        reload();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void addBuddy() {
        String name = buddyName.getText().toString().trim();
        String phone = buddyPhone.getText().toString().trim();
        if (name.isEmpty()) { buddyName.setError("Enter a name"); return; }
        if (phone.replaceAll("[^0-9]", "").length() < 4) {
            buddyPhone.setError("Enter a valid number");
            return;
        }
        try {
            repo.add(new Buddy(name, phone));
            buddyName.setText("");
            buddyPhone.setText("");
        } catch (IllegalArgumentException e) {
            buddyPhone.setError("Buddy already exists");
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(this, "Could not save buddy", Toast.LENGTH_SHORT).show();
        }
        reload();
    }

    private void reload() {
        buddies.clear();
        labels.clear();
        try {
            buddies.addAll(repo.load());
        } catch (IOException | ClassNotFoundException e) {
            // empty list
        }
        for (Buddy b : buddies) labels.add(b.getName() + "  -  " + b.getPhoneNumber());
        adapter.notifyDataSetChanged();
    }
}