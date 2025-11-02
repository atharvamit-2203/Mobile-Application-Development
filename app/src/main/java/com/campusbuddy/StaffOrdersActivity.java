package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;
import java.text.SimpleDateFormat;

// Staff orders management - view and manage all orders
public class StaffOrdersActivity extends Activity {

    private Spinner statusFilterSpinner;
    private ListView ordersListView;
    private TextView totalOrdersText, pendingCountText, completedCountText;
    
    private List<Map<String, Object>> allOrders = new ArrayList<>();
    private List<Map<String, Object>> filteredOrders = new ArrayList<>();
    private OrdersAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_orders);
        
        statusFilterSpinner = findViewById(R.id.statusFilterSpinner);
        ordersListView = findViewById(R.id.ordersListView);
        totalOrdersText = findViewById(R.id.totalOrdersText);
        pendingCountText = findViewById(R.id.pendingCountText);
        completedCountText = findViewById(R.id.completedCountText);
        
        setupStatusFilter();
        loadOrders();
    }
    
    private void setupStatusFilter() {
        String[] statuses = {"All Orders", "Pending", "Verified", "Completed", "Rejected"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusFilterSpinner.setAdapter(adapter);
        
        statusFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filterOrders(position);
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    
    private void loadOrders() {
        FirebaseHelper.getAllOrders(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> orders) {
                allOrders = orders;
                filteredOrders = new ArrayList<>(orders);
                updateStats();
                displayOrders();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(StaffOrdersActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void filterOrders(int filterPosition) {
        filteredOrders.clear();
        
        if (filterPosition == 0) {
            filteredOrders.addAll(allOrders);
        } else {
            String status = getStatusFromPosition(filterPosition);
            for (Map<String, Object> order : allOrders) {
                String orderStatus = (String) order.get("status");
                if (orderStatus != null && orderStatus.equalsIgnoreCase(status)) {
                    filteredOrders.add(order);
                }
            }
        }
        
        displayOrders();
    }
    
    private String getStatusFromPosition(int position) {
        switch (position) {
            case 1: return "pending";
            case 2: return "verified";
            case 3: return "completed";
            case 4: return "rejected";
            default: return "";
        }
    }
    
    private void updateStats() {
        totalOrdersText.setText(String.valueOf(allOrders.size()));
        
        int pending = 0, completed = 0;
        for (Map<String, Object> order : allOrders) {
            String status = (String) order.get("status");
            if ("pending".equals(status)) pending++;
            if ("completed".equals(status)) completed++;
        }
        
        pendingCountText.setText(String.valueOf(pending));
        completedCountText.setText(String.valueOf(completed));
    }
    
    private void displayOrders() {
        adapter = new OrdersAdapter();
        ordersListView.setAdapter(adapter);
    }
    
    private class OrdersAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filteredOrders.size();
        }
        
        @Override
        public Object getItem(int position) {
            return filteredOrders.get(position);
        }
        
        @Override
        public long getItemId(int position) {
            return position;
        }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_staff_order, parent, false);
            }
            
            Map<String, Object> order = filteredOrders.get(position);
            
            TextView orderIdText = convertView.findViewById(R.id.orderIdText);
            TextView userNameText = convertView.findViewById(R.id.userNameText);
            TextView totalAmountText = convertView.findViewById(R.id.totalAmountText);
            TextView statusText = convertView.findViewById(R.id.statusText);
            TextView dateText = convertView.findViewById(R.id.dateText);
            TextView itemsText = convertView.findViewById(R.id.itemsText);
            Button markCompleteBtn = convertView.findViewById(R.id.btnMarkComplete);
            
            String orderId = (String) order.get("id");
            double totalAmount = order.get("total_amount") != null ? ((Number) order.get("total_amount")).doubleValue() : 0;
            String status = (String) order.get("status");
            long orderDate = order.get("order_date") != null ? ((Number) order.get("order_date")).longValue() : 0;
            
            orderIdText.setText("Order #" + orderId.substring(0, Math.min(8, orderId.length())));
            userNameText.setText("Customer: " + order.get("user_id"));
            totalAmountText.setText("₹" + String.format("%.2f", totalAmount));
            statusText.setText(status != null ? status.toUpperCase() : "UNKNOWN");
            
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
            dateText.setText(sdf.format(new Date(orderDate)));
            
            // Set status color
            switch (status != null ? status : "") {
                case "pending":
                    statusText.setTextColor(0xFFF59E0B);
                    break;
                case "verified":
                    statusText.setTextColor(0xFF3B82F6);
                    break;
                case "completed":
                    statusText.setTextColor(0xFF10B981);
                    break;
                case "rejected":
                    statusText.setTextColor(0xFFEF4444);
                    break;
            }
            
            // Show items
            List<Map<String, Object>> items = (List<Map<String, Object>>) order.get("items");
            if (items != null && !items.isEmpty()) {
                StringBuilder itemsList = new StringBuilder("Items: ");
                for (int i = 0; i < items.size(); i++) {
                    Map<String, Object> item = items.get(i);
                    itemsList.append(item.get("item_name"));
                    if (i < items.size() - 1) itemsList.append(", ");
                }
                itemsText.setText(itemsList.toString());
            }
            
            // Mark complete button
            if ("verified".equals(status)) {
                markCompleteBtn.setVisibility(View.VISIBLE);
                markCompleteBtn.setOnClickListener(v -> markOrderComplete(orderId, position));
            } else {
                markCompleteBtn.setVisibility(View.GONE);
            }
            
            return convertView;
        }
    }
    
    private void markOrderComplete(String orderId, int position) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "completed");
        updates.put("completed_at", System.currentTimeMillis());
        
        FirebaseHelper.updateOrder(orderId, updates, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(StaffOrdersActivity.this, "Order marked as completed", Toast.LENGTH_SHORT).show();
                loadOrders();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(StaffOrdersActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
