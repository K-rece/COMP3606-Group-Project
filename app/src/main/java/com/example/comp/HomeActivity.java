package com.example.comp;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.comp.model.Assignment;
import com.example.comp.model.Milestone;
import com.example.comp.storage.AssignmentRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private Button addAssignment, startButton, tickButton, finishButton, buddiesButton;
    private TextView welcomeUserText, countdownView, statusView;
    private Spinner assignmentSpinner;
    private CountDownTimer countdownTimer;

    private final ArrayList<Assignment> assignments = new ArrayList<>();
    private ArrayAdapter<Assignment> spinnerAdapter;
    private AssignmentRepository repo;

    private final ActivityResultLauncher<Intent> addAssignmentLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Assignment a = (Assignment) result.getData()
                                    .getSerializableExtra("ASSIGNMENT");
                            if (a != null) {
                                try {
                                    repo.add(a);
                                } catch (IOException | ClassNotFoundException e) {
                                    Toast.makeText(this, "Could not save assignment",
                                            Toast.LENGTH_LONG).show();
                                }
                                loadAssignments();
                                if (!assignments.isEmpty()) {
                                    assignmentSpinner.setSelection(assignments.size() - 1);
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_home);

        repo = new AssignmentRepository(this);

        welcomeUserText = findViewById(R.id.welcomeUserText);
        countdownView = findViewById(R.id.countdownView);
        statusView = findViewById(R.id.statusView);
        addAssignment = findViewById(R.id.addAssignment);
        startButton = findViewById(R.id.startButton);
        tickButton = findViewById(R.id.tickButton);
        finishButton = findViewById(R.id.finishButton);
        buddiesButton = findViewById(R.id.buddiesButton);
        assignmentSpinner = findViewById(R.id.assignmentSpinner);

        welcomeUserText.setText("Welcome " + UserProfile.getName(this));

        spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, assignments);
        spinnerAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        assignmentSpinner.setAdapter(spinnerAdapter);

        assignmentSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        startCountdown(assignments.get(position));
                        refreshStatus();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        stopCountdown();
                        countdownView.setText("No assignment selected");
                        statusView.setText("");
                    }
                });

        addAssignment.setOnClickListener(v ->
                addAssignmentLauncher.launch(new Intent(this, AddAssignmentActivity.class)));
        buddiesButton.setOnClickListener(v ->
                startActivity(new Intent(this, BuddiesActivity.class)));
        startButton.setOnClickListener(v -> handleStart());
        tickButton.setOnClickListener(v -> handleTick());
        finishButton.setOnClickListener(v -> handleFinish());

        loadAssignments();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // ---------- helpers ----------

    private void loadAssignments() {
        assignments.clear();
        try {
            assignments.addAll(repo.load());
        } catch (IOException | ClassNotFoundException e) {
            Toast.makeText(this, "Could not load assignments", Toast.LENGTH_LONG).show();
        }
        spinnerAdapter.notifyDataSetChanged();
    }

    private Assignment selected() {
        int pos = assignmentSpinner.getSelectedItemPosition();
        if (pos < 0 || pos >= assignments.size()) {
            Toast.makeText(this, "Add an assignment first", Toast.LENGTH_SHORT).show();
            return null;
        }
        return assignments.get(pos);
    }

    private void save(Assignment a) {
        try {
            repo.update(a);
        } catch (IOException | ClassNotFoundException | IllegalArgumentException e) {
            Toast.makeText(this, "Could not save changes", Toast.LENGTH_LONG).show();
        }
    }

    private void refreshStatus() {
        int pos = assignmentSpinner.getSelectedItemPosition();
        if (pos < 0 || pos >= assignments.size()) return;
        Assignment a = assignments.get(pos);
        statusView.setText(a.getPercentComplete() + "% done - " + a.getStatus());
    }

    private boolean smsReady() {
        if (SmsPermissions.hasSmsPermissions(this)) return true;
        SmsPermissions.request(this);
        Toast.makeText(this, "Allow SMS permission, then try again", Toast.LENGTH_LONG).show();
        return false;
    }

    // ---------- buttons ----------

    private void handleStart() {
        Assignment a = selected();
        if (a == null || !smsReady()) return;
        a.markStarted();
        save(a);
        refreshStatus();
        int sent = BuddyNotifier.notifyStarted(this, a);
        Toast.makeText(this, sent + " buddies notified", Toast.LENGTH_LONG).show();
    }

    private void handleTick() {
        Assignment a = selected();
        if (a == null || !smsReady()) return;
        for (Milestone m : a.getMilestones()) {
            if (!m.isDone()) {
                m.setDone(true);
                a.updateProgress();
                save(a);
                refreshStatus();
                BuddyNotifier.notifyMilestone(this, a);
                return;
            }
        }
        Toast.makeText(this, "All questions are done. Press Finish.", Toast.LENGTH_SHORT).show();
    }

    private void handleFinish() {
        Assignment a = selected();
        if (a == null || !smsReady()) return;
        if (a.getPercentComplete() < 100) {
            Toast.makeText(this, "Complete all questions first", Toast.LENGTH_SHORT).show();
            return;
        }
        int points = BuddyNotifier.finishAndNotify(this, a);
        if (points < 0) {
            Toast.makeText(this, "Already finished, or it could not be saved",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Finished! +" + points + " pts", Toast.LENGTH_LONG).show();
        }
        refreshStatus();
    }

    // ---------- countdown ----------

    private void startCountdown(Assignment a) {
        stopCountdown();
        long remaining = a.getDueDate() - System.currentTimeMillis();
        if (remaining <= 0) {
            countdownView.setText("Overdue");
            return;
        }
        countdownTimer = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long ms) {
                long d = ms / 86400000L;
                long h = (ms / 3600000L) % 24;
                long m = (ms / 60000L) % 60;
                long s = (ms / 1000L) % 60;
                countdownView.setText(String.format(Locale.getDefault(),
                        "Due in %dd %02dh %02dm %02ds", d, h, m, s));
            }

            @Override
            public void onFinish() {
                countdownView.setText("Overdue");
            }
        }.start();
    }

    private void stopCountdown() {
        if (countdownTimer != null) {
            countdownTimer.cancel();
            countdownTimer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCountdown();
    }
}