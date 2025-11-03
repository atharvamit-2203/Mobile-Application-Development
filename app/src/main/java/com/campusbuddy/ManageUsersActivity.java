package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends Activity {
    private Spinner roleFilter;
    private RecyclerView recyclerView;
    private UsersAdapter adapter;
    private List<UserItem> users = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        db = FirebaseFirestore.getInstance();
        
        roleFilter = findViewById(R.id.roleFilter);
        recyclerView = findViewById(R.id.recyclerView);
        Button btnBack = findViewById(R.id.btnBack);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(users, this);
        recyclerView.setAdapter(adapter);

        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"All Users", "Students", "Faculty", "Staff", "Organizations", "Admin"});
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleFilter.setAdapter(filterAdapter);

        roleFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                loadUsers(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnBack.setOnClickListener(v -> finish());

        loadUsers(0);
    }

    private void loadUsers(int filterPosition) {
        String role = null;
        switch (filterPosition) {
            case 1: role = "student"; break;
            case 2: role = "faculty"; break;
            case 3: role = "staff"; break;
            case 4: role = "organization"; break;
            case 5: role = "admin"; break;
        }

        users.clear();

        if (role == null) {
            loadFromCollection("users", null);
            loadFromCollection("faculty", null);
            loadFromCollection("staff", null);
            loadFromCollection("organizations", null);
        } else {
            String collection = role.equals("student") ? "users" : 
                              role.equals("faculty") ? "faculty" :
                              role.equals("staff") ? "staff" :
                              role.equals("organization") ? "organizations" : "users";
            loadFromCollection(collection, role);
        }
    }

    private void loadFromCollection(String collection, String role) {
        db.collection(collection).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    String name = document.getString("name");
                    String userRole = document.getString("role");
                    if (userRole == null) userRole = collection.equals("users") ? "student" : collection.replaceAll("s$", "");
                    
                    users.add(new UserItem(document.getId(), name, email, userRole));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "Error loading users: " + task.getException().getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class UserItem {
        String id, name, email, role;

        public UserItem(String id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }
    }

    public static class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
        private List<UserItem> users;
        private Activity context;

        public UsersAdapter(List<UserItem> users, Activity context) {
            this.users = users;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            android.view.View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserItem user = users.get(position);
            holder.text1.setText(user.name != null ? user.name : "N/A");
            holder.text2.setText(user.email + " (" + user.role + ")");
            
            holder.itemView.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(context)
                    .setTitle("User Details")
                    .setMessage("Name: " + user.name + "\nEmail: " + user.email + "\nRole: " + user.role + "\nID: " + user.id)
                    .setPositiveButton("OK", null)
                    .setNeutralButton("Delete", (dialog, which) -> {
                        new android.app.AlertDialog.Builder(context)
                            .setTitle("Delete User")
                            .setMessage("Are you sure you want to delete this user?")
                            .setPositiveButton("Delete", (d, w) -> {
                                deleteUser(user, position);
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                    })
                    .show();
            });
        }

        private void deleteUser(UserItem user, int position) {
            String collection = user.role.equals("student") ? "users" :
                              user.role.equals("faculty") ? "faculty" :
                              user.role.equals("staff") ? "staff" :
                              user.role.equals("organization") ? "organizations" : "users";
            
            FirebaseFirestore.getInstance().collection(collection).document(user.id)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    users.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error deleting user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            public ViewHolder(android.view.View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
