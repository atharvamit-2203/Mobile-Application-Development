package com.campusbuddy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

// Firebase helper for authentication and database
public class FirebaseHelper {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface AuthCallback {
        void onSuccess(String userId);
        void onError(String error);
    }

    public interface LoginCallback {
        void onSuccess(FirebaseUser user, String role);
        void onError(String error);
    }
    
    public interface DataCallback {
        void onSuccess(List<Map<String, Object>> data);
        void onError(String error);
    }

    public interface SingleDataCallback {
        void onSuccess(Map<String, Object> data);
        void onError(String error);
    }

    // Register new user with email and password
    public static void register(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    callback.onSuccess(user.getUid());
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Create user profile in Firestore
    public static void createUserProfile(String userId, Map<String, Object> userData, SingleDataCallback callback) {
        db.collection("users").document(userId)
            .set(userData)
            .addOnSuccessListener(aVoid -> {
                userData.put("id", userId);
                callback.onSuccess(userData);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Login with email and password
    public static void login(String email, String password, LoginCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                FirebaseUser user = authResult.getUser();
                if (user != null) {
                    // Get user role from Firestore
                    db.collection("users").document(user.getUid())
                        .get()
                        .addOnSuccessListener(doc -> {
                            String role = doc.getString("role");
                            if (role == null) role = "student";
                            callback.onSuccess(user, role);
                        })
                        .addOnFailureListener(e -> callback.onError(e.getMessage()));
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get current user
    public static FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // Logout
    public static void logout() {
        auth.signOut();
    }
    
    // Get user data
    public static void getUserData(String userId, SingleDataCallback callback) {
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    callback.onSuccess(doc.getData());
                } else {
                    callback.onError("User not found");
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get clubs for student
    public static void getClubs(DataCallback callback) {
        db.collection("clubs")
            .whereEqualTo("is_active", true) // Only get active clubs
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> clubs = new ArrayList<>();
                java.util.Set<String> seenIds = new java.util.HashSet<>();
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String docId = doc.getId();
                    
                    // Skip duplicates based on document ID
                    if (seenIds.contains(docId)) {
                        continue;
                    }
                    seenIds.add(docId);
                    
                    Map<String, Object> club = doc.getData();
                    club.put("id", docId);
                    clubs.add(club);
                }
                callback.onSuccess(clubs);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get events
    public static void getEvents(DataCallback callback) {
        db.collection("events")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> event = doc.getData();
                    event.put("id", doc.getId());
                    events.add(event);
                }
                callback.onSuccess(events);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get bookings
    public static void getBookings(String userId, DataCallback callback) {
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> bookings = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> booking = doc.getData();
                    booking.put("id", doc.getId());
                    bookings.add(booking);
                }
                callback.onSuccess(bookings);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Create event
    public static void createEvent(Map<String, Object> eventData, SingleDataCallback callback) {
        db.collection("events")
            .add(eventData)
            .addOnSuccessListener(docRef -> {
                eventData.put("id", docRef.getId());
                callback.onSuccess(eventData);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Create booking
    public static void createBooking(Map<String, Object> bookingData, SingleDataCallback callback) {
        db.collection("bookings")
            .add(bookingData)
            .addOnSuccessListener(docRef -> {
                bookingData.put("id", docRef.getId());
                callback.onSuccess(bookingData);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Update user profile
    public static void updateUserProfile(String userId, Map<String, Object> updates, SingleDataCallback callback) {
        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener(v -> callback.onSuccess(updates))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ===== CANTEEN METHODS =====
    
    // Get canteen menu items
    public static void getCanteenMenu(DataCallback callback) {
        db.collection("canteen_items")
            .whereEqualTo("is_available", true)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> items = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> item = doc.getData();
                    item.put("id", doc.getId());
                    items.add(item);
                }
                callback.onSuccess(items);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Place canteen order
    public static void placeCanteenOrder(Map<String, Object> orderData, SingleDataCallback callback) {
        db.collection("canteen_orders")
            .add(orderData)
            .addOnSuccessListener(docRef -> {
                orderData.put("id", docRef.getId());
                callback.onSuccess(orderData);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get user's canteen orders
    public static void getCanteenOrders(String userId, DataCallback callback) {
        db.collection("canteen_orders")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> orders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> order = doc.getData();
                    order.put("id", doc.getId());
                    orders.add(order);
                }
                
                // Sort orders by date in descending order (newest first) in the app
                orders.sort((o1, o2) -> {
                    try {
                        Object date1 = o1.get("order_date");
                        Object date2 = o2.get("order_date");
                        
                        // Handle timestamp comparison
                        if (date1 instanceof com.google.firebase.Timestamp && date2 instanceof com.google.firebase.Timestamp) {
                            return ((com.google.firebase.Timestamp) date2).compareTo((com.google.firebase.Timestamp) date1);
                        }
                        
                        // Handle string comparison
                        if (date1 != null && date2 != null) {
                            return date2.toString().compareTo(date1.toString());
                        }
                        
                        return 0;
                    } catch (Exception e) {
                        return 0;
                    }
                });
                
                callback.onSuccess(orders);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ===== CLUBS METHODS =====
    
    // Join a club (simple join request)
    public static void joinClub(String clubId, String userId, SingleDataCallback callback) {
        // First, get student information
        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener(userDoc -> {
                if (userDoc.exists()) {
                    Map<String, Object> userData = userDoc.getData();
                    
                    // Create member request with all required fields
                    Map<String, Object> request = new HashMap<>();
                    request.put("organizationId", clubId);
                    request.put("studentId", userId);
                    request.put("studentName", userData.get("name"));
                    request.put("studentEmail", userData.get("email"));
                    request.put("course", userData.get("course"));
                    request.put("semester", userData.get("semester"));
                    request.put("message", ""); // Optional message
                    request.put("status", "pending");
                    request.put("timestamp", System.currentTimeMillis());
                    
                    // Save to member_requests collection (used by MemberRequestsActivity)
                    db.collection("member_requests")
                        .add(request)
                        .addOnSuccessListener(docRef -> {
                            request.put("id", docRef.getId());
                            callback.onSuccess(request);
                        })
                        .addOnFailureListener(e -> callback.onError(e.getMessage()));
                } else {
                    callback.onError("User not found");
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get user's clubs (where they are members)
    public static void getMyClubs(String userId, DataCallback callback) {
        db.collection("club_memberships")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> clubIds = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    clubIds.add(doc.getData());
                }
                
                // Get full club details
                List<Map<String, Object>> clubs = new ArrayList<>();
                for (Map<String, Object> membership : clubIds) {
                    String clubId = (String) membership.get("club_id");
                    db.collection("clubs").document(clubId)
                        .get()
                        .addOnSuccessListener(clubDoc -> {
                            if (clubDoc.exists()) {
                                Map<String, Object> club = clubDoc.getData();
                                club.put("id", clubDoc.getId());
                                clubs.add(club);
                            }
                            if (clubs.size() == clubIds.size()) {
                                callback.onSuccess(clubs);
                            }
                        });
                }
                
                if (clubIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get club events
    public static void getClubEvents(DataCallback callback) {
        db.collection("club_events")
            .whereEqualTo("status", "approved")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> events = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> event = doc.getData();
                    event.put("id", doc.getId());
                    events.add(event);
                }
                callback.onSuccess(events);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Register for event
    public static void registerForEvent(String eventId, String userId, SingleDataCallback callback) {
        Map<String, Object> registration = new HashMap<>();
        registration.put("event_id", eventId);
        registration.put("user_id", userId);
        registration.put("registered_at", System.currentTimeMillis());
        
        db.collection("event_registrations")
            .add(registration)
            .addOnSuccessListener(docRef -> {
                registration.put("id", docRef.getId());
                callback.onSuccess(registration);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ===== NOTIFICATIONS METHODS =====
    
    // Get user notifications
    public static void getNotifications(String userId, DataCallback callback) {
        db.collection("notifications")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> notifications = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> notif = doc.getData();
                    notif.put("id", doc.getId());
                    notifications.add(notif);
                }
                callback.onSuccess(notifications);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Mark notification as read
    public static void markNotificationRead(String notificationId, SingleDataCallback callback) {
        db.collection("notifications").document(notificationId)
            .update("is_read", true)
            .addOnSuccessListener(v -> {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Check room availability (for faculty and organization)
    public static void checkRoomAvailability(String room, String date, String startTime, String endTime, DataCallback callback) {
        db.collection("bookings")
            .whereEqualTo("room_number", room)
            .whereEqualTo("date", date)
            .whereEqualTo("status", "confirmed")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> conflicts = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> booking = doc.getData();
                    String bookingStart = (String) booking.get("start_time");
                    String bookingEnd = (String) booking.get("end_time");
                    
                    // Check for time overlap
                    if (timesOverlap(startTime, endTime, bookingStart, bookingEnd)) {
                        booking.put("id", doc.getId());
                        conflicts.add(booking);
                    }
                }
                callback.onSuccess(conflicts);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    private static boolean timesOverlap(String start1, String end1, String start2, String end2) {
        // Simple string comparison (works for HH:mm format)
        return !(end1.compareTo(start2) <= 0 || start1.compareTo(end2) >= 0);
    }
    
    // Create room booking
    public static void createRoomBooking(Map<String, Object> booking, SingleDataCallback callback) {
        db.collection("bookings")
            .add(booking)
            .addOnSuccessListener(docRef -> {
                Map<String, Object> result = new HashMap<>();
                result.put("id", docRef.getId());
                result.put("success", true);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Create club event
    public static void createClubEvent(Map<String, Object> event, SingleDataCallback callback) {
        db.collection("club_events")
            .add(event)
            .addOnSuccessListener(docRef -> {
                Map<String, Object> result = new HashMap<>();
                result.put("id", docRef.getId());
                result.put("success", true);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get timetable for user and day
    public static void getTimetable(String userId, String day, DataCallback callback) {
        db.collection("timetable")
            .whereEqualTo("user_id", userId)
            .whereEqualTo("day", day)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> classes = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> classData = doc.getData();
                    classData.put("id", doc.getId());
                    classes.add(classData);
                }
                callback.onSuccess(classes);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Add class to timetable
    public static void addTimetableClass(Map<String, Object> classData, SingleDataCallback callback) {
        db.collection("timetable")
            .add(classData)
            .addOnSuccessListener(docRef -> {
                Map<String, Object> result = new HashMap<>();
                result.put("id", docRef.getId());
                result.put("success", true);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get order by ID
    public static void getOrderById(String orderId, SingleDataCallback callback) {
        db.collection("canteen_orders")
            .document(orderId)
            .get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    Map<String, Object> order = doc.getData();
                    order.put("id", doc.getId());
                    callback.onSuccess(order);
                } else {
                    callback.onError("Order not found");
                }
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Update order status
    public static void updateOrder(String orderId, Map<String, Object> updates, SingleDataCallback callback) {
        db.collection("canteen_orders")
            .document(orderId)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get pending orders for staff
    public static void getPendingOrders(DataCallback callback) {
        db.collection("canteen_orders")
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> orders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> order = doc.getData();
                    order.put("id", doc.getId());
                    orders.add(order);
                }
                callback.onSuccess(orders);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
    
    // Get all orders for staff management
    public static void getAllOrders(DataCallback callback) {
        db.collection("canteen_orders")
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> orders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> order = doc.getData();
                    order.put("id", doc.getId());
                    orders.add(order);
                }
                callback.onSuccess(orders);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Get user's club memberships
    public static void getUserMemberships(String userId, DataCallback callback) {
        db.collection("member_requests")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> memberships = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> membership = doc.getData();
                    membership.put("id", doc.getId());
                    memberships.add(membership);
                }
                callback.onSuccess(memberships);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Add club membership request
    public static void addMemberRequest(Map<String, Object> request, DataCallback callback) {
        db.collection("member_requests")
            .add(request)
            .addOnSuccessListener(documentReference -> {
                List<Map<String, Object>> result = new ArrayList<>();
                Map<String, Object> data = new HashMap<>(request);
                data.put("id", documentReference.getId());
                result.add(data);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Mark student attendance
    public static void markAttendance(Map<String, Object> attendanceData, DataCallback callback) {
        db.collection("attendance")
            .add(attendanceData)
            .addOnSuccessListener(documentReference -> {
                List<Map<String, Object>> result = new ArrayList<>();
                Map<String, Object> data = new HashMap<>(attendanceData);
                data.put("id", documentReference.getId());
                result.add(data);
                callback.onSuccess(result);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // Update canteen order payment status
    public static void updateCanteenOrderStatus(String orderId, String paymentStatus, SingleDataCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("payment_status", paymentStatus);
        updates.put("updated_at", System.currentTimeMillis());

        db.collection("canteen_orders").document(orderId)
            .update(updates)
            .addOnSuccessListener(aVoid -> callback.onSuccess(updates))
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
