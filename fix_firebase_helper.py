#!/usr/bin/env python3
# -*- coding: utf-8 -*-

java_code = '''package com.campusbuddy;

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
            .get()
            .addOnSuccessListener(querySnapshot -> {
                List<Map<String, Object>> clubs = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    Map<String, Object> club = doc.getData();
                    club.put("id", doc.getId());
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
}
'''

# Write the file with UTF-8 encoding
with open(r'D:\MyApplication16\app\src\main\java\com\campusbuddy\FirebaseHelper.java', 'w', encoding='utf-8') as f:
    f.write(java_code)

print("✅ FirebaseHelper.java fixed")
