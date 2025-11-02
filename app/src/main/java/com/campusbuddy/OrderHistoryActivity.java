package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import java.util.*;
import java.text.SimpleDateFormat;

// View user's canteen order history
public class OrderHistoryActivity extends Activity {

    private ListView ordersListView;
    private TextView tvNoOrders;
    private List<Map<String, Object>> orders = new ArrayList<>();
    private OrdersAdapter adapter;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        
        ordersListView = findViewById(R.id.ordersListView);
        tvNoOrders = findViewById(R.id.tvNoOrders);
        
        adapter = new OrdersAdapter();
        ordersListView.setAdapter(adapter);
        
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            Map<String, Object> order = orders.get(position);
            showOrderQR(order);
        });
        
        loadOrders();
    }
    
    private void loadOrders() {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.getCanteenOrders(userId, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                orders = data;
                if (orders.isEmpty()) {
                    tvNoOrders.setVisibility(View.VISIBLE);
                    ordersListView.setVisibility(View.GONE);
                } else {
                    tvNoOrders.setVisibility(View.GONE);
                    ordersListView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(OrderHistoryActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void showOrderQR(Map<String, Object> order) {
        Intent intent = new Intent(this, OrderQRActivity.class);
        intent.putExtra("orderId", (String) order.get("id"));
        intent.putExtra("totalAmount", ((Number) order.get("total_amount")).doubleValue());
        intent.putExtra("status", (String) order.get("status"));
        intent.putExtra("orderDate", ((Number) order.get("order_date")).longValue());
        startActivity(intent);
    }
    
    // Orders Adapter
    class OrdersAdapter extends BaseAdapter {
        @Override
        public int getCount() { return orders.size(); }
        
        @Override
        public Object getItem(int position) { return orders.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_order_history, parent, false);
            }
            
            Map<String, Object> order = orders.get(position);
            
            TextView tvOrderId = convertView.findViewById(R.id.tvOrderId);
            TextView tvOrderDate = convertView.findViewById(R.id.tvOrderDate);
            TextView tvOrderAmount = convertView.findViewById(R.id.tvOrderAmount);
            TextView tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);
            TextView tvItemCount = convertView.findViewById(R.id.tvItemCount);
            
            String orderId = (String) order.get("id");
            long orderDate = ((Number) order.get("order_date")).longValue();
            double amount = ((Number) order.get("total_amount")).doubleValue();
            String status = (String) order.get("status");
            
            // Count items
            int itemCount = 0;
            if (order.get("items") instanceof List) {
                itemCount = ((List<?>) order.get("items")).size();
            }
            
            tvOrderId.setText("#" + orderId.substring(0, Math.min(8, orderId.length())));
            tvOrderDate.setText(dateFormat.format(new Date(orderDate)));
            tvOrderAmount.setText("₹" + String.format("%.2f", amount));
            tvItemCount.setText(itemCount + " items");
            
            // Status styling
            tvOrderStatus.setText(status.toUpperCase());
            switch (status.toLowerCase()) {
                case "pending":
                    tvOrderStatus.setTextColor(0xFFF59E0B);
                    break;
                case "verified":
                    tvOrderStatus.setTextColor(0xFF3B82F6);
                    break;
                case "completed":
                    tvOrderStatus.setTextColor(0xFF10B981);
                    break;
                case "rejected":
                    tvOrderStatus.setTextColor(0xFFEF4444);
                    break;
            }
            
            return convertView;
        }
    }
}
