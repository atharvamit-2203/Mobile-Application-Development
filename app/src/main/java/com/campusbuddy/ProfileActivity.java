package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.json.JSONObject;

// User profile view
public class ProfileActivity extends Activity {

    private TextView tvName, tvEmail, tvRole, tvSapId, tvInitial;
    private Button btnEditProfile, btnChangePassword, btnViewQR, btnOrderHistory;
    private ImageView ivStudentQR;
    private LinearLayout qrLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvRole = findViewById(R.id.tvRole);
        tvSapId = findViewById(R.id.tvSapId);
        tvInitial = findViewById(R.id.tvInitial);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnViewQR = findViewById(R.id.btnViewQR);
        btnOrderHistory = findViewById(R.id.btnOrderHistory);
        ivStudentQR = findViewById(R.id.ivStudentQR);
        qrLayout = findViewById(R.id.qrLayout);

        loadProfile();
        
        // Generate student QR code for students
        Prefs prefs = Prefs.getInstance(this);
        String role = prefs.getUserRole();
        if ("student".equalsIgnoreCase(role)) {
            generateStudentQR();
            qrLayout.setVisibility(android.view.View.VISIBLE);
        } else {
            qrLayout.setVisibility(android.view.View.GONE);
        }

        btnEditProfile.setOnClickListener(v ->
            Toast.makeText(this, "Edit Profile - Coming Soon", Toast.LENGTH_SHORT).show()      
        );

        btnChangePassword.setOnClickListener(v ->
            Toast.makeText(this, "Change Password - Coming Soon", Toast.LENGTH_SHORT).show()   
        );
        
        btnOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderHistoryActivity.class);
            startActivity(intent);
        });
        
        btnViewQR.setOnClickListener(v -> {
            if (ivStudentQR.getVisibility() == android.view.View.VISIBLE) {
                ivStudentQR.setVisibility(android.view.View.GONE);
                btnViewQR.setText("Show My QR Code");
            } else {
                ivStudentQR.setVisibility(android.view.View.VISIBLE);
                btnViewQR.setText("Hide QR Code");
            }
        });
    }    private void loadProfile() {
        Prefs prefs = Prefs.getInstance(this);

        String name = prefs.getName();
        String email = prefs.getEmail();
        String role = prefs.getUserRole();
        String userId = prefs.getUserId();

        tvName.setText(name != null ? name : "N/A");
        tvEmail.setText(email != null ? email : "N/A");
        tvRole.setText(role != null ? role.toUpperCase() : "N/A");
        tvSapId.setText(userId != null ? userId : "N/A");
        
        // Set initial letter for avatar
        if (name != null && !name.isEmpty()) {
            tvInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }
    
    private void generateStudentQR() {
        try {
            Prefs prefs = Prefs.getInstance(this);
            
            // Create student ID QR data
            JSONObject qrData = new JSONObject();
            qrData.put("type", "student_id");
            qrData.put("userId", prefs.getUserId());
            qrData.put("name", prefs.getName());
            qrData.put("email", prefs.getEmail());
            qrData.put("role", prefs.getUserRole());
            qrData.put("timestamp", System.currentTimeMillis());
            
            String qrContent = qrData.toString();
            
            // Generate QR code
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(qrContent, BarcodeFormat.QR_CODE, 512, 512);
            
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            
            ivStudentQR.setImageBitmap(bitmap);
            ivStudentQR.setVisibility(android.view.View.GONE); // Hidden by default
            
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
