package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class EditProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF0F172A);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
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
        title.setText("✏️ Edit Profile");
        title.setTextSize(28);
        title.setTextColor(0xFFE2E8F0);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("Update your personal information");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        Prefs prefs = Prefs.getInstance(this);
        
        // Name Label
        TextView nameLabel = new TextView(this);
        nameLabel.setText("Name");
        nameLabel.setTextSize(14);
        nameLabel.setTextColor(0xFFE2E8F0);
        nameLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams nameLabelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameLabelParams.topMargin = 10;
        nameLabelParams.bottomMargin = 8;
        nameLabel.setLayoutParams(nameLabelParams);
        layout.addView(nameLabel);
        
        EditText name = new EditText(this);
        name.setHint("Enter your name");
        name.setText(prefs.getName());
        name.setPadding(30, 30, 30, 30);
        name.setBackgroundColor(0xFF1E293B);
        name.setTextColor(0xFFE2E8F0);
        name.setHintTextColor(0xFF64748B);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameParams.bottomMargin = 20;
        name.setLayoutParams(nameParams);
        layout.addView(name);
        
        // Email Label
        TextView emailLabel = new TextView(this);
        emailLabel.setText("Email (Cannot be changed)");
        emailLabel.setTextSize(14);
        emailLabel.setTextColor(0xFFE2E8F0);
        emailLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams emailLabelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emailLabelParams.bottomMargin = 8;
        emailLabel.setLayoutParams(emailLabelParams);
        layout.addView(emailLabel);
        
        EditText email = new EditText(this);
        email.setHint("Email");
        email.setText(prefs.getEmail());
        email.setPadding(30, 30, 30, 30);
        email.setEnabled(false);
        email.setBackgroundColor(0xFF334155);
        email.setTextColor(0xFF94A3B8);
        email.setHintTextColor(0xFF64748B);
        LinearLayout.LayoutParams emailParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        emailParams.bottomMargin = 20;
        email.setLayoutParams(emailParams);
        layout.addView(email);
        
        // Phone Label
        TextView phoneLabel = new TextView(this);
        phoneLabel.setText("Phone Number");
        phoneLabel.setTextSize(14);
        phoneLabel.setTextColor(0xFFE2E8F0);
        phoneLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams phoneLabelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        phoneLabelParams.bottomMargin = 8;
        phoneLabel.setLayoutParams(phoneLabelParams);
        layout.addView(phoneLabel);
        
        EditText phone = new EditText(this);
        phone.setHint("Enter phone number");
        phone.setPadding(30, 30, 30, 30);
        phone.setBackgroundColor(0xFF1E293B);
        phone.setTextColor(0xFFE2E8F0);
        phone.setHintTextColor(0xFF64748B);
        LinearLayout.LayoutParams phoneParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        phoneParams.bottomMargin = 30;
        phone.setLayoutParams(phoneParams);
        layout.addView(phone);
        
        Button save = new Button(this);
        save.setText("✓ Save Changes");
        save.setPadding(30, 30, 30, 30);
        save.setBackgroundColor(0xFF10B981);
        save.setTextColor(0xFFFFFFFF);
        save.setTextSize(16);
        save.setTypeface(null, android.graphics.Typeface.BOLD);
        save.setOnClickListener(v -> {
            String newName = name.getText().toString().trim();
            if (!newName.isEmpty()) {
                prefs.setUserData(prefs.getEmail(), newName);
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        LinearLayout.LayoutParams saveParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        saveParams.bottomMargin = 15;
        save.setLayoutParams(saveParams);
        layout.addView(save);
        
        Button back = new Button(this);
        back.setText("← Back");
        back.setPadding(30, 30, 30, 30);
        back.setBackgroundColor(0xFF475569);
        back.setTextColor(0xFFFFFFFF);
        back.setTextSize(16);
        back.setOnClickListener(v -> finish());
        layout.addView(back);
        
        scroll.addView(layout);
        setContentView(scroll);
    }
}
