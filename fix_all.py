import re

print("Starting fixes...")

# 1. Fix AdminTeacherAssignmentActivity.java
print("\n1. Fixing AdminTeacherAssignmentActivity.java...")
with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java.backup', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove spinnerSubject
content = content.replace('private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch, spinnerSubject;',
                         'private Spinner spinnerFaculty, spinnerCourse, spinnerYear, spinnerBatch;')
content = content.replace('private TextView tvStatus;', 'private TextView tvStatus, tvSelectedTeacher;')

# Update courses - simple replace
old_courses = '"B.Tech Computer Engineering", "B.Tech IT", "B.Tech AIDS", \n            "B.Tech Electronics", "MCA", "M.Tech"'
new_courses = '"B.Tech CE", "B.Tech AIDS", "MBA Tech"'
content = content.replace(old_courses, new_courses)

# Remove subject lines
content = content.replace('        String subject = spinnerSubject.getSelectedItem().toString();\n', '')
content = content.replace('                assignment.put("subject", subject);\n', '')
content = content.replace('                        updateTimetable(selectedFacultyId, subject, course, year, batch, studentIds);\n', '')

with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("   ✓ Done")

# 2. Fix ViewTeacherAssignmentsActivity.java
print("\n2. Fixing ViewTeacherAssignmentsActivity.java...")
with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Remove subject line from display
content = content.replace('                        "Subject: " + subject + "\\n" +\n', '')
# Update delete message
content = content.replace('"Delete assignment of " + facultyName + " for " + subject + "?"',
                         '"Delete assignment of " + facultyName + " for " + course + "?"')

with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)
print("   ✓ Done")

print("\n✓ All fixes completed!")
print("\nNext: Build the project with: .\\gradlew.bat assembleDebug")
