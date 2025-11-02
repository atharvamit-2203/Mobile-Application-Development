"""
Add AdminSetupActivity to AndroidManifest.xml
"""
import xml.etree.ElementTree as ET
import os

manifest_path = r'D:\MyApplication16\app\src\main\AndroidManifest.xml'

# Check if file exists
if not os.path.exists(manifest_path):
    print(f"❌ AndroidManifest.xml not found at {manifest_path}")
    exit(1)

try:
    # Parse the XML
    tree = ET.parse(manifest_path)
    root = tree.getroot()
    
    # Define namespace
    namespace = {'android': 'http://schemas.android.com/apk/res/android'}
    ET.register_namespace('android', 'http://schemas.android.com/apk/res/android')
    
    # Find the application element
    application = root.find('application')
    
    if application is None:
        print("❌ Could not find <application> element")
        exit(1)
    
    # Check if activity already exists
    existing_activities = []
    for activity in application.findall('activity'):
        name = activity.get('{http://schemas.android.com/apk/res/android}name')
        if name:
            existing_activities.append(name)
    
    activity_name = 'com.campusbuddy.AdminSetupActivity'
    
    # Check if activity already exists
    if activity_name in existing_activities or 'AdminSetupActivity' in [a.split('.')[-1] for a in existing_activities]:
        print(f"ℹ️  AdminSetupActivity already exists in manifest")
    else:
        # Create new activity element
        activity = ET.SubElement(application, 'activity')
        activity.set('{http://schemas.android.com/apk/res/android}name', '.AdminSetupActivity')
        activity.set('{http://schemas.android.com/apk/res/android}exported', 'false')
        
        # Write back to file
        tree.write(manifest_path, encoding='utf-8', xml_declaration=True)
        print(f"✅ Added AdminSetupActivity to AndroidManifest.xml")
        print(f"\n📝 Note: Long press the Admin button on the home screen to access Admin Setup")
    
except Exception as e:
    print(f"❌ Error updating AndroidManifest.xml: {str(e)}")
    import traceback
    traceback.print_exc()
