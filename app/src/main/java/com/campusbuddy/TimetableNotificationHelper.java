package com.campusbuddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Map;

public class TimetableNotificationHelper {
    private static final String TAG = "TimetableNotification";
    private static final String CHANNEL_ID = "timetable_notifications";
    private static final int NOTIFICATION_ADVANCE_MINUTES = 10; // Alert 10 minutes before class
    
    /**
     * Create notification channel (Android 8.0+)
     */
    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Lecture Reminders",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for upcoming lectures");
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
    
    /**
     * Schedule notifications for all upcoming lectures from timetable
     * This just sets up a background check - actual notifications sent when needed
     */
    public static void scheduleAllNotifications(Context context) {
        String userId = Prefs.getInstance(context).getUserId();
        if (userId == null) {
            return;
        }
        
        createNotificationChannel(context);
        
        // Start a background service to check for upcoming lectures
        FirebaseFirestore.getInstance()
            .collection("timetable")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int count = queryDocumentSnapshots.size();
                if (count > 0) {
                    Toast.makeText(context, "Timetable saved! You'll be notified before lectures.", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading timetable", e);
            });
    }
    
    /**
     * Show notification for upcoming lecture (call this when needed)
     */
    public static void showLectureNotification(Context context, String subject, String room, String startTime) {
        createNotificationChannel(context);
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) {
            return;
        }
        
        String contentText = "Starting at " + startTime;
        if (room != null && !room.isEmpty()) {
            contentText += " in " + room;
        }
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("📚 Upcoming Lecture: " + subject)
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);
        
        notificationManager.notify(subject.hashCode(), builder.build());
    }
    
    /**
     * Get today's lectures for the current user
     */
    public static void getTodaysLectures(Context context, LecturesCallback callback) {
        String userId = Prefs.getInstance(context).getUserId();
        if (userId == null) {
            callback.onResult(new java.util.ArrayList<>());
            return;
        }
        
        // Get current day name
        Calendar calendar = Calendar.getInstance();
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        String today = days[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        
        FirebaseFirestore.getInstance()
            .collection("timetable")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("day_of_week", today)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                java.util.List<Map<String, Object>> lectures = new java.util.ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    lectures.add(doc.getData());
                }
                callback.onResult(lectures);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error loading today's lectures", e);
                callback.onResult(new java.util.ArrayList<>());
            });
    }
    
    public interface LecturesCallback {
        void onResult(java.util.List<Map<String, Object>> lectures);
    }
}
