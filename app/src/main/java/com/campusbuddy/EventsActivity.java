package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

// View and register for club events
public class EventsActivity extends Activity {

    private ListView eventsListView;
    private List<Map<String, Object>> events = new ArrayList<>();
    private EventsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        
        eventsListView = findViewById(R.id.eventsListView);
        adapter = new EventsAdapter();
        eventsListView.setAdapter(adapter);
        
        loadEvents();
    }
    
    private void loadEvents() {
        FirebaseHelper.getClubEvents(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                events = data;
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(EventsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void registerForEvent(String eventId, int position) {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.registerForEvent(eventId, userId, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(EventsActivity.this, "Registered successfully!", Toast.LENGTH_SHORT).show();
                Map<String, Object> event = events.get(position);
                event.put("registered", true);
                adapter.notifyDataSetChanged();
                
                // Send notification
                String eventName = (String) event.get("name");
                NotificationHelper.notifyEventRegistration(
                    EventsActivity.this,
                    userId,
                    eventName
                );
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(EventsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    class EventsAdapter extends BaseAdapter {
        @Override
        public int getCount() { return events.size(); }
        
        @Override
        public Object getItem(int position) { return events.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_event, parent, false);
            }
            
            Map<String, Object> event = events.get(position);
            
            TextView titleText = convertView.findViewById(R.id.eventTitleText);
            TextView dateText = convertView.findViewById(R.id.eventDateText);
            TextView venueText = convertView.findViewById(R.id.eventVenueText);
            TextView descText = convertView.findViewById(R.id.eventDescText);
            Button registerButton = convertView.findViewById(R.id.registerEventButton);
            
            titleText.setText((String) event.get("title"));
            dateText.setText((String) event.getOrDefault("event_date", ""));
            venueText.setText((String) event.getOrDefault("venue", ""));
            descText.setText((String) event.getOrDefault("description", ""));
            
            Boolean registered = (Boolean) event.get("registered");
            if (Boolean.TRUE.equals(registered)) {
                registerButton.setText("Registered");
                registerButton.setEnabled(false);
            } else {
                registerButton.setText("Register");
                registerButton.setEnabled(true);
                registerButton.setOnClickListener(v -> registerForEvent((String) event.get("id"), position));
            }
            
            return convertView;
        }
    }
}
