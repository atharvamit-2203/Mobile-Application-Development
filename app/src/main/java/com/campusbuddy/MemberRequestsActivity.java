package com.campusbuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MemberRequestsActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private TextView emptyView;
    private RequestsAdapter adapter;
    private FirebaseFirestore db;
    private String organizationId;
    private List<MemberRequest> requests;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_member_requests);
            
            db = FirebaseFirestore.getInstance();
            requests = new ArrayList<>();
            
            // Initialize views
            recyclerView = findViewById(R.id.recyclerView);
            emptyView = findViewById(R.id.emptyView);
            
            if (recyclerView == null || emptyView == null) {
                Toast.makeText(this, "Error: Views not found in layout", Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Setup RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RequestsAdapter();
            recyclerView.setAdapter(adapter);
            
            // Back button
            View btnBack = findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> finish());
            }
            
            // Get organization ID from Intent extras
            organizationId = getIntent().getStringExtra("organizationId");
            android.util.Log.d("MemberRequests", "Intent organizationId: " + organizationId);
            
            // Fallback: if not passed via Intent, use user's ID (club doc ID = user ID)
            if (organizationId == null) {
                organizationId = Prefs.getInstance(this).getUserId();
                android.util.Log.d("MemberRequests", "Using user ID as organizationId: " + organizationId);
            }
            
            // Load requests
            if (organizationId != null) {
                loadMemberRequests();
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
                finish();
            }
            
        } catch (Exception e) {
            android.util.Log.e("MemberRequests", "Error in onCreate", e);
            Toast.makeText(this, "Error loading page: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void loadMemberRequests() {
        try {
            // Debug logging
            String debugMsg = "Loading requests for club_id: " + organizationId;
            Toast.makeText(this, debugMsg, Toast.LENGTH_LONG).show();
            android.util.Log.d("MemberRequests", debugMsg);
            
            // First, let's query ALL member_requests to see what's there
            db.collection("member_requests")
                .get()
                .addOnSuccessListener(allDocs -> {
                    android.util.Log.d("MemberRequests", "Total member_requests in DB: " + allDocs.size());
                    for (com.google.firebase.firestore.DocumentSnapshot doc : allDocs.getDocuments()) {
                        String clubId = doc.getString("club_id");
                        String status = doc.getString("status");
                        android.util.Log.d("MemberRequests", "Found request - club_id: " + clubId + ", status: " + status + ", matches: " + (clubId != null && clubId.equals(organizationId)));
                    }
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("MemberRequests", "Error querying all requests", e);
                });
            
            // Query for pending join requests for this organization
            // Note: ClubsActivity stores it as "club_id", not "organizationId"
            db.collection("member_requests")
                .whereEqualTo("club_id", organizationId)
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                android.util.Log.d("MemberRequests", "Query with filters returned " + queryDocumentSnapshots.size() + " documents");
                requests.clear();
                
                if (queryDocumentSnapshots.isEmpty()) {
                    // No requests found
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "No pending requests found. Check logs for debug info.", Toast.LENGTH_LONG).show();
                    return;
                }
                
                // Process each request and fetch user details
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    String userId = doc.getString("user_id");
                    String requestId = doc.getId();
                    
                    // Fetch user details from users collection
                    db.collection("users").document(userId)
                        .get()
                        .addOnSuccessListener(userDoc -> {
                            if (userDoc.exists()) {
                                MemberRequest request = new MemberRequest();
                                request.id = requestId;
                                request.studentId = userId;
                                request.studentName = userDoc.getString("name");
                                request.studentEmail = userDoc.getString("email");
                                request.course = userDoc.getString("course");
                                request.semester = userDoc.getString("semester");
                                request.message = doc.getString("message") != null ? doc.getString("message") : "";
                                request.timestamp = doc.contains("timestamp") ? doc.getLong("timestamp") : System.currentTimeMillis();
                                
                                requests.add(request);
                                
                                // Update UI when all requests are loaded
                                if (requests.size() == queryDocumentSnapshots.size()) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    emptyView.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(this, "Found " + requests.size() + " requests", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                }
            })
            .addOnFailureListener(e -> {
                android.util.Log.e("MemberRequests", "Query failed", e);
                Toast.makeText(this, "Failed to load requests: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            });
            
        } catch (Exception e) {
            android.util.Log.e("MemberRequests", "Error in loadMemberRequests", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void approveRequest(MemberRequest request, int position) {
        // Update request status to approved
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "approved");
        updates.put("approvedAt", System.currentTimeMillis());
        
        db.collection("member_requests")
            .document(request.id)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                // Add user to organization members
                addMemberToOrganization(request);
                
                // Send notification to student
                NotificationHelper.sendNotification(
                    this,
                    request.studentId,
                    NotificationHelper.TYPE_CLUB_JOIN,
                    "🎉 Request Approved!",
                    "Your request to join has been approved. Welcome aboard!"
                );
                
                // Remove from list
                requests.remove(position);
                adapter.notifyItemRemoved(position);
                
                Toast.makeText(this, "Request approved!", Toast.LENGTH_SHORT).show();
                
                // Check if list is empty
                if (requests.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to approve: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void rejectRequest(MemberRequest request, int position) {
        // Update request status to rejected
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("rejectedAt", System.currentTimeMillis());
        
        db.collection("member_requests")
            .document(request.id)
            .update(updates)
            .addOnSuccessListener(aVoid -> {
                // Send notification to student
                NotificationHelper.sendNotification(
                    this,
                    request.studentId,
                    NotificationHelper.TYPE_GENERAL,
                    "Request Update",
                    "Your membership request has been reviewed."
                );
                
                // Remove from list
                requests.remove(position);
                adapter.notifyItemRemoved(position);
                
                Toast.makeText(this, "Request rejected", Toast.LENGTH_SHORT).show();
                
                // Check if list is empty
                if (requests.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to reject: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    private void addMemberToOrganization(MemberRequest request) {
        // Add member to organization_members collection
        Map<String, Object> member = new HashMap<>();
        member.put("organizationId", organizationId);
        member.put("studentId", request.studentId);
        member.put("studentName", request.studentName);
        member.put("studentEmail", request.studentEmail);
        member.put("course", request.course);
        member.put("semester", request.semester);
        member.put("joinedAt", System.currentTimeMillis());
        member.put("role", "member");
        
        db.collection("organization_members")
            .add(member)
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Warning: Failed to add member to list", 
                    Toast.LENGTH_SHORT).show();
            });
    }
    
    // Data class for member requests
    private static class MemberRequest {
        String id;
        String studentId;
        String studentName;
        String studentEmail;
        String course;
        String semester;
        String message;
        Long timestamp;
    }
    
    // RecyclerView Adapter
    private class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.ViewHolder> {
        
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_member_request, parent, false);
            return new ViewHolder(view);
        }
        
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            MemberRequest request = requests.get(position);
            
            holder.nameText.setText(request.studentName);
            holder.emailText.setText(request.studentEmail);
            
            String details = "";
            if (request.course != null && !request.course.isEmpty()) {
                details = request.course;
                if (request.semester != null && !request.semester.isEmpty()) {
                    details += " - Sem " + request.semester;
                }
            }
            holder.detailsText.setText(details);
            
            if (request.message != null && !request.message.isEmpty()) {
                holder.messageText.setVisibility(View.VISIBLE);
                holder.messageText.setText("\"" + request.message + "\"");
            } else {
                holder.messageText.setVisibility(View.GONE);
            }
            
            holder.approveBtn.setOnClickListener(v -> {
                approveRequest(request, holder.getBindingAdapterPosition());
            });
            
            holder.rejectBtn.setOnClickListener(v -> {
                rejectRequest(request, holder.getBindingAdapterPosition());
            });
        }
        
        @Override
        public int getItemCount() {
            return requests.size();
        }
        
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText, emailText, detailsText, messageText;
            Button approveBtn, rejectBtn;
            
            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.nameText);
                emailText = itemView.findViewById(R.id.emailText);
                detailsText = itemView.findViewById(R.id.detailsText);
                messageText = itemView.findViewById(R.id.messageText);
                approveBtn = itemView.findViewById(R.id.approveBtn);
                rejectBtn = itemView.findViewById(R.id.rejectBtn);
            }
        }
    }
}
