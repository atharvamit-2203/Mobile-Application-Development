import re

# Read the manifest file
with open(r'D:\MyApplication16\app\src\main\AndroidManifest.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Activities to add
new_activities = """
        <activity android:name=".ProfileActivity"/>
        <activity android:name=".FacultyRoomBookingActivity"/>
        <activity android:name=".OrganizationEventActivity"/>"""

# Find the last activity closing tag
last_activity = content.rfind('</activity>')
if last_activity != -1:
    # Find the next closing angle bracket after the last activity
    insert_pos = content.find('>', last_activity) + 1
    
    # Check if these activities already exist
    if 'ProfileActivity' not in content:
        content = content[:insert_pos] + new_activities + content[insert_pos:]

# Write back
with open(r'D:\MyApplication16\app\src\main\AndroidManifest.xml', 'w', encoding='utf-8') as f:
    f.write(content)

print("✅ AndroidManifest.xml updated with new activities")
print("   - ProfileActivity")
print("   - FacultyRoomBookingActivity")
print("   - OrganizationEventActivity")
