package com.campusbuddy;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;
import java.util.*;
import java.text.SimpleDateFormat;

// Organization can create events and book rooms
public class OrganizationEventActivity extends Activity {

    private EditText etEventTitle, etDescription, etVenue;
    private TextView tvEventDate, tvStartTime, tvEndTime, tvConflicts;
    private Button btnSelectDate, btnSelectStartTime, btnSelectEndTime, btnCheckAvailability, btnCreateEvent;
    private Spinner spinnerEventType;
    private CheckBox cbRecruitment;
    
    private Calendar selectedDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_event);
        
        etEventTitle = findViewById(R.id.etEventTitle);
        etDescription = findViewById(R.id.etDescription);
        etVenue = findViewById(R.id.etVenue);
        tvEventDate = findViewById(R.id.tvEventDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvConflicts = findViewById(R.id.tvConflicts);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectStartTime = findViewById(R.id.btnSelectStartTime);
        btnSelectEndTime = findViewById(R.id.btnSelectEndTime);
        btnCheckAvailability = findViewById(R.id.btnCheckAvailability);
        btnCreateEvent = findViewById(R.id.btnCreateEvent);
        spinnerEventType = findViewById(R.id.spinnerEventType);
        cbRecruitment = findViewById(R.id.cbRecruitment);
        
        setupEventTypeSpinner();
        setupDateTimePickers();
        
        btnCheckAvailability.setOnClickListener(v -> checkVenueAvailability());
        btnCreateEvent.setOnClickListener(v -> createEvent());
    }
    
    private void setupEventTypeSpinner() {
        String[] eventTypes = {"Workshop", "Seminar", "Competition", "Cultural", "Technical", "Sports", "Social", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEventType.setAdapter(adapter);
    }
    
    private void setupDateTimePickers() {
        btnSelectDate.setOnClickListener(v -> {
            DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    tvEventDate.setText(dateFormat.format(selectedDate.getTime()));
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
    
    private void checkVenueAvailability() {
        String venue = etVenue.getText().toString().trim();
        if (venue.isEmpty()) {
            Toast.makeText(this, "Please enter venue", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String date = dateFormat.format(selectedDate.getTime());
        String start = timeFormat.format(startTime.getTime());
        String end = timeFormat.format(endTime.getTime());
        
        FirebaseHelper.checkRoomAvailability(venue, date, start, end, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> conflicts) {
                if (conflicts.isEmpty()) {
                    tvConflicts.setText("✓ Venue is available");
                    tvConflicts.setTextColor(0xFF10B981);
                    btnCreateEvent.setEnabled(true);
                } else {
                    StringBuilder sb = new StringBuilder("⚠ Conflicts found:\n");
                    for (Map<String, Object> booking : conflicts) {
                        sb.append("- ").append(booking.get("title")).append("\n");
                    }
                    tvConflicts.setText(sb.toString());
                    tvConflicts.setTextColor(0xFFDC3545);
                    btnCreateEvent.setEnabled(false);
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(OrganizationEventActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void createEvent() {
        String title = etEventTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String venue = etVenue.getText().toString().trim();
        String eventType = spinnerEventType.getSelectedItem().toString();
        boolean isRecruitment = cbRecruitment.isChecked();
        
        if (title.isEmpty() || description.isEmpty() || venue.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Prefs prefs = Prefs.getInstance(this);
        
        // Create event
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("description", description);
        event.put("event_type", eventType);
        event.put("event_date", dateFormat.format(selectedDate.getTime()));
        event.put("start_time", timeFormat.format(startTime.getTime()));
        event.put("end_time", timeFormat.format(endTime.getTime()));
        event.put("venue", venue);
        event.put("club_id", prefs.getUserId());
        event.put("club_name", prefs.getName());
        event.put("is_recruitment", isRecruitment);
        event.put("status", "approved");  // Auto-approve events to make them immediately visible
        event.put("created_at", new Date());
        event.put("max_participants", 100);
        event.put("registered_count", 0);
        
        FirebaseHelper.createClubEvent(event, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                // Also create room booking
                bookVenue(title, venue, prefs);
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(OrganizationEventActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void bookVenue(String eventTitle, String venue, Prefs prefs) {
        Map<String, Object> booking = new HashMap<>();
        booking.put("title", eventTitle);
        booking.put("room_number", venue);
        booking.put("room_type", "Event Venue");
        booking.put("date", dateFormat.format(selectedDate.getTime()));
        booking.put("start_time", timeFormat.format(startTime.getTime()));
        booking.put("end_time", timeFormat.format(endTime.getTime()));
        booking.put("booked_by", prefs.getUserId());
        booking.put("booked_by_name", prefs.getName());
        booking.put("booked_by_role", "organization");
        booking.put("purpose", "Club Event");
        booking.put("status", "confirmed");
        booking.put("created_at", new Date());
        
        FirebaseHelper.createRoomBooking(booking, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(OrganizationEventActivity.this, "Event created and venue booked!", Toast.LENGTH_LONG).show();
                finish();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(OrganizationEventActivity.this, "Event created but venue booking failed: " + error, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
