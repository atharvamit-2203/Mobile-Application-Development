import re

# Read the manifest file
with open(r'D:\MyApplication16\app\src\main\AndroidManifest.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# Add TimetableActivity if not present
if 'TimetableActivity' not in content:
    last_activity = content.rfind('</activity>')
    insert_pos = content.find('>', last_activity) + 1
    new_activity = '\n        <activity android:name=".TimetableActivity"/>'
    content = content[:insert_pos] + new_activity + content[insert_pos:]

# Write back
with open(r'D:\MyApplication16\app\src\main\AndroidManifest.xml', 'w', encoding='utf-8') as f:
    f.write(content)

print("✅ AndroidManifest.xml updated with TimetableActivity")
