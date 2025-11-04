print("Applying comprehensive fixes...")

# Fix AdminTeacherAssignmentActivity - remove all spinnerSubject UI code
with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

new_lines = []
skip_count = 0

for i, line in enumerate(lines):
    if skip_count > 0:
        skip_count -= 1
        continue
    
    # Skip the entire Subject section (label + spinner)
    if '// Subject' in line and i > 80:  # Make sure it's the UI section, not setupSpinners
        skip_count = 20  # Skip next 20 lines to remove entire subject UI section
        continue
    
    # Remove subject lines in setupSpinners
    if 'String[] subjects = {"Data Structures"' in line:
        skip_count = 6  # Skip subjects array and adapter setup
        continue
    
    new_lines.append(line)

with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print("✓ AdminTeacherAssignmentActivity.java fixed")

# Fix ViewTeacherAssignmentsActivity - add course variable in showDeleteConfirmation
with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'r', encoding='utf-8') as f:
    content = f.read()

# Add course variable before the delete confirmation dialog
old_method = '''    private void showDeleteConfirmation(Map<String, Object> assignment) {
        String facultyName = (String) assignment.get("faculty_name");

        new android.app.AlertDialog.Builder(this)'''

new_method = '''    private void showDeleteConfirmation(Map<String, Object> assignment) {
        String facultyName = (String) assignment.get("faculty_name");
        String course = (String) assignment.get("course");

        new android.app.AlertDialog.Builder(this)'''

content = content.replace(old_method, new_method)

with open('app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java', 'w', encoding='utf-8') as f:
    f.write(content)

print("✓ ViewTeacherAssignmentsActivity.java fixed")
print("\n✓ All fixes complete!")
