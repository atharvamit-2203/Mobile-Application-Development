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
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 40, 40, 40);
        
        db = FirebaseFirestore.getInstance();
        
        TextView title = new TextView(this);
        title.setText("My Students");
        title.setTextSize(24);
        title.setPadding(0, 0, 0, 20);
        layout.addView(title);
        
        TextView lblFilter = new TextView(this);
        lblFilter.setText("Filter by Course:");
        lblFilter.setPadding(0, 10, 0, 10);
        layout.addView(lblFilter);
        
        spinnerCourse = new Spinner(this);
        layout.addView(spinnerCourse);
        
        tvStudentCount = new TextView(this);
        tvStudentCount.setText("Total Students: 0");
        tvStudentCount.setPadding(0, 20, 0, 10);
        tvStudentCount.setTextSize(16);
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
        btnRefresh.setText("Refresh");
        btnRefresh.setOnClickListener(v -> loadStudents());
        layout.addView(btnRefresh);
        
        Button btnBack = new Button(this);
        btnBack.setText("Back");
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
