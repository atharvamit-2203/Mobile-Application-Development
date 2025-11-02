package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import android.content.Intent;
import org.json.JSONObject;
import java.util.*;

// Staff activity to scan QR codes and verify orders
@SuppressWarnings("deprecation")
public class StaffOrderScannerActivity extends Activity {

    private TextView tvScanStatus, tvOrderDetails;
    private Button btnScanQR, btnVerifyOrder, btnRejectOrder;
    private LinearLayout orderDetailsLayout;
    
    private String scannedOrderId;
    private double scannedAmount;
    private String scannedUserId;
    private String scannedUserName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_order_scanner);
        
        tvScanStatus = findViewById(R.id.tvScanStatus);
        tvOrderDetails = findViewById(R.id.tvOrderDetails);
        btnScanQR = findViewById(R.id.btnScanQR);
        btnVerifyOrder = findViewById(R.id.btnVerifyOrder);
        btnRejectOrder = findViewById(R.id.btnRejectOrder);
        orderDetailsLayout = findViewById(R.id.orderDetailsLayout);
        
        orderDetailsLayout.setVisibility(android.view.View.GONE);
        
        btnScanQR.setOnClickListener(v -> startQRScanner());
        btnVerifyOrder.setOnClickListener(v -> verifyOrder());
        btnRejectOrder.setOnClickListener(v -> rejectOrder());
    }
    
    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan Order QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false); // Allow rotation
        integrator.initiateScan();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show();
            } else {
                processQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
    private void processQRCode(String qrContent) {
        try {
            JSONObject qrData = new JSONObject(qrContent);
            
            String type = qrData.getString("type");
            if (!type.equals("canteen_order")) {
                Toast.makeText(this, "Invalid QR code type", Toast.LENGTH_SHORT).show();
                return;
            }
            
            scannedOrderId = qrData.getString("orderId");
            scannedAmount = qrData.getDouble("amount");
            scannedUserId = qrData.getString("userId");
            scannedUserName = qrData.getString("userName");
            long timestamp = qrData.getLong("timestamp");
            
            // Check if QR is not too old (valid for 1 hour)
            long currentTime = System.currentTimeMillis();
            if (currentTime - timestamp > 3600000) {
                Toast.makeText(this, "QR code expired. Please generate new one.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Load order details from Firebase
            loadOrderDetails();
            
        } catch (Exception e) {
            Toast.makeText(this, "Invalid QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void loadOrderDetails() {
        FirebaseHelper.getOrderById(scannedOrderId, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> order) {
                displayOrderDetails(order);
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(StaffOrderScannerActivity.this, "Error loading order: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void displayOrderDetails(Map<String, Object> order) {
        String status = (String) order.get("status");
        
        if (status.equals("verified") || status.equals("completed")) {
            tvScanStatus.setText("⚠️ Order Already Verified");
            tvScanStatus.setTextColor(0xFFF59E0B);
            orderDetailsLayout.setVisibility(android.view.View.GONE);
            return;
        }
        
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(scannedOrderId).append("\n\n");
        details.append("Customer: ").append(scannedUserName).append("\n\n");
        details.append("Amount: ₹").append(scannedAmount).append("\n\n");
        details.append("Items:\n");
        
        List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                String name = (String) item.get("item_name");
                int quantity = ((Number) item.get("quantity")).intValue();
                details.append("  • ").append(name).append(" x ").append(quantity).append("\n");
            }
        }
        
        tvOrderDetails.setText(details.toString());
        tvScanStatus.setText("✅ Valid Order - Ready to Verify");
        tvScanStatus.setTextColor(0xFF10B981);
        orderDetailsLayout.setVisibility(android.view.View.VISIBLE);
    }
    
    private void verifyOrder() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "verified");
        updates.put("verified_at", System.currentTimeMillis());
        updates.put("verified_by", Prefs.getInstance(this).getUserId());
        
        FirebaseHelper.updateOrder(scannedOrderId, updates, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(StaffOrderScannerActivity.this, "✅ Order verified! Please prepare the order.", Toast.LENGTH_LONG).show();
                resetScanner();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(StaffOrderScannerActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void rejectOrder() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("rejected_at", System.currentTimeMillis());
        updates.put("rejected_by", Prefs.getInstance(this).getUserId());
        
        FirebaseHelper.updateOrder(scannedOrderId, updates, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(StaffOrderScannerActivity.this, "Order rejected", Toast.LENGTH_SHORT).show();
                resetScanner();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(StaffOrderScannerActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void resetScanner() {
        scannedOrderId = null;
        tvOrderDetails.setText("");
        tvScanStatus.setText("Scan a QR code to verify order");
        tvScanStatus.setTextColor(0xFFFFFFFF);
        orderDetailsLayout.setVisibility(android.view.View.GONE);
    }
}

