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

        Prefs prefs = Prefs.getInstance(this);
        String role = prefs.getUserRole();
        if ("student".equalsIgnoreCase(role)) {
            generateStudentQR();
            qrLayout.setVisibility(android.view.View.VISIBLE);
        } else {
            qrLayout.setVisibility(android.view.View.GONE);
        }

        btnEditProfile.setOnClickListener(v ->
            startActivity(new Intent(this, EditProfileActivity.class))
        );

        btnChangePassword.setOnClickListener(v -> changePassword());

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
    }

    private void loadProfile() {
        Prefs prefs = Prefs.getInstance(this);

        String name = prefs.getName();
        String email = prefs.getEmail();
        String role = prefs.getUserRole();
        String userId = prefs.getUserId();

        tvName.setText(name != null ? name : "N/A");
        tvEmail.setText(email != null ? email : "N/A");
        tvRole.setText(role != null ? role.toUpperCase() : "N/A");
        tvSapId.setText(userId != null ? userId : "N/A");

        if (name != null && !name.isEmpty()) {
            tvInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }
    }

    private void generateStudentQR() {
        try {
            Prefs prefs = Prefs.getInstance(this);

            JSONObject qrData = new JSONObject();
            qrData.put("type", "student_id");
            qrData.put("userId", prefs.getUserId());
            qrData.put("name", prefs.getName());
            qrData.put("email", prefs.getEmail());
            qrData.put("role", prefs.getUserRole());
            qrData.put("timestamp", System.currentTimeMillis());

            String qrContent = qrData.toString();

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
            ivStudentQR.setVisibility(android.view.View.GONE);

        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Change Password");
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        final EditText oldPassword = new EditText(this);
        oldPassword.setHint("Current Password");
        oldPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(oldPassword);
        
        final EditText newPassword = new EditText(this);
        newPassword.setHint("New Password");
        newPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(newPassword);
        
        final EditText confirmPassword = new EditText(this);
        confirmPassword.setHint("Confirm New Password");
        confirmPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(confirmPassword);
        
        builder.setView(layout);
        
        builder.setPositiveButton("Change", (dialog, which) -> {
            String oldPass = oldPassword.getText().toString().trim();
            String newPass = newPassword.getText().toString().trim();
            String confirmPass = confirmPassword.getText().toString().trim();
            
            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (newPass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updatePassword(newPass)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            }
        });
        
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
