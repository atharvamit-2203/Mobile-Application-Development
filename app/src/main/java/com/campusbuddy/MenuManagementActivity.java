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
        layout.setBackgroundColor(0xFF0F172A);
        layout.setPadding(30, 30, 30, 30);
        
        // Header Card
        androidx.cardview.widget.CardView headerCard = new androidx.cardview.widget.CardView(this);
        headerCard.setCardBackgroundColor(0xFF1E293B);
        headerCard.setRadius(20);
        headerCard.setCardElevation(8);
        LinearLayout.LayoutParams headerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        headerParams.bottomMargin = 25;
        headerCard.setLayoutParams(headerParams);
        
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayout.setPadding(30, 30, 30, 30);
        
        TextView title = new TextView(this);
        title.setText("🍽️ Menu Management");
        title.setTextSize(28);
        title.setTextColor(0xFFE2E8F0);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Manage canteen menu items");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        // Info Card
        androidx.cardview.widget.CardView infoCard = new androidx.cardview.widget.CardView(this);
        infoCard.setCardBackgroundColor(0xFF1E293B);
        infoCard.setRadius(15);
        infoCard.setCardElevation(6);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        infoParams.bottomMargin = 25;
        infoCard.setLayoutParams(infoParams);
        
        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setPadding(25, 25, 25, 25);
        
        TextView info = new TextView(this);
        info.setText("Manage canteen menu:\n\n➕ Add new items\n💰 Update prices\n❌ Mark items unavailable\n🎁 Create special offers");
        info.setTextSize(16);
        info.setTextColor(0xFFE2E8F0);
        info.setLineSpacing(0, 1.5f);
        infoLayout.addView(info);
        
        infoCard.addView(infoLayout);
        layout.addView(infoCard);
        
        Button back = new Button(this);
        back.setText("← Back");
        back.setBackgroundColor(0xFF475569);
        back.setTextColor(0xFFFFFFFF);
        back.setTextSize(16);
        back.setPadding(30, 30, 30, 30);
        back.setOnClickListener(v -> finish());
        layout.addView(back);
        
        setContentView(layout);
    }
}
