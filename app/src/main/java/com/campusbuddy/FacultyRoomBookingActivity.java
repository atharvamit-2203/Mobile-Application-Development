package com.campusbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Faculty can book rooms for extra lectures
public class FacultyRoomBookingActivity extends Activity {

    private EditText etTitle, etRoom;
    private TextView tvDate, tvStartTime, tvEndTime, tvConflicts;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnCheckAvailability, btnBookRoom;
    private Spinner spinnerRoomType;
    
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_room_booking);
        
        etTitle = findViewById(R.id.etTitle);
        etRoom = findViewById(R.id.etRoom);
        tvDate = findViewById(R.id.tvDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvConflicts = findViewById(R.id.tvConflicts);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnBookRoom = findViewById(R.id.btnBookRoom);
        spinnerRoomType = findViewById(R.id.spinnerRoomType);
        
        setupRoomTypeSpinner();
        setupDateTimePickers();
        
        btnCheckAvailability.setOnClickListener(v -> checkRoomAvailability());
        btnBookRoom.setOnClickListener(v -> bookRoom());
    }
    
    private void setupRoomTypeSpinner() {
        String[] roomTypes = {"Classroom", "Lab", "Auditorium", "Conference Room", "Tutorial Room"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoomType.setAdapter(adapter);
    }
    
    private void setupDateTimePickers() {
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    tvDate.setText(dateFormat.format(selectedDate.getTime()));
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            dialog.getDatePicker().setMinDate(System.currentTimeMillis());
            dialog.show();
        });
        
        btnSelectStartTime.setOnClickListener(v -> {
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
        
        btnSelectEndTime.setOnClickListener(v -> {
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
    }
    
    private void checkRoomAvailability() {
        String room = etRoom.getText().toString().trim();
        if (room.isEmpty()) {
            Toast.makeText(this, "Please enter room number", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String date = dateFormat.format(selectedDate.getTime());
        String start = timeFormat.format(startTime.getTime());
        String end = timeFormat.format(endTime.getTime());
        
        // Check for conflicts
        FirebaseHelper.checkRoomAvailability(room, date, start, end, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> conflicts) {
                if (conflicts.isEmpty()) {
                    tvConflicts.setText("✓ Room is available");
                    tvConflicts.setTextColor(0xFF10B981);
                    btnBookRoom.setEnabled(true);
                } else {
                    StringBuilder sb = new StringBuilder("⚠ Conflicts found:\n");
                    for (Map<String, Object> booking : conflicts) {
                        sb.append("- ").append(booking.get("title")).append(" (")
                          .append(booking.get("booked_by_name")).append(")\n");
                    }
                    tvConflicts.setText(sb.toString());
                    tvConflicts.setTextColor(0xFFDC3545);
                    btnBookRoom.setEnabled(false);
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(FacultyRoomBookingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void bookRoom() {
        String title = etTitle.getText().toString().trim();
        String room = etRoom.getText().toString().trim();
        String roomType = spinnerRoomType.getSelectedItem().toString();
        
        if (title.isEmpty() || room.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Prefs prefs = Prefs.getInstance(this);
        Map<String, Object> booking = new HashMap<>();
        booking.put("title", title);
        booking.put("room_number", room);
        booking.put("room_type", roomType);
        booking.put("date", dateFormat.format(selectedDate.getTime()));
        booking.put("start_time", timeFormat.format(startTime.getTime()));
        booking.put("end_time", timeFormat.format(endTime.getTime()));
        booking.put("booked_by", prefs.getUserId());
        booking.put("booked_by_name", prefs.getName());
        booking.put("booked_by_role", "faculty");
        booking.put("purpose", "Extra Lecture");
        booking.put("status", "confirmed");
        booking.put("created_at", new Date());
        
        FirebaseHelper.createRoomBooking(booking, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(FacultyRoomBookingActivity.this, "Room booked successfully!", Toast.LENGTH_SHORT).show();
                
                // Send notification
                String userId = prefs.getUserId();
                String bookingId = data.get("id") != null ? (String) data.get("id") : "N/A";
                NotificationHelper.notifyRoomBooking(
                    FacultyRoomBookingActivity.this,
                    userId,
                    bookingId,
                    room,
                    dateFormat.format(selectedDate.getTime())
                );
                
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(FacultyRoomBookingActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
