package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

// View and create bookings
public class BookingsActivity extends Activity {

    private ListView bookingsListView;
    private List<Map<String, Object>> bookings = new ArrayList<>();
    private BookingsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookings);
        
        bookingsListView = findViewById(R.id.bookingsListView);
        adapter = new BookingsAdapter();
        bookingsListView.setAdapter(adapter);
        
        loadBookings();
    }
    
    private void loadBookings() {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.getBookings(userId, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                bookings = data;
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(BookingsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    class BookingsAdapter extends BaseAdapter {
        @Override
        public int getCount() { return bookings.size(); }
        
        @Override
        public Object getItem(int position) { return bookings.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_booking, parent, false);
            }
            
            Map<String, Object> booking = bookings.get(position);
            
            TextView titleText = convertView.findViewById(R.id.bookingTitleText);
            TextView dateText = convertView.findViewById(R.id.bookingDateText);
            TextView timeText = convertView.findViewById(R.id.bookingTimeText);
            TextView statusText = convertView.findViewById(R.id.bookingStatusText);
            
            titleText.setText((String) booking.get("title"));
            dateText.setText((String) booking.getOrDefault("booking_date", ""));
            timeText.setText(booking.getOrDefault("start_time", "") + " - " + booking.getOrDefault("end_time", ""));
            statusText.setText((String) booking.getOrDefault("status", "pending"));
            
            return convertView;
        }
    }
}
