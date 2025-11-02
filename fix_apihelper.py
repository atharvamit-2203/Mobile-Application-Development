#!/usr/bin/env python3
# -*- coding: utf-8 -*-

apihelper_code = '''package com.campusbuddy;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// Simple API helper matching Next.js fetch calls (deprecated - use FirebaseHelper instead)
public class ApiHelper {
    // Change this to your computer's IP when testing on real device
    private static final String BASE_URL = "http://10.0.2.2:8000";

    public interface Callback {
        void onSuccess(String response);
        void onError(String error);
    }

    public static void login(String email, String password, String role, Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/auth/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                json.put("email", email);
                json.put("password", password);
                json.put("role", role);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                int code = conn.getResponseCode();
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(code >= 400 ? conn.getErrorStream() : conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                String result = response.toString();
                if (code == 200) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(result);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public static void fetchData(String endpoint, Callback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                // Token authorization removed - use FirebaseHelper for authenticated requests

                int code = conn.getResponseCode();
                BufferedReader br = new BufferedReader(
                    new InputStreamReader(code >= 400 ? conn.getErrorStream() : conn.getInputStream())
                );
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                String result = response.toString();
                if (code == 200) {
                    callback.onSuccess(result);
                } else {
                    callback.onError(result);
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}
'''

# Write the file
with open(r'D:\MyApplication16\app\src\main\java\com\campusbuddy\ApiHelper.java', 'w', encoding='utf-8') as f:
    f.write(apihelper_code)

print("✅ ApiHelper.java fixed")
