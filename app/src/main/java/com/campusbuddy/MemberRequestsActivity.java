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

/**
 * Activity for organizations to view and manage member join requests
 */
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
        setContentView(R.layout.activity_member_requests);
        
        db = FirebaseFirestore.getInstance();
        organizationId = Prefs.getInstance(this).getUserId();
        requests = new ArrayList<>();
        
        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        emptyView = findViewById(R.id.emptyView);
        
        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RequestsAdapter();
        recyclerView.setAdapter(adapter);
        
        // Back button
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // Load requests
        loadMemberRequests();
    }
    
    private void loadMemberRequests() {
        // Query for pending join requests for this organization
        db.collection("member_requests")
            .whereEqualTo("organizationId", organizationId)
            .whereEqualTo("status", "pending")
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                requests.clear();
                
                for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                    MemberRequest request = new MemberRequest();
                    request.id = doc.getId();
                    request.studentId = doc.getString("studentId");
                    request.studentName = doc.getString("studentName");
                    request.studentEmail = doc.getString("studentEmail");
                    request.course = doc.getString("course");
                    request.semester = doc.getString("semester");
                    request.message = doc.getString("message");
                    request.timestamp = doc.getLong("timestamp");
                    
                    requests.add(request);
                }
                
                // Update UI
                if (requests.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Failed to load requests: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            });
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
