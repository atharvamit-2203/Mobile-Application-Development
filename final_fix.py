import re

print("Applying precise fixes...")

# ===== FIX 1: AdminTeacherAssignmentActivity.java =====
with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Update variable declarations
content = content.replace(
    'private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch, spinnerSubject;',
    'private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch;'
)
content = content.replace(
    'private TextView tvStatus;',
    'private TextView tvStatus, tvSelectedTeacher;'
)

# 2. Remove Subject label and spinner UI (between spinnerFaculty and Course label)
# Find and remove the Subject section
subject_section = '''        // Subject
        TextView lblSubject = new TextView(this);
        lblSubject.setText("Subject");
        lblSubject.setTextSize(15);
        lblSubject.setTextColor(0xFFE2E8F0);
        lblSubject.setTypeface(null, android.graphics.Typeface.BOLD);
        lblSubject.setPadding(0, 20, 0, 8);
        layout.addView(lblSubject);
        
        spinnerSubject = new Spinner(this);
        spinnerSubject.setBackgroundColor(0xFF1E293B);
        spinnerSubject.setPadding(25, 25, 25, 25);
        spinnerParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinnerParams.bottomMargin = 5;
        spinnerSubject.setLayoutParams(spinnerParams);
        layout.addView(spinnerSubject);

'''
content = content.replace(subject_section, '')

# 3. Add teacher name display after spinnerFaculty
add_after = '        layout.addView(spinnerFaculty);'
teacher_display = '''        layout.addView(spinnerFaculty);

        // Selected Teacher Name Display
        tvSelectedTeacher = new TextView(this);
        tvSelectedTeacher.setTextSize(14);
        tvSelectedTeacher.setTextColor(0xFF10B981);
        tvSelectedTeacher.setPadding(10, 10, 10, 15);
        tvSelectedTeacher.setVisibility(TextView.GONE);
        tvSelectedTeacher.setTypeface(null, android.graphics.Typeface.BOLD);
        layout.addView(tvSelectedTeacher);
'''
content = content.replace(add_after, teacher_display)

# 4. Add faculty selection listener before closing onCreate
add_before_close = '        btnBack.setOnClickListener(v -> finish());'
listener_code = '''        btnBack.setOnClickListener(v -> finish());

        // Faculty selection listener
        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                if (position > 0) {
                    Map<String, Object> selectedFaculty = facultyList.get(position - 1);
                    String name = (String) selectedFaculty.get("name");
                    String department = (String) selectedFaculty.get("department");
                    tvSelectedTeacher.setText("Selected: " + name + " (" + department + ")");
                    tvSelectedTeacher.setVisibility(TextView.VISIBLE);
                } else {
                    tvSelectedTeacher.setVisibility(TextView.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvSelectedTeacher.setVisibility(TextView.GONE);
            }
        });'''
content = content.replace(add_before_close, listener_code)

# 5. Update courses in setupSpinners
old_courses = '''String[] courses = {"B.Tech Computer Engineering", "B.Tech IT", "B.Tech AIDS", 
            "B.Tech Electronics", "MCA", "M.Tech"};'''
new_courses = 'String[] courses = {"B.Tech CE", "B.Tech AIDS", "MBA Tech"};'
content = content.replace(old_courses, new_courses)

# 6. Remove subjects array from setupSpinners
subjects_section = '''        // Subjects
        String[] subjects = {"Data Structures", "Database Management", "Operating Systems", 
            "Computer Networks", "Software Engineering", "Web Development", 
            "Machine Learning", "Artificial Intelligence", "Cloud Computing"};
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, subjects);
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

'''
content = content.replace(subjects_section, '')

# 7. Remove subject variable from assignTeacher
content = content.replace('        String subject = spinnerSubject.getSelectedItem().toString();\n', '')

# 8. Remove subject from assignment
content = content.replace('                assignment.put("subject", subject);\n', '')

# 9. Remove subject from updateTimetable call
content = content.replace('updateTimetable(selectedFacultyId, subject, course, year, batch, studentIds);', '')

# 10. Remove updateTimetable method entirely
updatetimetable_pattern = r'    private void updateTimetable\(String facultyId.*?\n    \}\n'
content = re.sub(updatetimetable_pattern, '', content, flags=re.DOTALL)

with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("✓ AdminTeacherAssignmentActivity.java fixed")

# ===== FIX 2: ViewTeacherAssignmentsActivity.java =====
with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove subject line from display
content = content.replace('                        "Subject: " + subject + "\\n" +\n', '')

# Fix delete confirmation - add course variable
old_delete = '''    private void showDeleteConfirmation(Map<String, Object> assignment) {
        String facultyName = (String) assignment.get("faculty_name");
        String subject = (String) assignment.get("subject");

        new android.app.AlertDialog.Builder(this)
            .setTitle("Delete Assignment")
            .setMessage("Delete assignment of " + facultyName + " for " + subject + "?")'''

new_delete = '''    private void showDeleteConfirmation(Map<String, Object> assignment) {
        String facultyName = (String) assignment.get("faculty_name");
        String course = (String) assignment.get("course");

        new android.app.AlertDialog.Builder(this)
            .setTitle("Delete Assignment")
            .setMessage("Delete assignment of " + facultyName + " for " + course + "?")'''

content = content.replace(old_delete, new_delete)

with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("✓ ViewTeacherAssignmentsActivity.java fixed")

print("\n✅ All fixes applied successfully!")
