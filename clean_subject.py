print("Removing spinnerSubject references by line numbers...")

# Read the file
with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'r', encoding='utf-8') as f:
    lines = f.readlines()

# Lines to remove (0-indexed, so subtract 1 from line numbers)
# Lines 88-107: Subject label and spinner
# Line 247-252: Subjects array setup  
# Line 312: subject variable
# Line 357: subject in assignment
# Line 368: updateTimetable call
# Lines 390-410: updateTimetable method

# Mark lines to delete
lines_to_delete = set()

# Find and mark Subject UI section (around line 88-107)
for i in range(len(lines)):
    if i >= 87 and i <= 106 and ('lblSubject' in lines[i] or 'spinnerSubject' in lines[i] or ('// Subject' in lines[i] and i > 80)):
        lines_to_delete.add(i)

# Find subjects array in setupSpinners
in_subjects_section = False
for i in range(len(lines)):
    if '// Subjects' in lines[i] and i > 200:
        in_subjects_section = True
    if in_subjects_section:
        lines_to_delete.add(i)
        if 'spinnerSubject.setAdapter' in lines[i]:
            in_subjects_section = False

# Find subject variable declaration
for i in range(len(lines)):
    if 'String subject = spinnerSubject.getSelectedItem' in lines[i]:
        lines_to_delete.add(i)

# Find assignment.put("subject"
for i in range(len(lines)):
    if 'assignment.put("subject", subject)' in lines[i]:
        lines_to_delete.add(i)

# Find updateTimetable call
for i in range(len(lines)):
    if 'updateTimetable(selectedFacultyId, subject' in lines[i]:
        lines_to_delete.add(i)

# Find and mark updateTimetable method
in_update_timetable = False
for i in range(len(lines)):
    if 'private void updateTimetable(' in lines[i]:
        in_update_timetable = True
    if in_update_timetable:
        lines_to_delete.add(i)
        if lines[i].strip() == '}' and i > 380:
            in_update_timetable = False

# Write only non-deleted lines
new_lines = [lines[i] for i in range(len(lines)) if i not in lines_to_delete]

with open('app/src/main/java/com/campusbuddy/AdminTeacherAssignmentActivity.java', 'w', encoding='utf-8') as f:
    f.writelines(new_lines)

print(f"✓ Removed {len(lines_to_delete)} lines")
print("✓ AdminTeacherAssignmentActivity.java cleaned!")
