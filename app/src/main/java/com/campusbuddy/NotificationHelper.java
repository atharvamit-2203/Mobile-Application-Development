package com.campusbuddy;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for creating and managing notifications in the app
 * Notifications are stored in Firestore and can be displayed to users
 */
public class NotificationHelper {
    
    private static final String COLLECTION_NAME = "notifications";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    // Notification types
    public static final String TYPE_CANTEEN_ORDER = "canteen_order";
    public static final String TYPE_ROOM_BOOKING = "room_booking";
    public static final String TYPE_EVENT_REGISTRATION = "event_registration";
    public static final String TYPE_CLUB_JOIN = "club_join";
    public static final String TYPE_TIMETABLE_UPDATE = "timetable_update";
    public static final String TYPE_ASSIGNMENT = "assignment";
    public static final String TYPE_EXTRA_CLASS = "extra_class";
    public static final String TYPE_GENERAL = "general";
    
    /**
     * Send a notification to a specific user
     * 
     * @param context Application context
     * @param userId User ID to send notification to
     * @param type Type of notification (use TYPE_ constants)
     * @param title Notification title
     * @param message Notification message
     */
    public static void sendNotification(Context context, String userId, String type, String title, String message) {
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(context, "Cannot send notification: User ID is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("type", type);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        notification.put("created_at", com.google.firebase.Timestamp.now());
        
        db.collection(COLLECTION_NAME)
            .add(notification)
            .addOnSuccessListener(documentReference -> {
                // Notification created successfully
                // You can show a toast or update UI if needed
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to create notification: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    /**
     * Send a notification with a callback
     */
    public static void sendNotification(Context context, String userId, String type, String title, String message, NotificationCallback callback) {
        if (userId == null || userId.isEmpty()) {
            if (callback != null) {
                callback.onError("User ID is empty");
            }
            return;
        }
        
        Map<String, Object> notification = new HashMap<>();
        notification.put("userId", userId);
        notification.put("type", type);
        notification.put("title", title);
        notification.put("message", message);
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        notification.put("created_at", com.google.firebase.Timestamp.now());
        
        db.collection(COLLECTION_NAME)
            .add(notification)
            .addOnSuccessListener(documentReference -> {
                if (callback != null) {
                    callback.onSuccess(documentReference.getId());
                }
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            });
    }
    
    /**
     * Mark a notification as read
     */
    public static void markAsRead(String notificationId) {
        db.collection(COLLECTION_NAME)
            .document(notificationId)
            .update("read", true)
            .addOnFailureListener(e -> {
                // Handle error silently
            });
    }
    
    /**
     * Delete a notification
     */
    public static void deleteNotification(String notificationId) {
        db.collection(COLLECTION_NAME)
            .document(notificationId)
            .delete()
            .addOnFailureListener(e -> {
                // Handle error silently
            });
    }
    
    /**
     * Get unread notification count for a user
     */
    public static void getUnreadCount(String userId, UnreadCountCallback callback) {
        db.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .whereEqualTo("read", false)
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (callback != null) {
                    callback.onCountReceived(queryDocumentSnapshots.size());
                }
            })
            .addOnFailureListener(e -> {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            });
    }
    
    // Callback interfaces
    public interface NotificationCallback {
        void onSuccess(String notificationId);
        void onError(String error);
    }
    
    public interface UnreadCountCallback {
        void onCountReceived(int count);
        void onError(String error);
    }
    
    // Helper methods for common notifications
    
    public static void notifyCanteenOrder(Context context, String userId, String orderId, double amount) {
        String title = "🍽️ Order Placed";
        String message = String.format("Your canteen order #%s for ₹%.2f has been placed successfully!", orderId, amount);
        sendNotification(context, userId, TYPE_CANTEEN_ORDER, title, message);
    }
    
    public static void notifyRoomBooking(Context context, String userId, String bookingId, String roomName, String date) {
        String title = "📅 Room Booked";
        String message = String.format("Room %s has been booked for %s. Booking ID: %s", roomName, date, bookingId);
        sendNotification(context, userId, TYPE_ROOM_BOOKING, title, message);
    }
    
    public static void notifyEventRegistration(Context context, String userId, String eventName) {
        String title = "🎉 Event Registration";
        String message = String.format("You have successfully registered for %s", eventName);
        sendNotification(context, userId, TYPE_EVENT_REGISTRATION, title, message);
    }
    
    public static void notifyClubJoin(Context context, String userId, String clubName) {
        String title = "🎭 Club Membership";
        String message = String.format("You have joined %s. Welcome!", clubName);
        sendNotification(context, userId, TYPE_CLUB_JOIN, title, message);
    }
    
    public static void notifyTimetableUpdate(Context context, String userId) {
        String title = "📚 Timetable Updated";
        String message = "Your timetable has been updated. Check the Timetable section for details.";
        sendNotification(context, userId, TYPE_TIMETABLE_UPDATE, title, message);
    }
    
    public static void notifyExtraClass(Context context, String userId, String subject, String date, String time) {
        String title = "📖 Extra Class Scheduled";
        String message = String.format("Extra class for %s scheduled on %s at %s", subject, date, time);
        sendNotification(context, userId, TYPE_EXTRA_CLASS, title, message);
    }
    
    public static void notifyAssignment(Context context, String userId, String subject, String dueDate) {
        String title = "📝 New Assignment";
        String message = String.format("New assignment for %s. Due date: %s", subject, dueDate);
        sendNotification(context, userId, TYPE_ASSIGNMENT, title, message);
    }
}
