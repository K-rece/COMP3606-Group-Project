package com.example.comp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DateFormat;
import java.util.Calendar;

public class AddAssignmentActivity extends AppCompatActivity {
    private EditText assignmentTitle,assignmentCourse,questionCount;
    private TextView dueDateView;
    private Button doneButton, dueDateButton;
    private long dueMillis = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_add_assignment);

        assignmentTitle = findViewById(R.id.assignmentTitle);
        assignmentCourse = findViewById(R.id.assignmentCourse);
        questionCount = findViewById(R.id.questionCount);
        dueDateView = findViewById(R.id.dueDateView);

        doneButton = findViewById(R.id.doneButton);
        dueDateButton = findViewById(R.id.dueDateButton);

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDueDate();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssignment();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void pickDueDate() {
        Calendar now = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            new TimePickerDialog(this, (tv, hour, minute) -> {
                Calendar chosen = Calendar.getInstance();
                chosen.set(year, month, day, hour, minute, 0);
                dueMillis = chosen.getTimeInMillis();
                dueDateView.setText(DateFormat.getDateTimeInstance(
                                DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(chosen.getTime()));
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), false).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void addAssignment() {
        String title = assignmentTitle.getText().toString();
        String course = assignmentCourse.getText().toString();
        String questions = questionCount.getText().toString().trim();

        if(title.isEmpty()) {
            assignmentTitle.setError("Enter Title");
            return;
        }
        if(questions.isEmpty()) {
            questionCount.setError("Enter amount of questions");
            return;
        }
        if (dueMillis < 0) {
            Toast.makeText(this, "Pick a due date and time", Toast.LENGTH_SHORT).show();
            return;
        }

        int progress = Integer.parseInt(questions);
        Assignment a = new Assignment(title, course, dueMillis, progress);

        Intent intent = new Intent();
        intent.putExtra("ASSIGNMENT",a);
        setResult(RESULT_OK, intent);
        finish();
    }
}
