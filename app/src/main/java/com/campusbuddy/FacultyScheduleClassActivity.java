package com.campusbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;
import java.text.SimpleDateFormat;

public class FacultyScheduleClassActivity extends Activity {
    private FirebaseFirestore db;
    private Spinner spinnerSubject, spinnerCourse, spinnerYear, spinnerBatch;
    private EditText etClassTitle, etClassRoom, etDate, etStartTime, etEndTime;
    private Button btnSchedule, btnViewSchedule, btnBack;
    private ProgressBar progressBar;
    private TextView tvStatus;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        db = FirebaseFirestore.getInstance();
        
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(0xFF0F172A);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        
        // Header Card
        androidx.cardview.widget.CardView headerCard = new androidx.cardview.widget.CardView(this);
        headerCard.setCardBackgroundColor(0xFF1E293B);
        headerCard.setRadius(20);
        headerCard.setCardElevation(8);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headerParams.bottomMargin = 25;
        headerCard.setLayoutParams(headerParams);
        
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(30, 30, 30, 30);
        
        TextView title = new TextView(this);
        title.setText("📅 Schedule Class");
        title.setTextSize(28);
        title.setTextColor(0xFFFFFFFF);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Create a new class for your assigned students");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        // Class Title
        TextView lblTitle = new TextView(this);
        lblTitle.setText("Class Title");
        lblTitle.setTextSize(15);
        lblTitle.setTextColor(0xFFE2E8F0);
        lblTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        lblTitle.setPadding(0, 10, 0, 8);
        layout.addView(lblTitle);
        
        etClassTitle = new EditText(this);
        etClassTitle.setHint("e.g., Data Structures Lecture");
        etClassTitle.setTextColor(0xFFFFFFFF);
        etClassTitle.setHintTextColor(0xFF64748B);
        etClassTitle.setBackgroundColor(0xFF1E293B);
        etClassTitle.setPadding(25, 25, 25, 25);
        etClassTitle.setTextSize(16);
        LinearLayout.LayoutParams etParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        etParams.bottomMargin = 5;
        etClassTitle.setLayoutParams(etParams);
        layout.addView(etClassTitle);
        
        // Subject
        TextView lblSubject = new TextView(this);
        lblSubject.setText("Subject");
        lblSubject.setTextSize(16);
        lblSubject.setTextColor(0xFFFFFFFF);
        lblSubject.setPadding(0, 20, 0, 5);
        layout.addView(lblSubject);
        
        spinnerSubject = new Spinner(this);
        spinnerSubject.setBackgroundColor(0xFF374151);
        spinnerSubject.setPadding(20, 20, 20, 20);
        layout.addView(spinnerSubject);
        
        // Course
        TextView lblCourse = new TextView(this);
        lblCourse.setText("Course");
        lblCourse.setTextSize(16);
        lblCourse.setTextColor(0xFFFFFFFF);
        lblCourse.setPadding(0, 20, 0, 5);
        layout.addView(lblCourse);
        
        spinnerCourse = new Spinner(this);
        spinnerCourse.setBackgroundColor(0xFF374151);
        spinnerCourse.setPadding(20, 20, 20, 20);
        layout.addView(spinnerCourse);
        
        // Year
        TextView lblYear = new TextView(this);
        lblYear.setText("Year");
        lblYear.setTextSize(16);
        lblYear.setTextColor(0xFFFFFFFF);
        lblYear.setPadding(0, 20, 0, 5);
        layout.addView(lblYear);
        
        spinnerYear = new Spinner(this);
        spinnerYear.setBackgroundColor(0xFF374151);
        spinnerYear.setPadding(20, 20, 20, 20);
        layout.addView(spinnerYear);
        
        // Batch
        TextView lblBatch = new TextView(this);
        lblBatch.setText("Batch");
        lblBatch.setTextSize(16);
        lblBatch.setTextColor(0xFFFFFFFF);
        lblBatch.setPadding(0, 20, 0, 5);
        layout.addView(lblBatch);
        
        spinnerBatch = new Spinner(this);
        spinnerBatch.setBackgroundColor(0xFF374151);
        spinnerBatch.setPadding(20, 20, 20, 20);
        layout.addView(spinnerBatch);
        
        // Room
        TextView lblRoom = new TextView(this);
        lblRoom.setText("Room");
        lblRoom.setTextSize(16);
        lblRoom.setTextColor(0xFFFFFFFF);
        lblRoom.setPadding(0, 20, 0, 5);
        layout.addView(lblRoom);
        
        etClassRoom = new EditText(this);
        etClassRoom.setHint("e.g., Room 301");
        etClassRoom.setTextColor(0xFFFFFFFF);
        etClassRoom.setHintTextColor(0xFF9CA3AF);
        etClassRoom.setBackgroundColor(0xFF374151);
        etClassRoom.setPadding(20, 20, 20, 20);
        layout.addView(etClassRoom);
        
        // Date
        TextView lblDate = new TextView(this);
        lblDate.setText("Date");
        lblDate.setTextSize(16);
        lblDate.setTextColor(0xFFFFFFFF);
        lblDate.setPadding(0, 20, 0, 5);
        layout.addView(lblDate);
        
        etDate = new EditText(this);
        etDate.setHint("Select Date");
        etDate.setTextColor(0xFFFFFFFF);
        etDate.setHintTextColor(0xFF9CA3AF);
        etDate.setBackgroundColor(0xFF374151);
        etDate.setPadding(20, 20, 20, 20);
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> showDatePicker());
        layout.addView(etDate);
        
        // Start Time
        TextView lblStartTime = new TextView(this);
        lblStartTime.setText("Start Time");
        lblStartTime.setTextSize(16);
        lblStartTime.setTextColor(0xFFFFFFFF);
        lblStartTime.setPadding(0, 20, 0, 5);
        layout.addView(lblStartTime);
        
        etStartTime = new EditText(this);
        etStartTime.setHint("Select Start Time");
        etStartTime.setTextColor(0xFFFFFFFF);
        etStartTime.setHintTextColor(0xFF9CA3AF);
        etStartTime.setBackgroundColor(0xFF374151);
        etStartTime.setPadding(20, 20, 20, 20);
        etStartTime.setFocusable(false);
        etStartTime.setOnClickListener(v -> showStartTimePicker());
        layout.addView(etStartTime);
        
        // End Time
        TextView lblEndTime = new TextView(this);
        lblEndTime.setText("End Time");
        lblEndTime.setTextSize(16);
        lblEndTime.setTextColor(0xFFFFFFFF);
        lblEndTime.setPadding(0, 20, 0, 5);
        layout.addView(lblEndTime);
        
        etEndTime = new EditText(this);
        etEndTime.setHint("Select End Time");
        etEndTime.setTextColor(0xFFFFFFFF);
        etEndTime.setHintTextColor(0xFF9CA3AF);
        etEndTime.setBackgroundColor(0xFF374151);
        etEndTime.setPadding(20, 20, 20, 20);
        etEndTime.setFocusable(false);
        etEndTime.setOnClickListener(v -> showEndTimePicker());
        layout.addView(etEndTime);
        
        // Status
        tvStatus = new TextView(this);
        tvStatus.setTextSize(14);
        tvStatus.setTextColor(0xFF10B981);
        tvStatus.setPadding(0, 20, 0, 10);
        tvStatus.setVisibility(TextView.GONE);
        layout.addView(tvStatus);
        
        // Progress Bar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(ProgressBar.GONE);
        layout.addView(progressBar);
        
        // Schedule Button
        btnSchedule = new Button(this);
        btnSchedule.setText("Schedule Class");
        btnSchedule.setBackgroundColor(0xFF3B82F6);
        btnSchedule.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams scheduleParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        scheduleParams.topMargin = 30;
        btnSchedule.setLayoutParams(scheduleParams);
        layout.addView(btnSchedule);
        
        // View Schedule Button
        btnViewSchedule = new Button(this);
        btnViewSchedule.setText("View My Schedule");
        btnViewSchedule.setBackgroundColor(0xFF10B981);
        btnViewSchedule.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        viewParams.topMargin = 15;
        btnViewSchedule.setLayoutParams(viewParams);
        layout.addView(btnViewSchedule);
        
        // Back Button
        btnBack = new Button(this);
        btnBack.setText("Back");
        btnBack.setBackgroundColor(0xFF6B7280);
        btnBack.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        backParams.topMargin = 15;
        btnBack.setLayoutParams(backParams);
        layout.addView(btnBack);
        
        scrollView.addView(layout);
        setContentView(scrollView);
        
        setupSpinners();
        
        btnSchedule.setOnClickListener(v -> scheduleClass());
        btnViewSchedule.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, TimetableActivity.class));
        });
        btnBack.setOnClickListener(v -> finish());
    }
    
    private void setupSpinners() {
        // Subjects
        String[] subjects = {"Data Structures", "Database Management", "Operating Systems", 
            "Computer Networks", "Software Engineering", "Web Development", 
            "Machine Learning", "Artificial Intelligence", "Cloud Computing"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);
        
        // Courses
        String[] courses = {"B.Tech Computer Engineering", "B.Tech IT", "B.Tech AIDS", 
            "B.Tech Electronics", "MCA", "M.Tech"};
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, courses);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(courseAdapter);
        
        // Years
        String[] years = {"1", "2", "3", "4"};
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);
        
        // Batches
        String[] batches = {"A", "B", "C", "D", "All"};
        ArrayAdapter<String> batchAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, batches);
        batchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBatch.setAdapter(batchAdapter);
    }
    
    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            etDate.setText(dateFormat.format(selectedDate.getTime()));
        }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), 
           selectedDate.get(Calendar.DAY_OF_MONTH)).show();
    }
    
    private void showStartTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTime.set(Calendar.MINUTE, minute);
            etStartTime.setText(timeFormat.format(startTime.getTime()));
        }, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true).show();
    }
    
    private void showEndTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endTime.set(Calendar.MINUTE, minute);
            etEndTime.setText(timeFormat.format(endTime.getTime()));
        }, endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE), true).show();
    }
    
    private void scheduleClass() {
        String classTitle = etClassTitle.getText().toString().trim();
        String room = etClassRoom.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String startTimeStr = etStartTime.getText().toString().trim();
        String endTimeStr = etEndTime.getText().toString().trim();
        
        if (classTitle.isEmpty() || date.isEmpty() || startTimeStr.isEmpty() || endTimeStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String subject = spinnerSubject.getSelectedItem().toString();
        String course = spinnerCourse.getSelectedItem().toString();
        String year = spinnerYear.getSelectedItem().toString();
        String batch = spinnerBatch.getSelectedItem().toString();
        String facultyId = Prefs.getInstance(this).getUserId();
        String facultyName = Prefs.getInstance(this).getName();
        
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvStatus.setVisibility(TextView.VISIBLE);
        tvStatus.setText("Finding assigned students...");
        
        // Find students assigned to this faculty for this subject/course/year/batch
        db.collection("teacher_assignments")
            .whereEqualTo("faculty_id", facultyId)
            .whereEqualTo("subject", subject)
            .whereEqualTo("course", course)
            .whereEqualTo("year", year)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<String> studentIds = new ArrayList<>();
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String assignmentBatch = (String) doc.getData().get("batch");
                    if (batch.equals("All") || batch.equals(assignmentBatch)) {
                        List<String> ids = (List<String>) doc.getData().get("student_ids");
                        if (ids != null) {
                            studentIds.addAll(ids);
                        }
                    }
                }
                
                if (studentIds.isEmpty()) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    tvStatus.setText("No students assigned for this subject/course/year/batch");
                    tvStatus.setTextColor(0xFFEF4444);
                    Toast.makeText(this, "No assigned students found", Toast.LENGTH_LONG).show();
                    return;
                }
                
                tvStatus.setText("Creating class for " + studentIds.size() + " students...");
                
                // Create timetable entry
                Map<String, Object> classEntry = new HashMap<>();
                classEntry.put("title", classTitle);
                classEntry.put("subject", subject);
                classEntry.put("course", course);
                classEntry.put("year", year);
                classEntry.put("batch", batch);
                classEntry.put("room", room);
                classEntry.put("date", date);
                classEntry.put("start_time", startTimeStr);
                classEntry.put("end_time", endTimeStr);
                classEntry.put("faculty_id", facultyId);
                classEntry.put("faculty_name", facultyName);
                classEntry.put("student_ids", studentIds);
                classEntry.put("created_at", com.google.firebase.Timestamp.now());
                
                db.collection("timetable")
                    .add(classEntry)
                    .addOnSuccessListener(docRef -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        tvStatus.setText("✅ Class scheduled successfully for " + studentIds.size() + " students!");
                        tvStatus.setTextColor(0xFF10B981);
                        Toast.makeText(this, "Class scheduled! Students will see it in their timetable", Toast.LENGTH_LONG).show();
                        
                        // Clear form
                        etClassTitle.setText("");
                        etClassRoom.setText("");
                        etDate.setText("");
                        etStartTime.setText("");
                        etEndTime.setText("");
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        tvStatus.setText("Error: " + e.getMessage());
                        tvStatus.setTextColor(0xFFEF4444);
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                tvStatus.setText("Error: " + e.getMessage());
                tvStatus.setTextColor(0xFFEF4444);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
}
