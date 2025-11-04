package com.campusbuddy;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;


public class FirebaseSetup {
    
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();
    
    public interface SetupCallback {
        void onComplete(String message);
        void onError(String error);
    }
    
    public static void setupFirestore(SetupCallback callback) {
        // First, create a test student user
        String email = "test@student.com";
        String password = "password123";
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(authResult -> {
                String userId = authResult.getUser().getUid();
                
                // Create all collections with sample data
                createUserCollections(userId, callback);
                createCollegeCollections(callback);
                createClubCollections(callback);
                createEventCollections(callback);
                createBookingCollections(userId, callback);
                createNotificationCollections(userId, callback);
                
                callback.onComplete("Firebase setup complete! Test user created: " + email);
            })
            .addOnFailureListener(e -> {
                // If user already exists, that's okay
                if (e.getMessage().contains("already")) {
                    callback.onComplete("Test user already exists. Collections will be created.");
                    createSampleCollections(callback);
                } else {
                    callback.onError("Setup failed: " + e.getMessage());
                }
            });
    }
    
    private static void createSampleCollections(SetupCallback callback) {
        createCollegeCollections(callback);
        createClubCollections(callback);
        createEventCollections(callback);
    }
    
    private static void createUserCollections(String userId, SetupCallback callback) {
        // Create users collection with student data (matching your backend User model)
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", "test_student");
        userData.put("email", "test@student.com");
        userData.put("full_name", "Test Student");
        userData.put("role", "student");
        userData.put("college_id", 1);
        
        // Student specific fields
        userData.put("student_id", "STU001");
        userData.put("course", "B.Tech");
        userData.put("branch", "Computer Science");
        userData.put("semester", "6");
        userData.put("academic_year", "2024-25");
        userData.put("batch", "2022-26");
        userData.put("cgpa", 8.5);
        userData.put("phone_number", "+91 9876543210");
        userData.put("department", "Computer Engineering");
        userData.put("bio", "Passionate about coding and technology");
        userData.put("skills", Arrays.asList("Java", "Python", "React", "Firebase"));
        userData.put("interests", Arrays.asList("AI", "Mobile Development", "Cloud Computing"));
        userData.put("is_active", true);
        userData.put("is_verified", true);
        userData.put("created_at", System.currentTimeMillis());
        userData.put("updated_at", System.currentTimeMillis());
        
        db.collection("users").document(userId).set(userData);
        
        // Create additional sample users
        createSampleUser("faculty@college.com", "Faculty Member", "faculty");
        createSampleUser("admin@college.com", "Admin User", "admin");
        createSampleUser("org@college.com", "Organization Head", "organization");
    }
    
    private static void createSampleUser(String email, String name, String role) {
        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("full_name", name);
        user.put("role", role);
        user.put("is_active", true);
        user.put("created_at", System.currentTimeMillis());
        
        if (role.equals("faculty")) {
            user.put("employee_id", "FAC001");
            user.put("designation", "Assistant Professor");
            user.put("department", "Computer Engineering");
            user.put("specialization", "Machine Learning");
            user.put("experience_years", 5);
        } else if (role.equals("organization")) {
            user.put("organization_type", "Technical Club");
            user.put("department", "Coding Club");
        }
        
        db.collection("users").add(user);
    }
    
    private static void createCollegeCollections(SetupCallback callback) {
        // Create colleges collection (matching College model)
        Map<String, Object> college = new HashMap<>();
        college.put("name", "MPSTME Mumbai");
        college.put("code", "MPSTME");
        college.put("type", "Engineering College");
        college.put("address", "SVKM Campus, Vile Parle West");
        college.put("city", "Mumbai");
        college.put("state", "Maharashtra");
        college.put("pincode", "400056");
        college.put("contact_email", "info@mpstme.edu.in");
        college.put("contact_phone", "+91-22-12345678");
        college.put("website_url", "https://mpstme.nmims.edu");
        college.put("established_year", 2006);
        college.put("is_active", true);
        college.put("created_at", System.currentTimeMillis());
        
        db.collection("colleges").add(college);
    }
    
    private static void createClubCollections(SetupCallback callback) {
        // Create clubs collection (matching Club model)
        String[] clubs = {"Coding Club", "Robotics Club", "Music Club", "Dance Club", "Drama Club"};
        String[] categories = {"Technical", "Technical", "Cultural", "Cultural", "Cultural"};
        
        for (int i = 0; i < clubs.length; i++) {
            Map<String, Object> club = new HashMap<>();
            club.put("name", clubs[i]);
            club.put("description", "Official " + clubs[i] + " of MPSTME");
            club.put("category", categories[i]);
            club.put("college_id", 1);
            club.put("max_members", 100);
            club.put("member_count", 0);
            club.put("is_active", true);
            club.put("created_at", System.currentTimeMillis());
            club.put("updated_at", System.currentTimeMillis());
            
            db.collection("clubs").add(club);
        }
    }
    
    private static void createEventCollections(SetupCallback callback) {
        // Create events collection (matching EventCreate/EventResponse)
        Map<String, Object> event = new HashMap<>();
        event.put("title", "Tech Workshop: Firebase for Beginners");
        event.put("description", "Learn how to integrate Firebase in your mobile apps");
        event.put("event_type", "workshop");
        event.put("start_time", System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)); // 7 days from now
        event.put("end_time", System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L) + (2 * 60 * 60 * 1000L)); // +2 hours
        event.put("venue", "Room 301, Building A");
        event.put("max_participants", 50);
        event.put("current_participants", 0);
        event.put("is_public", true);
        event.put("college_id", 1);
        event.put("created_at", System.currentTimeMillis());
        
        db.collection("events").add(event);
        
        // Add another event
        Map<String, Object> event2 = new HashMap<>();
        event2.put("title", "Annual Tech Fest");
        event2.put("description", "College-wide technology festival with competitions");
        event2.put("event_type", "fest");
        event2.put("start_time", System.currentTimeMillis() + (14 * 24 * 60 * 60 * 1000L));
        event2.put("end_time", System.currentTimeMillis() + (16 * 24 * 60 * 60 * 1000L));
        event2.put("venue", "Main Campus");
        event2.put("max_participants", 500);
        event2.put("current_participants", 0);
        event2.put("is_public", true);
        event2.put("college_id", 1);
        event2.put("created_at", System.currentTimeMillis());
        
        db.collection("events").add(event2);
    }
    
    private static void createBookingCollections(String userId, SetupCallback callback) {
        // Create bookings collection (matching BookingCreate/BookingResponse)
        Map<String, Object> booking = new HashMap<>();
        booking.put("title", "Data Structures Lecture");
        booking.put("subject", "Computer Science");
        booking.put("description", "Advanced topics in Data Structures");
        booking.put("booking_type", "lecture");
        booking.put("booking_date", System.currentTimeMillis());
        booking.put("start_time", "10:00");
        booking.put("end_time", "11:30");
        booking.put("room_id", "101");
        booking.put("status", "approved");
        booking.put("created_by", userId);
        booking.put("created_by_type", "faculty");
        booking.put("created_at", System.currentTimeMillis());
        
        db.collection("bookings").add(booking);
    }
    
    private static void createNotificationCollections(String userId, SetupCallback callback) {
        // Create notifications collection (matching Notification model)
        Map<String, Object> notification = new HashMap<>();
        notification.put("user_id", userId);
        notification.put("title", "Welcome to Campus Buddy!");
        notification.put("message", "Your account has been created successfully");
        notification.put("type", "general");
        notification.put("is_read", false);
        notification.put("priority", "high");
        notification.put("created_at", System.currentTimeMillis());
        
        db.collection("notifications").add(notification);
    }
    
    // Create organization_applications collection (matching OrganizationApplication model)
    public static void createOrganizationApplication(String clubId, String userId, Map<String, Object> applicationData, FirebaseHelper.SingleDataCallback callback) {
        Map<String, Object> application = new HashMap<>();
        application.put("club_id", clubId);
        application.put("user_id", userId);
        application.put("full_name", applicationData.get("full_name"));
        application.put("batch", applicationData.get("batch"));
        application.put("year_of_study", applicationData.get("year_of_study"));
        application.put("sap_id", applicationData.get("sap_id"));
        application.put("department_to_join", applicationData.get("department_to_join"));
        application.put("why_join", applicationData.get("why_join"));
        application.put("what_contribute", applicationData.get("what_contribute"));
        application.put("can_stay_longer_hours", applicationData.getOrDefault("can_stay_longer_hours", false));
        application.put("status", "pending");
        application.put("created_at", System.currentTimeMillis());
        application.put("updated_at", System.currentTimeMillis());
        
        db.collection("organization_applications")
            .add(application)
            .addOnSuccessListener(docRef -> {
                application.put("id", docRef.getId());
                callback.onSuccess(application);
            })
            .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
