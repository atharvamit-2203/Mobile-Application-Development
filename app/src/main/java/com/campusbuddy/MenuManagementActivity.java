package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class MenuManagementActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        TextView title = new TextView(this);
        title.setText("Menu Management");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 40);
        layout.addView(title);
        
        TextView info = new TextView(this);
        info.setText("Manage canteen menu:\n\n• Add new items\n• Update prices\n• Mark items unavailable\n• Create special offers");
        info.setTextSize(16);
        info.setPadding(0, 0, 0, 40);
        layout.addView(info);
        
        Button back = new Button(this);
        back.setText("Back");
        back.setOnClickListener(v -> finish());
        layout.addView(back);
        
        setContentView(layout);
        Toast.makeText(this, "Feature coming soon!", Toast.LENGTH_SHORT).show();
    }
}
