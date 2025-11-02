package com.campusbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Timetable for students and faculty
public class TimetableActivity extends Activity {

    private ListView timetableListView;
    private Button btnAddClass, btnUploadTimetable;
    private Spinner spinnerDay;
    private List<Map<String, Object>> timetable = new ArrayList<>();
    private TimetableAdapter adapter;
    private String userRole;
    
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        
        timetableListView = findViewById(R.id.timetableListView);
        btnAddClass = findViewById(R.id.btnAddClass);
        btnUploadTimetable = findViewById(R.id.btnUploadTimetable);
        spinnerDay = findViewById(R.id.spinnerDay);

        userRole = Prefs.getInstance(this).getUserRole();

        setupDaySpinner();
        adapter = new TimetableAdapter();
        timetableListView.setAdapter(adapter);

        // Only faculty and students can add classes
        if (userRole.equals("faculty") || userRole.equals("student")) {
            btnAddClass.setVisibility(View.VISIBLE);
            btnAddClass.setOnClickListener(v -> showAddClassDialog());
            btnUploadTimetable.setVisibility(View.VISIBLE);
            btnUploadTimetable.setOnClickListener(v -> {
                android.content.Intent intent = new android.content.Intent(this, TimetableUploadActivity.class);
                startActivity(intent);
            });
        } else {
            btnAddClass.setVisibility(View.GONE);
            btnUploadTimetable.setVisibility(View.GONE);
        }        loadTimetable("Monday");
        
        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String day = parent.getItemAtPosition(position).toString();
                loadTimetable(day);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void setupDaySpinner() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, days);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapter);
    }
    
    private void loadTimetable(String day) {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.getTimetable(userId, day, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                timetable = data;
                // Sort by start time
                Collections.sort(timetable, (a, b) -> {
                    String timeA = (String) a.get("start_time");
                    String timeB = (String) b.get("start_time");
                    return timeA.compareTo(timeB);
                });
                adapter.notifyDataSetChanged();
                
                // Check for upcoming classes and notify
                checkUpcomingClasses();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(TimetableActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void checkUpcomingClasses() {
        Calendar now = Calendar.getInstance();
        String currentTime = timeFormat.format(now.getTime());
        
        for (Map<String, Object> classItem : timetable) {
            String startTime = (String) classItem.get("start_time");
            // Check if class is starting in next 30 minutes
            // Simple implementation - you can enhance this
        }
    }
    
    private void showAddClassDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        
        EditText etSubject = dialogView.findViewById(R.id.etSubject);
        EditText etRoom = dialogView.findViewById(R.id.etRoom);
        TextView tvStartTime = dialogView.findViewById(R.id.tvStartTime);
        TextView tvEndTime = dialogView.findViewById(R.id.tvEndTime);
        Button btnSelectStart = dialogView.findViewById(R.id.btnSelectStart);
        Button btnSelectEnd = dialogView.findViewById(R.id.btnSelectEnd);
        
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        
        btnSelectStart.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    startTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    startTime.set(Calendar.MINUTE, minute);
                    tvStartTime.setText(timeFormat.format(startTime.getTime()));
                },
                startTime.get(Calendar.HOUR_OF_DAY),
                startTime.get(Calendar.MINUTE),
                true
            );
            dialog.show();
        });
        
        btnSelectEnd.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    endTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    endTime.set(Calendar.MINUTE, minute);
                    tvEndTime.setText(timeFormat.format(endTime.getTime()));
                },
                endTime.get(Calendar.HOUR_OF_DAY),
                endTime.get(Calendar.MINUTE),
                true
            );
            dialog.show();
        });
        
        builder.setView(dialogView)
            .setTitle("Add Class")
            .setPositiveButton("Add", (dialog, which) -> {
                String subject = etSubject.getText().toString().trim();
                String room = etRoom.getText().toString().trim();
                
                if (subject.isEmpty()) {
                    Toast.makeText(this, "Please enter subject", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String day = spinnerDay.getSelectedItem().toString();
                addClassToTimetable(subject, room, day, 
                    timeFormat.format(startTime.getTime()),
                    timeFormat.format(endTime.getTime()));
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void addClassToTimetable(String subject, String room, String day, String startTime, String endTime) {
        Prefs prefs = Prefs.getInstance(this);
        Map<String, Object> classData = new HashMap<>();
        classData.put("user_id", prefs.getUserId());
        classData.put("subject", subject);
        classData.put("room", room);
        classData.put("day", day);
        classData.put("start_time", startTime);
        classData.put("end_time", endTime);
        classData.put("created_by", prefs.getName());
        classData.put("created_at", new Date());
        
        FirebaseHelper.addTimetableClass(classData, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(TimetableActivity.this, "Class added!", Toast.LENGTH_SHORT).show();
                loadTimetable(day);
                
                // Send notification
                String userId = prefs.getUserId();
                NotificationHelper.notifyTimetableUpdate(
                    TimetableActivity.this,
                    userId
                );
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(TimetableActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private class TimetableAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return timetable.size();
        }
        
        @Override
        public Object getItem(int position) {
            return timetable.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_timetable, parent, false);
            }
            
            Map<String, Object> classItem = timetable.get(position);
            
            TextView tvSubject = convertView.findViewById(R.id.tvSubject);
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            TextView tvRoom = convertView.findViewById(R.id.tvRoom);
            
            tvSubject.setText((String) classItem.get("subject"));
            String time = classItem.get("start_time") + " - " + classItem.get("end_time");
            tvTime.setText(time);
            String room = (String) classItem.get("room");
            tvRoom.setText(room != null && !room.isEmpty() ? "Room: " + room : "");
            
            return convertView;
        }
    }
}
