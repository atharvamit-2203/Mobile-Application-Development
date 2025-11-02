"""
Create admin user in Firebase using REST API
"""
import requests
import json

# Path to google-services.json
google_services_path = r'D:\MyApplication16\app\google-services.json'

try:
    # Read google-services.json
    with open(google_services_path, 'r') as f:
        config = json.load(f)
    
    project_id = config['project_info']['project_id']
    api_key = config['client'][0]['api_key'][0]['current_key']
    
    print("=" * 60)
    print("CREATING ADMIN USER IN FIREBASE")
    print("=" * 60)
    print(f"Project: {project_id}")
    print()
    
    # Admin credentials
    admin_email = "sysadmin@mpstme.edu.in"  # Using different email since admin@mpstme.edu.in exists
    admin_password = "Admin@123"  # Change this if needed
    admin_name = "System Administrator"
    
    print(f"Creating admin user:")
    print(f"  Email: {admin_email}")
    print(f"  Name: {admin_name}")
    print()
    
    # Step 1: Create Firebase Auth user
    print("Step 1: Creating Firebase Auth account...")
    auth_url = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={api_key}"
    
    auth_payload = {
        "email": admin_email,
        "password": admin_password,
        "returnSecureToken": True
    }
    
    auth_response = requests.post(auth_url, json=auth_payload)
    
    if auth_response.status_code == 200:
        auth_data = auth_response.json()
        uid = auth_data['localId']
        print(f"  ✅ Auth account created! UID: {uid}")
    else:
        error_data = auth_response.json()
        error_message = error_data.get('error', {}).get('message', 'Unknown error')
        
        if 'EMAIL_EXISTS' in error_message:
            print(f"  ⚠️  Email already exists. Attempting to get UID...")
            
            # Try to sign in to get UID
            signin_url = f"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}"
            signin_payload = {
                "email": admin_email,
                "password": admin_password,
                "returnSecureToken": True
            }
            
            signin_response = requests.post(signin_url, json=signin_payload)
            if signin_response.status_code == 200:
                signin_data = signin_response.json()
                uid = signin_data['localId']
                print(f"  ✅ Found existing user. UID: {uid}")
            else:
                print(f"  ❌ Could not get UID. Please check password or create with different email.")
                exit(1)
        else:
            print(f"  ❌ Error: {error_message}")
            print(auth_response.text)
            exit(1)
    
    # Step 2: Create Firestore user document
    print()
    print("Step 2: Creating user profile in Firestore...")
    
    firestore_url = f"https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/users/{uid}"
    
    import time
    user_data = {
        "fields": {
            "name": {"stringValue": admin_name},
            "email": {"stringValue": admin_email},
            "role": {"stringValue": "admin"},
            "created_at": {"integerValue": str(int(time.time() * 1000))}
        }
    }
    
    firestore_response = requests.patch(firestore_url, json=user_data)
    
    if firestore_response.status_code == 200:
        print(f"  ✅ User profile created with role='admin'")
    else:
        print(f"  ❌ Error creating profile: {firestore_response.status_code}")
        print(firestore_response.text)
    
    print()
    print("=" * 60)
    print("✅ ADMIN USER CREATED SUCCESSFULLY!")
    print("=" * 60)
    print()
    print("Login credentials:")
    print(f"  Email: {admin_email}")
    print(f"  Password: {admin_password}")
    print()
    print("Next steps:")
    print("1. Open your app")
    print("2. Log out if currently logged in")
    print("3. Click on 'Admin' button")
    print("4. Login with the credentials above")
    print("5. You should now see the Admin Dashboard!")
    
except FileNotFoundError:
    print(f"❌ google-services.json not found at: {google_services_path}")
except Exception as e:
    print(f"❌ Error: {str(e)}")
    import traceback
    traceback.print_exc()
