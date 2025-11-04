with open("app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java", "r", encoding="utf-8") as f:
    content = f.read()

content = content.replace("                        \"Subject: \" + subject + \"\\n\" +\n", "")
content = content.replace("for \" + subject + \"?\"", "for \" + course + \"?\"")

with open("app/src/main/java/com/campusbuddy/ViewTeacherAssignmentsActivity.java", "w", encoding="utf-8") as f:
    f.write(content)

print("Done!")
