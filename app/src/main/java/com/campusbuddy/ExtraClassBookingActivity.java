package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class ExtraClassBookingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        TextView title = new TextView(this);
        title.setText("Book Extra Classes");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 40);
        layout.addView(title);
        
        TextView info = new TextView(this);
        info.setText("Schedule extra classes:\n\n• Book rooms for extra classes\n• Set date and time\n• Notify students\n• Manage schedules");
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
