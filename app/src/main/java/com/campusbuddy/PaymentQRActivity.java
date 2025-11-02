package com.campusbuddy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.json.JSONObject;
import java.util.*;

// Show QR code for order payment
public class PaymentQRActivity extends Activity {

    private ImageView qrCodeImage;
    private TextView tvOrderId, tvAmount, tvInstructions;
    private Button btnDone;
    
    private String orderId;
    private double totalAmount;
    private List<Map<String, Object>> cartItems;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_qr);
        
        qrCodeImage = findViewById(R.id.qrCodeImage);
        tvOrderId = findViewById(R.id.tvOrderId);
        tvAmount = findViewById(R.id.tvAmount);
        tvInstructions = findViewById(R.id.tvInstructions);
        btnDone = findViewById(R.id.btnDone);
        
        // Get data from intent
        orderId = getIntent().getStringExtra("orderId");
        totalAmount = getIntent().getDoubleExtra("totalAmount", 0);
        
        tvOrderId.setText("Order ID: " + orderId);
        tvAmount.setText("Amount: ₹" + totalAmount);
        
        // Generate QR code
        generateQRCode();
        
        btnDone.setOnClickListener(v -> finish());
    }
    
    private void generateQRCode() {
        try {
            // Create JSON with order details
            JSONObject qrData = new JSONObject();
            qrData.put("type", "canteen_order");
            qrData.put("orderId", orderId);
            qrData.put("amount", totalAmount);
            qrData.put("timestamp", System.currentTimeMillis());
            qrData.put("userId", Prefs.getInstance(this).getUserId());
            qrData.put("userName", Prefs.getInstance(this).getName());
            
            String qrContent = qrData.toString();
            
            // Generate QR code bitmap
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
            
            qrCodeImage.setImageBitmap(bitmap);
            
        } catch (Exception e) {
            Toast.makeText(this, "Error generating QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
