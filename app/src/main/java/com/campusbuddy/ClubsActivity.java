package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

@SuppressWarnings("deprecation")
public class ClubsActivity extends Activity {
    private TabHost tabHost;
    private ListView campusClubsListView;
    private ListView myClubsListView;
    private List<Map<String, Object>> allClubs = new ArrayList<>();
    private List<Map<String, Object>> myClubs = new ArrayList<>();
    private CampusClubsAdapter campusAdapter;
    private MyClubsAdapter myAdapter;
    private Set<String> userMembershipClubIds = new HashSet<>();
    private Map<String, String> membershipStatuses = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clubs);

        tabHost = findViewById(R.id.clubsTabHost);
        tabHost.setup();

        TabHost.TabSpec spec1 = tabHost.newTabSpec("Campus Clubs");
        spec1.setContent(R.id.campusClubsTab);
        spec1.setIndicator("Campus Clubs");
        tabHost.addTab(spec1);

        TabHost.TabSpec spec2 = tabHost.newTabSpec("My Clubs");
        spec2.setContent(R.id.myClubsTab);
        spec2.setIndicator("My Clubs");
        tabHost.addTab(spec2);

        campusClubsListView = findViewById(R.id.campusClubsListView);
        myClubsListView = findViewById(R.id.myClubsListView);

        campusAdapter = new CampusClubsAdapter();
        myAdapter = new MyClubsAdapter();
        
        campusClubsListView.setAdapter(campusAdapter);
        myClubsListView.setAdapter(myAdapter);

        loadUserMemberships();
    }

    private void loadUserMemberships() {
        String userId = Prefs.getInstance(this).getUserId();
        if (userId == null || userId.isEmpty()) {
            loadAllClubs();
            return;
        }

        FirebaseHelper.getUserMemberships(userId, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> memberships) {
                userMembershipClubIds.clear();
                membershipStatuses.clear();
                
                for (Map<String, Object> membership : memberships) {
                    String clubId = (String) membership.get("club_id");
                    String status = (String) membership.get("status");
                    if (clubId != null) {
                        userMembershipClubIds.add(clubId);
                        if (status != null) {
                            membershipStatuses.put(clubId, status);
                        }
                    }
                }
                
                loadAllClubs();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ClubsActivity.this, "Error loading memberships: " + error, Toast.LENGTH_SHORT).show();
                loadAllClubs();
            }
        });
    }

    private void loadAllClubs() {
        FirebaseHelper.getClubs(new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> clubs) {
                allClubs.clear();
                myClubs.clear();
                
                for (Map<String, Object> club : clubs) {
                    allClubs.add(club);
                    
                    // Use "id" which is the document ID added by FirebaseHelper.getClubs()
                    String clubId = (String) club.get("id");
                    if (clubId == null) {
                        clubId = (String) club.get("club_id"); // Fallback
                    }
                    String status = membershipStatuses.get(clubId);
                    if ("approved".equals(status)) {
                        myClubs.add(club);
                    }
                }
                
                campusAdapter.notifyDataSetChanged();
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ClubsActivity.this, "Error loading clubs: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class CampusClubsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allClubs.size();
        }

        @Override
        public Object getItem(int position) {
            return allClubs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_club, parent, false);
            }

            Map<String, Object> club = allClubs.get(position);
            // Use "id" which is the document ID added by FirebaseHelper.getClubs()
            String tempClubId = (String) club.get("id");
            if (tempClubId == null) {
                tempClubId = (String) club.get("club_id"); // Fallback
            }
            final String clubId = tempClubId; // Make final for lambda

            TextView nameText = convertView.findViewById(R.id.clubNameText);
            TextView descText = convertView.findViewById(R.id.clubDescText);
            TextView categoryText = convertView.findViewById(R.id.clubCategoryText);
            Button joinButton = convertView.findViewById(R.id.joinClubButton);

            // Try both "name" and "club_name" fields
            String clubName = (String) club.get("name");
            if (clubName == null || clubName.isEmpty()) {
                clubName = (String) club.get("club_name");
            }
            nameText.setText(clubName != null ? clubName : "Unnamed Club");
            descText.setText((String) club.get("description"));
            categoryText.setText((String) club.get("category"));

            String status = membershipStatuses.get(clubId);
            if ("approved".equals(status)) {
                joinButton.setText(" Member");
                joinButton.setEnabled(false);
            } else if ("pending".equals(status)) {
                joinButton.setText("Pending");
                joinButton.setEnabled(false);
            } else {
                joinButton.setText("Join Club");
                joinButton.setEnabled(true);
                joinButton.setOnClickListener(v -> joinClub(clubId));
            }

            return convertView;
        }
    }

    private class MyClubsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return myClubs.size();
        }

        @Override
        public Object getItem(int position) {
            return myClubs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_my_club, parent, false);
            }

            Map<String, Object> club = myClubs.get(position);

            TextView nameText = convertView.findViewById(R.id.myClubNameText);
            TextView descText = convertView.findViewById(R.id.myClubDescText);
            TextView categoryText = convertView.findViewById(R.id.myClubCategoryText);
            TextView roleText = convertView.findViewById(R.id.myClubRoleText);

            // Try both "name" and "club_name" fields
            String clubName = (String) club.get("name");
            if (clubName == null || clubName.isEmpty()) {
                clubName = (String) club.get("club_name");
            }
            nameText.setText(clubName != null ? clubName : "Unnamed Club");
            descText.setText((String) club.get("description"));
            categoryText.setText("Category: " + club.get("category"));
            roleText.setText("Role: Member");

            return convertView;
        }
    }

    private void joinClub(String clubId) {
        String userId = Prefs.getInstance(this).getUserId();
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("club_id", clubId);
        request.put("status", "pending");
        request.put("request_date", new Date().toString());

        FirebaseHelper.addMemberRequest(request, new FirebaseHelper.DataCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> data) {
                Toast.makeText(ClubsActivity.this, "Join request sent", Toast.LENGTH_SHORT).show();
                loadUserMemberships();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ClubsActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}