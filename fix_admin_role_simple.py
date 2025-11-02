"""
Fix admin role in Firebase using REST API
"""
import requests
import json

# Path to google-services.json
google_services_path = r'D:\MyApplication16\app\google-services.json'

try:
    # Read google-services.json to get project info
    with open(google_services_path, 'r') as f:
        config = json.load(f)
    
    project_id = config['project_info']['project_id']
    api_key = config['client'][0]['api_key'][0]['current_key']
    
    print("=" * 60)
    print("FIXING ADMIN ROLE IN FIREBASE")
    print("=" * 60)
    print(f"Project: {project_id}")
    print()
    
    # First, let's get the user by email to find the UID
    admin_email = "admin@mpstme.edu.in"
    
    print(f"Looking for user: {admin_email}")
    print()
    
    # Get all users from Firestore
    firestore_url = f"https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/users"
    
    response = requests.get(firestore_url)
    
    if response.status_code == 200:
        data = response.json()
        documents = data.get('documents', [])
        
        found = False
        
        for doc in documents:
            fields = doc.get('fields', {})
            
            # Extract values
            email = fields.get('email', {}).get('stringValue', '')
            current_role = fields.get('role', {}).get('stringValue', 'NOT SET')
            name = fields.get('name', {}).get('stringValue', 'N/A')
            
            # Get document ID (UID)
            doc_path = doc.get('name', '')
            uid = doc_path.split('/')[-1]
            
            print(f"User: {name}")
            print(f"  Email: {email}")
            print(f"  Current Role: {current_role}")
            print(f"  UID: {uid}")
            
            if email.lower() == admin_email.lower():
                found = True
                print(f"  >>> FOUND ADMIN USER <<<")
                
                if current_role != 'admin':
                    print(f"  ⚠️  Updating role from '{current_role}' to 'admin'...")
                    
                    # Update the role using PATCH
                    update_url = f"{firestore_url}/{uid}?updateMask.fieldPaths=role"
                    
                    update_payload = {
                        "fields": {
                            "role": {
                                "stringValue": "admin"
                            }
                        }
                    }
                    
                    update_response = requests.patch(update_url, json=update_payload)
                    
                    if update_response.status_code == 200:
                        print(f"  ✅ Successfully updated role to 'admin'!")
                    else:
                        print(f"  ❌ Error updating role: {update_response.status_code}")
                        print(f"  {update_response.text}")
                else:
                    print(f"  ✅ Role is already set to 'admin'")
            
            print()
        
        if not found:
            print("=" * 60)
            print(f"❌ User with email '{admin_email}' not found")
            print("=" * 60)
            print("\nAvailable options:")
            print("1. Use the Admin Setup in the app:")
            print("   - Open your app")
            print("   - Log out if logged in")
            print("   - Go to home screen")
            print("   - LONG PRESS the Admin button")
            print("   - Create a new admin account")
            print()
            print("2. Change the admin_email in this script and run again")
        else:
            print("=" * 60)
            print("✅ DONE! Admin role has been fixed.")
            print("=" * 60)
            print("\nNext steps:")
            print("1. Close your app completely")
            print("2. Clear app data (Settings → Apps → Campus Buddy → Clear Data)")
            print("   OR just log out and log back in")
            print("3. Log in as admin with: admin@mpstme.edu.in")
            print("4. You should now see the Admin Dashboard")
    
    elif response.status_code == 403:
        print("❌ Permission denied. Firebase security rules may be blocking access.")
        print()
        print("Solution: Use the Admin Setup in the app instead:")
        print("1. Open your app")
        print("2. Log out if logged in")
        print("3. Go to home screen")
        print("4. LONG PRESS the Admin button for 2-3 seconds")
        print("5. Create a new admin account")
    else:
        print(f"❌ Error: {response.status_code}")
        print(response.text)
        print()
        print("Use the Admin Setup in the app:")
        print("- LONG PRESS the Admin button on home screen")
        print("- Create a new admin account")

except FileNotFoundError:
    print(f"❌ google-services.json not found at: {google_services_path}")
    print()
    print("Please ensure google-services.json is in: D:\\MyApplication16\\app\\")
except Exception as e:
    print(f"❌ Error: {str(e)}")
    import traceback
    traceback.print_exc()
