#!/usr/bin/env python3
# Script to update AdminTeacherAssignmentActivity.java

def update_admin_teacher_assignment():
    # Read the backup file
    with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java.backup', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    new_lines = []
    skip_next = False
    in_subject_section = False
    subject_section_start = -1
    
    for i, line in enumerate(lines):
        # Skip subject field from variable declaration
        if 'private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch, spinnerSubject;' in line:
            new_lines.append('    private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch;\n')
            continue
        
        # Add tvSelectedTeacher to TextView declaration
        if 'private TextView tvStatus;' in line:
            new_lines.append('    private TextView tvStatus, tvSelectedTeacher;\n')
            continue
        
        # Update subtitle
        if 'Connect teachers with students by subject and class' in line:
            new_lines.append(line.replace('Connect teachers with students by subject and class',
                                         'Connect teachers with students by course, year and batch'))
            continue
        
        # Skip Subject label and spinner in onCreate
        if '// Subject' in line and 'TextView lblSubject' not in line:
            in_subject_section = True
            subject_section_start = i
            continue
        
        if in_subject_section:
            # Count lines to skip (label + spinner + params + addView = about 15 lines)
            if i - subject_section_start < 15:
                continue
            else:
                in_subject_section = False
        
        # Add tvSelectedTeacher after spinnerFaculty
        if 'layout.addView(spinnerFaculty);' in line:
            new_lines.append(line)
            new_lines.append('\n')
            new_lines.append('        // Selected Teacher Name Display\n')
            new_lines.append('        tvSelectedTeacher = new TextView(this);\n')
            new_lines.append('        tvSelectedTeacher.setTextSize(14);\n')
            new_lines.append('        tvSelectedTeacher.setTextColor(0xFF10B981);\n')
            new_lines.append('        tvSelectedTeacher.setPadding(10, 10, 10, 15);\n')
            new_lines.append('        tvSelectedTeacher.setVisibility(TextView.GONE);\n')
            new_lines.append('        tvSelectedTeacher.setTypeface(null, android.graphics.Typeface.BOLD);\n')
            new_lines.append('        layout.addView(tvSelectedTeacher);\n')
            continue
        
        # Add faculty selection listener before closing onCreate
        if 'btnBack.setOnClickListener(v -> finish());' in line:
            new_lines.append(line)
            new_lines.append('\n')
            new_lines.append('        // Faculty selection listener to show selected teacher name\n')
            new_lines.append('        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {\n')
            new_lines.append('            @Override\n')
            new_lines.append('            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {\n')
            new_lines.append('                if (position > 0) {\n')
            new_lines.append('                    Map<String, Object> selectedFaculty = facultyList.get(position - 1);\n')
            new_lines.append('                    String name = (String) selectedFaculty.get("name");\n')
            new_lines.append('                    String department = (String) selectedFaculty.get("department");\n')
            new_lines.append('                    tvSelectedTeacher.setText("Selected: " + name + " (" + department + ")");\n')
            new_lines.append('                    tvSelectedTeacher.setVisibility(TextView.VISIBLE);\n')
            new_lines.append('                } else {\n')
            new_lines.append('                    tvSelectedTeacher.setVisibility(TextView.GONE);\n')
            new_lines.append('                }\n')
            new_lines.append('            }\n')
            new_lines.append('\n')
            new_lines.append('            @Override\n')
            new_lines.append('            public void onNothingSelected(AdapterView<?> parent) {\n')
            new_lines.append('                tvSelectedTeacher.setVisibility(TextView.GONE);\n')
            new_lines.append('            }\n')
            new_lines.append('        });\n')
            continue
        
        # Update course options in setupSpinners
        if '"B.Tech Computer Engineering", "B.Tech IT", "B.Tech AIDS"' in line:
            new_lines.append('        String[] courses = {"B.Tech CE", "B.Tech AIDS", "MBA Tech"};\n')
            # Skip next line too
            skip_next = True
            continue
        
        if skip_next:
            skip_next = False
            continue
        
        # Remove subject-related lines in setupSpinners
        if '// Subjects' in line or ('String[] subjects' in line and 'Data Structures' in line):
            # Skip the subjects section (about 6 lines)
            for j in range(6):
                if i + j < len(lines):
                    continue
            continue
        
        # Remove subject variable in assignTeacher
        if 'String subject = spinnerSubject.getSelectedItem().toString();' in line:
            continue
        
        # Remove subject from assignment
        if 'assignment.put("subject", subject);' in line:
            continue
        
        # Remove subject from updateTimetable call
        if 'updateTimetable(selectedFacultyId, subject, course, year, batch, studentIds);' in line:
            continue
        
        # Keep all other lines
        new_lines.append(line)
    
    # Write the updated file
    with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    print("AdminTeacherAssignmentActivity.java updated successfully!")

def update_view_teacher_assignments():
    # Read the file
    with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    new_lines = []
    
    for line in lines:
        # Update display string to remove subject
        if 'String display = facultyName + "\\n" +' in line:
            new_lines.append('                    String display = "Teacher: " + facultyName + "\\n" +\n')
            continue
        
        if '"Subject: " + subject + "\\n" +' in line:
            continue
        
        if 'course + " - Year " + year + " - Batch " + batch + "\\n" +' in line:
            new_lines.append('                        "Course: " + course + " - Year " + year + " - Batch " + batch + "\\n" +\n')
            continue
        
        # Update delete confirmation
        if 'setMessage("Delete assignment of " + facultyName + " for " + subject + "?")' in line:
            new_lines.append('            .setMessage("Delete assignment of " + facultyName + " for " + course + "?")\n')
            continue
        
        # Keep all other lines
        new_lines.append(line)
    
    # Write the updated file
    with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    
    print("ViewTeacherAssignmentsActivity.java updated successfully!")

if __name__ == '__main__':
    update_admin_teacher_assignment()
    update_view_teacher_assignments()
    print("\nAll files updated successfully!")
