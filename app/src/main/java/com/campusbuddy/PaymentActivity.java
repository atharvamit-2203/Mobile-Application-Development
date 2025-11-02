package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.view.View;

public class PaymentActivity extends Activity {
    
    private TextView amountText, orderIdText;
    private EditText cardNumberInput, cardHolderInput, expiryDateInput, cvvInput;
    private Button payButton, cancelButton;
    private ProgressBar progressBar;
    private View paymentFormLayout, processingLayout, successLayout;
    
    private String orderId;
    private double amount;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        
        // Get order details from intent
        orderId = getIntent().getStringExtra("orderId");
        amount = getIntent().getDoubleExtra("amount", 0.0);
        
        initViews();
        setupListeners();
        
        // Display order details
        orderIdText.setText("Order ID: " + orderId);
        amountText.setText("₹" + String.format("%.2f", amount));
    }
    
    private void initViews() {
        amountText = findViewById(R.id.amountText);
        orderIdText = findViewById(R.id.orderIdText);
        cardNumberInput = findViewById(R.id.cardNumberInput);
        cardHolderInput = findViewById(R.id.cardHolderInput);
        expiryDateInput = findViewById(R.id.expiryDateInput);
        cvvInput = findViewById(R.id.cvvInput);
        payButton = findViewById(R.id.payButton);
        cancelButton = findViewById(R.id.cancelButton);
        progressBar = findViewById(R.id.progressBar);
        paymentFormLayout = findViewById(R.id.paymentFormLayout);
        processingLayout = findViewById(R.id.processingLayout);
        successLayout = findViewById(R.id.successLayout);
    }
    
    private void setupListeners() {
        payButton.setOnClickListener(v -> processPayment());
        cancelButton.setOnClickListener(v -> {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            finish();
        });
        
        findViewById(R.id.btnDone).setOnClickListener(v -> {
            // Update order status to paid
            updateOrderStatus();
            finish();
        });
    }
    
    private void processPayment() {
        String cardNumber = cardNumberInput.getText().toString().trim();
        String cardHolder = cardHolderInput.getText().toString().trim();
        String expiryDate = expiryDateInput.getText().toString().trim();
        String cvv = cvvInput.getText().toString().trim();
        
        // Validate inputs
        if (cardNumber.isEmpty() || cardHolder.isEmpty() || expiryDate.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill all card details", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (cardNumber.replace(" ", "").length() != 16) {
            Toast.makeText(this, "Card number must be 16 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (cvv.length() != 3) {
            Toast.makeText(this, "CVV must be 3 digits", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show processing
        paymentFormLayout.setVisibility(View.GONE);
        processingLayout.setVisibility(View.VISIBLE);
        
        // Simulate payment processing (2 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Show success
            processingLayout.setVisibility(View.GONE);
            successLayout.setVisibility(View.VISIBLE);
            
            // Send notification
            String userId = Prefs.getInstance(PaymentActivity.this).getUserId();
            NotificationHelper.sendNotification(
                PaymentActivity.this,
                userId,
                NotificationHelper.TYPE_CANTEEN_ORDER,
                "💳 Payment Successful",
                String.format("Payment of ₹%.2f completed for order %s", amount, orderId)
            );
        }, 2000);
    }
    
    private void updateOrderStatus() {
        // Update order payment status in Firebase
        FirebaseHelper.updateCanteenOrderStatus(orderId, "paid", new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(java.util.Map<String, Object> data) {
                Toast.makeText(PaymentActivity.this, "Payment completed successfully!", Toast.LENGTH_SHORT).show();
            }
            
            @Override
            public void onError(String error) {
                // Silent error - payment still went through
            }
        });
    }
}
