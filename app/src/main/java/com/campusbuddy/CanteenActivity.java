package com.campusbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

// Canteen ordering system with menu, cart, and checkout
public class CanteenActivity extends Activity {

    private ListView menuListView;
    private TextView cartCountText, cartTotalText;
    private Button viewCartButton;
    private ScrollView cartLayout;
    private LinearLayout checkoutLayout;
    private ListView cartListView;
    private RadioGroup paymentMethodGroup;
    private Button placeOrderButton, continueShoppingButton;
    private EditText searchEditText;
    private Spinner categorySpinner;
    
    private List<Map<String, Object>> menuItems = new ArrayList<>();
    private List<Map<String, Object>> filteredMenuItems = new ArrayList<>();
    private List<Map<String, Object>> cartItems = new ArrayList<>();
    private MenuAdapter menuAdapter;
    private CartAdapter cartAdapter;
    private String selectedCategory = "All";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canteen);
        
        initViews();
        loadMenu();
    }
    
    private void initViews() {
        menuListView = findViewById(R.id.menuListView);
        cartCountText = findViewById(R.id.cartCountText);
        cartTotalText = findViewById(R.id.cartTotalText);
        viewCartButton = findViewById(R.id.viewCartButton);
        cartLayout = findViewById(R.id.cartLayout);
        checkoutLayout = findViewById(R.id.checkoutLayout);
        cartListView = findViewById(R.id.cartListView);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        placeOrderButton = findViewById(R.id.placeOrderButton);
        continueShoppingButton = findViewById(R.id.continueShoppingButton);
        searchEditText = findViewById(R.id.searchEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        
        menuAdapter = new MenuAdapter();
        menuListView.setAdapter(menuAdapter);
        
        cartAdapter = new CartAdapter();
        cartListView.setAdapter(cartAdapter);
        
        viewCartButton.setOnClickListener(v -> showCart());
        continueShoppingButton.setOnClickListener(v -> showMenu());
        placeOrderButton.setOnClickListener(v -> placeOrder());
        
        // Setup search
        searchEditText.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMenu();
            }
            public void afterTextChanged(android.text.Editable s) {}
        });
        
        updateCartUI();
    }
    
    private void loadMenu() {
        FirebaseHelper.getCanteenMenu(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                menuItems = data;
                filteredMenuItems = new ArrayList<>(data);
                setupCategorySpinner();
                menuAdapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(CanteenActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupCategorySpinner() {
        // Extract unique categories
        java.util.Set<String> categories = new java.util.HashSet<>();
        categories.add("All");
        for (Map<String, Object> item : menuItems) {
            String category = (String) item.getOrDefault("category", "Other");
            categories.add(category);
        }
        
        List<String> categoryList = new ArrayList<>(categories);
        java.util.Collections.sort(categoryList);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        
        categorySpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categoryList.get(position);
                filterMenu();
            }
            
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });
    }
    
    private void filterMenu() {
        String searchText = searchEditText.getText().toString().toLowerCase();
        filteredMenuItems.clear();
        
        for (Map<String, Object> item : menuItems) {
            String name = ((String) item.get("name")).toLowerCase();
            String category = (String) item.getOrDefault("category", "Other");
            String description = ((String) item.getOrDefault("description", "")).toLowerCase();
            
            boolean matchesSearch = name.contains(searchText) || description.contains(searchText);
            boolean matchesCategory = selectedCategory.equals("All") || category.equals(selectedCategory);
            
            if (matchesSearch && matchesCategory) {
                filteredMenuItems.add(item);
            }
        }
        
        menuAdapter.notifyDataSetChanged();
    }
    
    private void addToCart(Map<String, Object> item) {
        // Check if item already in cart
        for (Map<String, Object> cartItem : cartItems) {
            if (cartItem.get("id").equals(item.get("id"))) {
                int quantity = ((Number) cartItem.get("quantity")).intValue() + 1;
                cartItem.put("quantity", quantity);
                updateCartUI();
                return;
            }
        }
        
        // Add new item to cart
        Map<String, Object> cartItem = new HashMap<>(item);
        cartItem.put("quantity", 1);
        cartItems.add(cartItem);
        updateCartUI();
        Toast.makeText(this, item.get("name") + " added to cart", Toast.LENGTH_SHORT).show();
    }
    
    private void removeFromCart(int position) {
        cartItems.remove(position);
        cartAdapter.notifyDataSetChanged();
        updateCartUI();
    }
    
    private void updateQuantity(int position, int change) {
        Map<String, Object> item = cartItems.get(position);
        int quantity = ((Number) item.get("quantity")).intValue() + change;
        
        if (quantity <= 0) {
            removeFromCart(position);
        } else {
            item.put("quantity", quantity);
            cartAdapter.notifyDataSetChanged();
            updateCartUI();
        }
    }
    
    private void updateCartUI() {
        int count = 0;
        final double[] totalArray = {0.0};
        
        for (Map<String, Object> item : cartItems) {
            int quantity = ((Number) item.get("quantity")).intValue();
            double price = ((Number) item.get("price")).doubleValue();
            count += quantity;
            totalArray[0] += price * quantity;
        }
        
        cartCountText.setText(count + " items");
        cartTotalText.setText("₹" + String.format("%.2f", totalArray[0]));
        viewCartButton.setEnabled(count > 0);
    }
    
    private void showCart() {
        menuListView.setVisibility(View.GONE);
        cartLayout.setVisibility(View.VISIBLE);
        checkoutLayout.setVisibility(View.VISIBLE);
        cartAdapter.notifyDataSetChanged();
    }
    
    private void showMenu() {
        cartLayout.setVisibility(View.GONE);
        checkoutLayout.setVisibility(View.GONE);
        menuListView.setVisibility(View.VISIBLE);
    }
    
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int selectedPaymentId = paymentMethodGroup.getCheckedRadioButtonId();
        if (selectedPaymentId == -1) {
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String paymentMethod = selectedPaymentId == R.id.payNowRadio ? "pay_now" : "pay_later";
        
        // Calculate total
        final double[] totalArray = {0.0};
        List<Map<String, Object>> orderItems = new ArrayList<>();
        
        for (Map<String, Object> item : cartItems) {
            int quantity = ((Number) item.get("quantity")).intValue();
            double price = ((Number) item.get("price")).doubleValue();
            totalArray[0] += price * quantity;
            
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("item_id", item.get("id"));
            orderItem.put("item_name", item.get("name"));
            orderItem.put("price", price);
            orderItem.put("quantity", quantity);
            orderItems.add(orderItem);
        }
        
        // Create order
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("user_id", Prefs.getInstance(this).getUserId());
        orderData.put("items", orderItems);
        orderData.put("total_amount", totalArray[0]);
        orderData.put("payment_method", paymentMethod);
        orderData.put("payment_status", paymentMethod.equals("pay_now") ? "paid" : "pending");
        orderData.put("status", "pending");
        orderData.put("order_date", System.currentTimeMillis());
        
        placeOrderButton.setEnabled(false);
        placeOrderButton.setText("Placing Order...");
        
        FirebaseHelper.placeCanteenOrder(orderData, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                String orderId = (String) data.get("id");
                Toast.makeText(CanteenActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                
                // Send notification
                String userId = Prefs.getInstance(CanteenActivity.this).getUserId();
                NotificationHelper.notifyCanteenOrder(
                    CanteenActivity.this,
                    userId,
                    orderId,
                    totalArray[0]
                );
                
                // If pay now is selected, open PayU payment gateway
                if (paymentMethod.equals("pay_now")) {
                    // Launch PaymentActivity
                    Intent paymentIntent = new Intent(CanteenActivity.this, PaymentActivity.class);
                    paymentIntent.putExtra("orderId", orderId);
                    paymentIntent.putExtra("amount", totalArray[0]);
                    startActivity(paymentIntent);
                } else {
                    // For pay later, show QR code
                    Intent intent = new Intent(CanteenActivity.this, PaymentQRActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("totalAmount", totalArray[0]);
                    startActivity(intent);
                }
                
                cartItems.clear();
                updateCartUI();
                showMenu();
                placeOrderButton.setEnabled(true);
                placeOrderButton.setText("Place Order");
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(CanteenActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                placeOrderButton.setEnabled(true);
                placeOrderButton.setText("Place Order");
            }
        });
    }
    
    // Menu Adapter
    class MenuAdapter extends BaseAdapter {
        @Override
        public int getCount() { return filteredMenuItems.size(); }
        
        @Override
        public Object getItem(int position) { return filteredMenuItems.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_menu, parent, false);
            }
            
            Map<String, Object> item = filteredMenuItems.get(position);
            
            TextView nameText = convertView.findViewById(R.id.itemNameText);
            TextView priceText = convertView.findViewById(R.id.itemPriceText);
            TextView descText = convertView.findViewById(R.id.itemDescText);
            TextView categoryText = convertView.findViewById(R.id.itemCategoryText);
            Button addButton = convertView.findViewById(R.id.addToCartButton);
            
            String name = (String) item.get("name");
            boolean isVeg = (Boolean) item.getOrDefault("veg", true);
            String vegSymbol = isVeg ? "🥬" : "🍗";
            
            nameText.setText(vegSymbol + " " + name);
            priceText.setText("₹" + item.get("price"));
            descText.setText((String) item.getOrDefault("description", ""));
            categoryText.setText((String) item.getOrDefault("category", ""));
            
            addButton.setOnClickListener(v -> addToCart(item));
            
            return convertView;
        }
    }
    
    // Cart Adapter
    class CartAdapter extends BaseAdapter {
        @Override
        public int getCount() { return cartItems.size(); }
        
        @Override
        public Object getItem(int position) { return cartItems.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_cart, parent, false);
            }
            
            Map<String, Object> item = cartItems.get(position);
            int quantity = ((Number) item.get("quantity")).intValue();
            double price = ((Number) item.get("price")).doubleValue();
            
            TextView nameText = convertView.findViewById(R.id.cartItemName);
            TextView priceText = convertView.findViewById(R.id.cartItemPrice);
            TextView quantityText = convertView.findViewById(R.id.cartItemQuantity);
            Button minusButton = convertView.findViewById(R.id.minusButton);
            Button plusButton = convertView.findViewById(R.id.plusButton);
            Button removeButton = convertView.findViewById(R.id.removeButton);
            
            nameText.setText((String) item.get("name"));
            priceText.setText("₹" + String.format("%.2f", price * quantity));
            quantityText.setText(String.valueOf(quantity));
            
            minusButton.setOnClickListener(v -> updateQuantity(position, -1));
            plusButton.setOnClickListener(v -> updateQuantity(position, 1));
            removeButton.setOnClickListener(v -> removeFromCart(position));
            
            return convertView;
        }
    }
}




