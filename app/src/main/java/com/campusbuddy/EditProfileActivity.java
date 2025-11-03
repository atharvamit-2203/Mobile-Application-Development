package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;

public class EditProfileActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrollView scroll = new ScrollView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        TextView title = new TextView(this);
        title.setText("Edit Profile");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 40);
        layout.addView(title);
        
        Prefs prefs = Prefs.getInstance(this);
        
        EditText name = new EditText(this);
        name.setHint("Name");
        name.setText(prefs.getName());
        name.setPadding(30, 30, 30, 30);
        layout.addView(name);
        
        EditText email = new EditText(this);
        email.setHint("Email");
        email.setText(prefs.getEmail());
        email.setPadding(30, 30, 30, 30);
        email.setEnabled(false);
        layout.addView(email);
        
        EditText phone = new EditText(this);
        phone.setHint("Phone Number");
        phone.setPadding(30, 30, 30, 30);
        layout.addView(phone);
        
        Button save = new Button(this);
        save.setText("Save Changes");
        save.setPadding(30, 30, 30, 30);
        save.setOnClickListener(v -> {
            String newName = name.getText().toString().trim();
            if (!newName.isEmpty()) {
                prefs.setUserData(prefs.getEmail(), newName);
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        layout.addView(save);
        
        Button back = new Button(this);
        back.setText("Back");
        back.setPadding(30, 30, 30, 30);
        back.setOnClickListener(v -> finish());
        layout.addView(back);
        
        scroll.addView(layout);
        setContentView(scroll);
    }
}
