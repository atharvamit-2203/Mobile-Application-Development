package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import org.json.JSONObject;
import java.util.*;
import java.text.SimpleDateFormat;

@SuppressWarnings({"deprecation", "unchecked"})
public class FacultyAttendanceScannerActivity extends Activity {

    private TextView tvScanStatus, tvStudentDetails, tvLectureInfo;
    private Button btnScanQR, btnMarkPresent, btnMarkAbsent;
    private LinearLayout studentDetailsLayout;
    private Spinner spinnerLectures;
    
    private String scannedUserId;
    private String scannedUserName;
    private String scannedEmail;
    private String selectedLectureId;
    private List<Map<String, Object>> facultyLectures = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_attendance_scanner);
        
        tvScanStatus = findViewById(R.id.tvScanStatus);
        tvStudentDetails = findViewById(R.id.tvStudentDetails);
        tvLectureInfo = findViewById(R.id.tvLectureInfo);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnMarkPresent = findViewById(R.id.btnMarkPresent);
        btnMarkAbsent = findViewById(R.id.btnMarkAbsent);
        studentDetailsLayout = findViewById(R.id.studentDetailsLayout);
        spinnerLectures = findViewById(R.id.spinnerLectures);
        
        studentDetailsLayout.setVisibility(android.view.View.GONE);
        
        btnScanQR.setOnClickListener(v -> startQRScanner());
        btnMarkPresent.setOnClickListener(v -> markAttendance("present"));
        btnMarkAbsent.setOnClickListener(v -> markAttendance("absent"));
        
        loadFacultyLectures();
    }
    
    private void loadFacultyLectures() {
        // Use TimetableNotificationHelper to get today's lectures
        TimetableNotificationHelper.getTodaysLectures(this, lectures -> {
            facultyLectures.clear();
            facultyLectures.addAll(lectures);
            
            List<String> lectureNames = new ArrayList<>();
            for (Map<String, Object> lecture : lectures) {
                String subject = (String) lecture.get("subject");
                String startTime = (String) lecture.get("start_time");
                String endTime = (String) lecture.get("end_time");
                String room = (String) lecture.get("room");
                
                String timeStr = startTime + " - " + endTime;
                String roomStr = (room != null && !room.isEmpty()) ? " (" + room + ")" : "";
                lectureNames.add(subject + " - " + timeStr + roomStr);
            }
            
            if (lectureNames.isEmpty()) {
                lectureNames.add("No lectures today - Upload timetable first");
                tvLectureInfo.setText("Please upload your timetable to see today's lectures");
            }
            
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                FacultyAttendanceScannerActivity.this,
                android.R.layout.simple_spinner_item,
                lectureNames
            );
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerLectures.setAdapter(adapter);
            
            spinnerLectures.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                    if (position < facultyLectures.size()) {
                        Map<String, Object> lecture = facultyLectures.get(position);
                        selectedLectureId = lecture.get("id") != null ? lecture.get("id").toString() : "";
                        updateLectureInfo(lecture);
                    }
                }
                
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedLectureId = null;
                }
            });
        });
    }
    
    private void updateLectureInfo(Map<String, Object> lecture) {
        String subject = (String) lecture.get("subject");
        String time = (String) lecture.get("time");
        String room = (String) lecture.get("room");
        String batch = (String) lecture.get("batch");
        
        String info = "Subject: " + subject + "\n" +
                     "Time: " + time + "\n" +
                     "Room: " + room + "\n" +
                     "Batch: " + batch;
        
        tvLectureInfo.setText(info);
    }
    
    private void startQRScanner() {
        if (selectedLectureId == null || facultyLectures.isEmpty()) {
            Toast.makeText(this, "Please select a lecture first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Student QR Code for Attendance");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                processQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void processQRCode(String qrContent) {
        try {
            JSONObject qrData = new JSONObject(qrContent);
            
            String type = qrData.getString("type");
            if (!type.equals("student_id")) {
                Toast.makeText(this, "Invalid QR code. Please scan student ID QR.", Toast.LENGTH_SHORT).show();
                return;
            }
            
            scannedUserId = qrData.getString("userId");
            scannedUserName = qrData.getString("name");
            scannedEmail = qrData.getString("email");
            String role = qrData.getString("role");
            long timestamp = qrData.getLong("timestamp");
            
            // Verify it's a student
            if (!role.equals("student")) {
                Toast.makeText(this, "This QR code is not for a student", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Check if QR is not too old (valid for 24 hours)
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > 86400000) {
                Toast.makeText(this, "QR code expired. Please ask student to regenerate.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Display student details
            displayStudentDetails();
            
        } catch (Exception e) {
            Toast.makeText(this, "Invalid QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void displayStudentDetails() {
        tvScanStatus.setText("✓ Student Scanned Successfully");
        tvScanStatus.setTextColor(0xFF10B981); // Green
        
        String details = "Name: " + scannedUserName + "\n" +
                        "Email: " + scannedEmail + "\n" +
                        "User ID: " + scannedUserId;
        
        tvStudentDetails.setText(details);
        studentDetailsLayout.setVisibility(android.view.View.VISIBLE);
    }
    
    private void markAttendance(String status) {
        if (scannedUserId == null || selectedLectureId == null) {
            Toast.makeText(this, "Please scan a student QR code first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Prefs prefs = Prefs.getInstance(this);
        String facultyId = prefs.getUserId();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        
        Map<String, Object> attendanceData = new HashMap<>();
        attendanceData.put("student_id", scannedUserId);
        attendanceData.put("student_name", scannedUserName);
        attendanceData.put("lecture_id", selectedLectureId);
        attendanceData.put("faculty_id", facultyId);
        attendanceData.put("status", status);
        attendanceData.put("date", currentDate);
        attendanceData.put("timestamp", System.currentTimeMillis());
        attendanceData.put("marked_by", prefs.getName());
        
        // Add subject and time from selected lecture
        if (spinnerLectures.getSelectedItemPosition() < facultyLectures.size()) {
            Map<String, Object> lecture = facultyLectures.get(spinnerLectures.getSelectedItemPosition());
            attendanceData.put("subject", lecture.get("subject"));
            attendanceData.put("time", lecture.get("time"));
            attendanceData.put("room", lecture.get("room"));
        }
        
        FirebaseHelper.markAttendance(attendanceData, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                String message = status.equals("present") ? 
                    "✓ Marked Present: " + scannedUserName : 
                    "✗ Marked Absent: " + scannedUserName;
                    
                Toast.makeText(FacultyAttendanceScannerActivity.this, message, Toast.LENGTH_SHORT).show();
                
                // Reset for next student
                resetScanner();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(FacultyAttendanceScannerActivity.this, 
                    "Error marking attendance: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void resetScanner() {
        scannedUserId = null;
        scannedUserName = null;
        scannedEmail = null;
        
        tvScanStatus.setText("Scan student QR code for attendance");
        tvScanStatus.setTextColor(0xFF64748B); // Gray
        tvStudentDetails.setText("");
        studentDetailsLayout.setVisibility(android.view.View.GONE);
    }
}
