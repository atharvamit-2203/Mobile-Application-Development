package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

// Browse and join clubs/organizations
public class ClubsActivity extends Activity {

    private ListView clubsListView;
    private List<Map<String, Object>> clubs = new ArrayList<>();
    private ClubsAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);
        
        clubsListView = findViewById(R.id.clubsListView);
        adapter = new ClubsAdapter();
        clubsListView.setAdapter(adapter);
        
        loadClubs();
    }
    
    private void loadClubs() {
        FirebaseHelper.getClubs(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                clubs = data;
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(ClubsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void joinClub(String clubId, int position) {
        String userId = Prefs.getInstance(this).getUserId();
        
        FirebaseHelper.joinClub(clubId, userId, new FirebaseHelper.SingleDataCallback() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                Toast.makeText(ClubsActivity.this, "Join request sent!", Toast.LENGTH_SHORT).show();
                Map<String, Object> club = clubs.get(position);
                club.put("membership_status", "pending");
                adapter.notifyDataSetChanged();
            }
            
            @Override
            public void onError(String error) {
                Toast.makeText(ClubsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    class ClubsAdapter extends BaseAdapter {
        @Override
        public int getCount() { return clubs.size(); }
        
        @Override
        public Object getItem(int position) { return clubs.get(position); }
        
        @Override
        public long getItemId(int position) { return position; }
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_club, parent, false);
            }
            
            Map<String, Object> club = clubs.get(position);
            
            TextView nameText = convertView.findViewById(R.id.clubNameText);
            TextView descText = convertView.findViewById(R.id.clubDescText);
            TextView categoryText = convertView.findViewById(R.id.clubCategoryText);
            TextView membersText = convertView.findViewById(R.id.clubMembersText);
            Button joinButton = convertView.findViewById(R.id.joinClubButton);
            
            nameText.setText((String) club.get("name"));
            descText.setText((String) club.getOrDefault("description", ""));
            categoryText.setText((String) club.getOrDefault("category", "General"));
            
            Object memberCount = club.get("member_count");
            Object maxMembers = club.get("max_members");
            String membersText_str = memberCount != null ? memberCount.toString() : "0";
            if (maxMembers != null) {
                membersText_str += " / " + maxMembers;
            }
            membersText.setText(membersText_str + " members");
            
            String status = (String) club.get("membership_status");
            if ("pending".equals(status)) {
                joinButton.setText("Pending");
                joinButton.setEnabled(false);
            } else if ("approved".equals(status)) {
                joinButton.setText("Joined");
                joinButton.setEnabled(false);
            } else {
                joinButton.setText("Join Club");
                joinButton.setEnabled(true);
                joinButton.setOnClickListener(v -> joinClub((String) club.get("id"), position));
            }
            
            return convertView;
        }
    }
}
