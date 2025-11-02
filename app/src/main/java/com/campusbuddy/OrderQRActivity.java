package com.campusbuddy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.*;

// Display QR code for a specific order
public class OrderQRActivity extends Activity {

    private ImageView ivOrderQR;
    private TextView tvOrderId, tvOrderAmount, tvOrderStatus, tvOrderDate, tvInstructions;
    private Button btnClose;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_qr);
        
        ivOrderQR = findViewById(R.id.ivOrderQR);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderAmount = findViewById(R.id.tvOrderAmount);
        tvOrderStatus = findViewById(R.id.tvOrderStatus);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnClose = findViewById(R.id.btnClose);
        
        // Get order details from intent
        String orderId = getIntent().getStringExtra("orderId");
        double amount = getIntent().getDoubleExtra("totalAmount", 0);
        String status = getIntent().getStringExtra("status");
        long orderDate = getIntent().getLongExtra("orderDate", 0);
        
        // Display order details
        tvOrderId.setText("Order #" + orderId.substring(0, Math.min(8, orderId.length())));
        tvOrderAmount.setText("₹" + String.format("%.2f", amount));
        tvOrderStatus.setText(status.toUpperCase());
        tvOrderDate.setText(dateFormat.format(new Date(orderDate)));
        
        // Status color
        switch (status.toLowerCase()) {
            case "pending":
                tvOrderStatus.setTextColor(0xFFF59E0B);
                tvInstructions.setText("Show this QR code to staff for payment verification");
                break;
            case "verified":
                tvOrderStatus.setTextColor(0xFF3B82F6);
                tvInstructions.setText("Order verified! Collect your items from counter");
                break;
            case "completed":
                tvOrderStatus.setTextColor(0xFF10B981);
                tvInstructions.setText("Order completed. Thank you!");
                break;
            case "rejected":
                tvOrderStatus.setTextColor(0xFFEF4444);
                tvInstructions.setText("Order rejected. Please contact staff");
                break;
        }
        
        // Generate QR code
        generateOrderQR(orderId, amount);
        
        btnClose.setOnClickListener(v -> finish());
    }
    
    private void generateOrderQR(String orderId, double amount) {
        try {
            Prefs prefs = Prefs.getInstance(this);
            
            // Create QR data
            JSONObject qrData = new JSONObject();
            qrData.put("type", "canteen_order");
            qrData.put("orderId", orderId);
            qrData.put("amount", amount);
            qrData.put("userId", prefs.getUserId());
            qrData.put("userName", prefs.getName());
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
            
            ivOrderQR.setImageBitmap(bitmap);
            
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
