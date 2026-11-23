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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private Button addAssignment;
    private TextView welcomeUserText, countdownView;
    private Spinner assignmentSpinner;
    private CountDownTimer countdownTimer;

    private final ArrayList<Assignment> assignments = new ArrayList<>();
    private ArrayAdapter<Assignment> spinnerAdapter;

    private final ActivityResultLauncher<Intent> addAssignmentLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Assignment a = (Assignment) result.getData()
                                    .getSerializableExtra("ASSIGNMENT");
                            if (a != null) {
                                assignments.add(a);
                                spinnerAdapter.notifyDataSetChanged();
                                assignmentSpinner.setSelection(assignments.size() - 1);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");

        welcomeUserText = findViewById(R.id.welcomeUserText);
        countdownView = findViewById(R.id.countdownView);
        addAssignment = findViewById(R.id.addAssignment);
        assignmentSpinner = findViewById(R.id.assignmentSpinner);

        welcomeUserText.setText("Welcome "+userName);

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
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        stopCountdown();
                        countdownView.setText("No assignment selected");
                    }
                });

        addAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddAssignmentScreen();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void goToAddAssignmentScreen() {
        Intent intent = new Intent(this, AddAssignmentActivity.class);
        addAssignmentLauncher.launch(intent);
    }
    private void startCountdown(Assignment a) {
        stopCountdown();
        long remaining = a.getDueMillis() - System.currentTimeMillis();
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
