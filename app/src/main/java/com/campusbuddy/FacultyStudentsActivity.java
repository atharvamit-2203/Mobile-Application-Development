package com.campusbuddy;

import android.app.Activity;
import android.os.Bundle;
import android.widget.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.*;

public class FacultyStudentsActivity extends Activity {
    private RecyclerView recyclerView;
    private StudentsAdapter adapter;
    private List<StudentItem> students = new ArrayList<>();
    private FirebaseFirestore db;
    private Spinner spinnerCourse;
    private TextView tvStudentCount;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xFF0F172A);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        
        db = FirebaseFirestore.getInstance();
        
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
        title.setText("👨‍🎓 My Students");
        title.setTextSize(28);
        title.setTextColor(0xFFE2E8F0);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        headerLayout.addView(title);
        
        TextView subtitle = new TextView(this);
        subtitle.setText("View your assigned students");
        subtitle.setTextSize(14);
        subtitle.setTextColor(0xFF94A3B8);
        subtitle.setPadding(0, 8, 0, 0);
        headerLayout.addView(subtitle);
        
        headerCard.addView(headerLayout);
        layout.addView(headerCard);
        
        // Filter Label
        TextView lblFilter = new TextView(this);
        lblFilter.setText("📚 Filter by Course");
        lblFilter.setTextSize(14);
        lblFilter.setTextColor(0xFFE2E8F0);
        lblFilter.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams filterLabelParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        filterLabelParams.topMargin = 10;
        filterLabelParams.bottomMargin = 10;
        lblFilter.setLayoutParams(filterLabelParams);
        layout.addView(lblFilter);
        
        spinnerCourse = new Spinner(this);
        spinnerCourse.setBackgroundColor(0xFF1E293B);
        spinnerCourse.setPadding(20, 20, 20, 20);
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 20;
        spinnerCourse.setLayoutParams(spinnerParams);
        layout.addView(spinnerCourse);
        
        tvStudentCount = new TextView(this);
        tvStudentCount.setText("Total Students: 0");
        tvStudentCount.setTextSize(16);
        tvStudentCount.setTextColor(0xFF94A3B8);
        tvStudentCount.setTypeface(null, android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams countParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        countParams.topMargin = 10;
        countParams.bottomMargin = 15;
        tvStudentCount.setLayoutParams(countParams);
        layout.addView(tvStudentCount);
        
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(ProgressBar.GONE);
        layout.addView(progressBar);
        
        LinearLayout recyclerContainer = new LinearLayout(this);
        recyclerContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            800
        );
        recyclerContainer.setLayoutParams(params);
        
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentsAdapter(students, this);
        recyclerView.setAdapter(adapter);
        recyclerContainer.addView(recyclerView);
        layout.addView(recyclerContainer);
        
        Button btnRefresh = new Button(this);
        btnRefresh.setText("🔄 Refresh");
        btnRefresh.setBackgroundColor(0xFF3B82F6);
        btnRefresh.setTextColor(0xFFFFFFFF);
        btnRefresh.setTextSize(16);
        btnRefresh.setPadding(30, 30, 30, 30);
        btnRefresh.setOnClickListener(v -> loadStudents());
        LinearLayout.LayoutParams refreshParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        refreshParams.topMargin = 20;
        refreshParams.bottomMargin = 15;
        btnRefresh.setLayoutParams(refreshParams);
        layout.addView(btnRefresh);
        
        Button btnBack = new Button(this);
        btnBack.setText("← Back");
        btnBack.setBackgroundColor(0xFF475569);
        btnBack.setTextColor(0xFFFFFFFF);
        btnBack.setTextSize(16);
        btnBack.setPadding(30, 30, 30, 30);
        btnBack.setOnClickListener(v -> finish());
        layout.addView(btnBack);
        
        scroll.addView(layout);
        setContentView(scroll);
        
        loadFacultyCourses();
        loadStudents();
    }
    
    private void loadFacultyCourses() {
        String facultyId = Prefs.getInstance(this).getUserId();
        
        db.collection("faculty").document(facultyId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    List<String> courses = (List<String>) doc.get("courses");
                    if (courses == null) courses = new ArrayList<>();
                    courses.add(0, "All Courses");
                    
                    ArrayAdapter<String> courseAdapter = new ArrayAdapter<>(
                        this, android.R.layout.simple_spinner_item, courses);
                    courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourse.setAdapter(courseAdapter);
                    
                    spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                            loadStudents();
                        }
                        
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            });
    }
    
    private void loadStudents() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        students.clear();
        
        String facultyId = Prefs.getInstance(this).getUserId();
        String selectedCourse = spinnerCourse.getSelectedItem() != null ? 
            spinnerCourse.getSelectedItem().toString() : "All Courses";
        
        db.collection("timetable")
            .whereEqualTo("faculty_id", facultyId)
            .get()
            .addOnSuccessListener(querySnapshot -> {
                Set<String> studentIds = new HashSet<>();
                Set<String> courses = new HashSet<>();
                
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    String course = doc.getString("course");
                    String subject = doc.getString("subject");
                    if (course != null) courses.add(course);
                    
                    if (selectedCourse.equals("All Courses") || selectedCourse.equals(course)) {
                        List<String> classStudents = (List<String>) doc.get("student_ids");
                        if (classStudents != null) {
                            studentIds.addAll(classStudents);
                        }
                    }
                }
                
                if (studentIds.isEmpty()) {
                    progressBar.setVisibility(ProgressBar.GONE);
                    tvStudentCount.setText("Total Students: 0");
                    adapter.notifyDataSetChanged();
                    return;
                }
                
                db.collection("users")
                    .get()
                    .addOnSuccessListener(studentSnapshot -> {
                        for (QueryDocumentSnapshot studentDoc : studentSnapshot) {
                            if (studentIds.contains(studentDoc.getId())) {
                                String name = studentDoc.getString("name");
                                String email = studentDoc.getString("email");
                                String course = studentDoc.getString("course");
                                String semester = studentDoc.getString("semester");
                                
                                students.add(new StudentItem(
                                    studentDoc.getId(), name, email, course, semester));
                            }
                        }
                        
                        progressBar.setVisibility(ProgressBar.GONE);
                        tvStudentCount.setText("Total Students: " + students.size());
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(ProgressBar.GONE);
                        Toast.makeText(this, "Error loading students", Toast.LENGTH_SHORT).show();
                    });
            })
            .addOnFailureListener(e -> {
                progressBar.setVisibility(ProgressBar.GONE);
                Toast.makeText(this, "Error loading timetable", Toast.LENGTH_SHORT).show();
            });
    }
    
    public static class StudentItem {
        String id, name, email, course, semester;
        
        public StudentItem(String id, String name, String email, String course, String semester) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.course = course;
            this.semester = semester;
        }
    }
    
    public static class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {
        private List<StudentItem> students;
        private Activity context;
        
        public StudentsAdapter(List<StudentItem> students, Activity context) {
            this.students = students;
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
            StudentItem student = students.get(position);
            holder.text1.setText(student.name != null ? student.name : "N/A");
            String details = (student.course != null ? student.course : "N/A") + 
                           " - Sem " + (student.semester != null ? student.semester : "N/A");
            holder.text2.setText(details);
            
            holder.itemView.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(context)
                    .setTitle("Student Details")
                    .setMessage("Name: " + student.name + 
                              "\nEmail: " + student.email +
                              "\nCourse: " + student.course +
                              "\nSemester: " + student.semester +
                              "\nID: " + student.id)
                    .setPositiveButton("OK", null)
                    .show();
            });
        }
        
        @Override
        public int getItemCount() {
            return students.size();
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
