package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

// View notifications
public class NotificationsActivity extends Activity {

    private ListView notificationsListView;
    private List<Map<String, Object>> notifications = new ArrayList<>();
    private NotificationsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        
        notificationsListView = findViewById(R.id.notificationsListView);
        adapter = new NotificationsAdapter();
        notificationsListView.setAdapter(adapter);
        
        loadNotifications();
    }
    
    private void loadNotifications() {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.getNotifications(userId, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                notifications = data;
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(NotificationsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void markAsRead(String notifId, int position) {
        FirebaseHelper.markNotificationRead(notifId, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Map<String, Object> notif = notifications.get(position);
                notif.put("is_read", true);
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                // Silently fail
            }
        });
    }
    
    class NotificationsAdapter extends BaseAdapter {
        @Override
        public int getCount() { return notifications.size(); }
        
        @Override
        public Object getItem(int position) { return notifications.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_notification, parent, false);
            }
            
            Map<String, Object> notif = notifications.get(position);
            
            TextView titleText = convertView.findViewById(R.id.notifTitleText);
            TextView messageText = convertView.findViewById(R.id.notifMessageText);
            TextView typeText = convertView.findViewById(R.id.notifTypeText);
            View unreadDot = convertView.findViewById(R.id.unreadDot);
            
            titleText.setText((String) notif.get("title"));
            messageText.setText((String) notif.get("message"));
            typeText.setText((String) notif.getOrDefault("type", "general"));
            
            Boolean isRead = (Boolean) notif.get("is_read");
            unreadDot.setVisibility(Boolean.TRUE.equals(isRead) ? View.GONE : View.VISIBLE);
            
            convertView.setOnClickListener(v -> {
                if (!Boolean.TRUE.equals(isRead)) {
                    markAsRead((String) notif.get("id"), position);
                }
            });
            
            return convertView;
        }
    }
}
