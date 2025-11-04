package com.campusbuddy;

import android.content.Context;
import android.util.Log;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiService {
    private static final String TAG = "ApiService";
    private static final String BASE_URL = "https://your-backend-url.com"; // TODO: Update with actual URL
    // API key stored in BuildConfig for security
    private static final String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;
    
    private static OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build();
    
    public interface TimetableUploadCallback {
        void onSuccess(List<Map<String, Object>> timetableEntries);
        void onError(String error);
    }
    
    /**
     * Upload timetable image to Gemini API for OCR processing
     */
    public static void uploadTimetableImage(Context context, File imageFile, TimetableUploadCallback callback) {
        String userId = Prefs.getInstance(context).getUserId();
        String token = Prefs.getInstance(context).getToken();
        
        if (userId == null || token == null) {
            callback.onError("User not logged in");
            return;
        }
        
        // Use Gemini API directly since we have the API key
        uploadToGeminiAPI(imageFile, userId, callback);
    }
    
    private static void uploadToGeminiAPI(File imageFile, String userId, TimetableUploadCallback callback) {
        new Thread(() -> {
            try {
                // Read image file as base64
                byte[] imageBytes = java.nio.file.Files.readAllBytes(imageFile.toPath());
                String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.NO_WRAP);
                
                // Prepare Gemini API request
                String geminiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash-exp:generateContent?key=" + GEMINI_API_KEY;
                
                JSONObject requestBody = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject content = new JSONObject();
                JSONArray parts = new JSONArray();
                
                // Add text prompt
                JSONObject textPart = new JSONObject();
                textPart.put("text", 
                    "Extract timetable as JSON array. Each entry: {\"day_of_week\": \"Monday\", \"start_time\": \"HH:MM\", \"end_time\": \"HH:MM\", \"subject\": \"subject name\", \"room\": \"room number\", \"faculty\": \"faculty name\"}. " +
                    "CRITICAL: Use 24-hour format (08:00-18:00 for 8 AM-6 PM). Morning lectures = 08:00-12:00, Afternoon = 13:00-18:00. " +
                    "Skip lunch breaks. Return ONLY a JSON array, no other text.");
                parts.put(textPart);
                
                // Add image
                JSONObject imagePart = new JSONObject();
                JSONObject inlineData = new JSONObject();
                inlineData.put("mime_type", "image/jpeg");
                inlineData.put("data", base64Image);
                imagePart.put("inline_data", inlineData);
                parts.put(imagePart);
                
                content.put("parts", parts);
                contents.put(content);
                requestBody.put("contents", contents);
                
                // Make API request
                RequestBody body = RequestBody.create(
                    requestBody.toString(),
                    MediaType.parse("application/json")
                );
                
                Request request = new Request.Builder()
                    .url(geminiUrl)
                    .post(body)
                    .build();
                
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                
                Log.d(TAG, "Gemini API Response: " + responseBody);
                
                if (!response.isSuccessful()) {
                    throw new IOException("API call failed: " + response.code() + " " + responseBody);
                }
                
                // Parse response
                JSONObject jsonResponse = new JSONObject(responseBody);
                JSONArray candidates = jsonResponse.getJSONArray("candidates");
                if (candidates.length() == 0) {
                    throw new Exception("No candidates in response");
                }
                
                JSONObject candidate = candidates.getJSONObject(0);
                JSONObject contentObj = candidate.getJSONObject("content");
                JSONArray partsArray = contentObj.getJSONArray("parts");
                String textResponse = partsArray.getJSONObject(0).getString("text");
                
                Log.d(TAG, "Extracted text: " + textResponse);
                
                // Parse timetable JSON from response
                String cleanedJson = extractJsonArray(textResponse);
                JSONArray timetableArray = new JSONArray(cleanedJson);
                
                // Convert to List<Map>
                List<Map<String, Object>> timetableEntries = new ArrayList<>();
                for (int i = 0; i < timetableArray.length(); i++) {
                    JSONObject entry = timetableArray.getJSONObject(i);
                    java.util.HashMap<String, Object> map = new java.util.HashMap<>();
                    map.put("day_of_week", entry.optString("day_of_week", ""));
                    map.put("start_time", entry.optString("start_time", ""));
                    map.put("end_time", entry.optString("end_time", ""));
                    map.put("subject", entry.optString("subject", ""));
                    map.put("room", entry.optString("room", ""));
                    map.put("faculty", entry.optString("faculty", ""));
                    map.put("user_id", userId);
                    
                    timetableEntries.add(map);
                }
                
                // Save to Firebase
                saveTimetableToFirebase(userId, timetableEntries, callback);
                
            } catch (Exception e) {
                Log.e(TAG, "Error uploading timetable", e);
                callback.onError("Failed to process timetable: " + e.getMessage());
            }
        }).start();
    }
    
    private static String extractJsonArray(String text) {
        // Remove markdown code blocks
        text = text.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "");
        
        // Find first [ and last ]
        int start = text.indexOf('[');
        int end = text.lastIndexOf(']');
        
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        
        return text;
    }
    
    private static void saveTimetableToFirebase(String userId, List<Map<String, Object>> entries, TimetableUploadCallback callback) {
        // Delete existing timetable entries for this user
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
            .collection("timetable")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                // Delete old entries
                for (com.google.firebase.firestore.DocumentSnapshot doc : querySnapshot.getDocuments()) {
                    doc.getReference().delete();
                }
                
                // Add new entries
                for (Map<String, Object> entry : entries) {
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("timetable")
                        .add(entry);
                }
                
                callback.onSuccess(entries);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving to Firebase", e);
                callback.onError("Failed to save timetable: " + e.getMessage());
            });
    }
}
