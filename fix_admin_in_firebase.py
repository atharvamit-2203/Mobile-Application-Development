"""
Fix admin role directly in Firebase using Admin SDK
"""
import firebase_admin
from firebase_admin import credentials, firestore, auth
import os

# Initialize Firebase Admin SDK
cred_path = r'D:\MyApplication16\serviceAccountKey.json'

if not os.path.exists(cred_path):
    print("=" * 60)
    print("⚠️  SERVICE ACCOUNT KEY NOT FOUND")
    print("=" * 60)
    print("\nTo fix the admin role, we need Firebase Admin SDK credentials.")
    print("\nOption 1: Download Service Account Key")
    print("1. Go to: https://console.firebase.google.com")
    print("2. Select your project: campusbuddy-f1c10")
    print("3. Click the gear icon → Project settings")
    print("4. Go to 'Service accounts' tab")
    print("5. Click 'Generate new private key'")
    print("6. Save as: D:\\MyApplication16\\serviceAccountKey.json")
    print("7. Run this script again")
    print("\nOption 2: Use the Admin Setup in the app")
    print("1. Open your app")
    print("2. Go to home screen")
    print("3. LONG PRESS the Admin button")
    print("4. Create a new admin account")
    print("=" * 60)
    exit(1)

try:
    # Initialize Firebase
    cred = credentials.Certificate(cred_path)
    firebase_admin.initialize_app(cred)
    
    db = firestore.client()
    
    print("=" * 60)
    print("FIXING ADMIN ROLE IN FIREBASE")
    print("=" * 60)
    print()
    
    # Get all users from Firestore
    users_ref = db.collection('users')
    users = users_ref.stream()
    
    admin_email = "admin@mpstme.edu.in"
    found = False
    
    for user_doc in users:
        user_data = user_doc.to_dict()
        email = user_data.get('email', '')
        current_role = user_data.get('role', 'NOT SET')
        
        print(f"User: {user_data.get('name', 'N/A')}")
        print(f"  Email: {email}")
        print(f"  Current Role: {current_role}")
        print(f"  ID: {user_doc.id}")
        
        if email.lower() == admin_email.lower():
            found = True
            print(f"  >>> FOUND ADMIN USER <<<")
            
            if current_role != 'admin':
                print(f"  ⚠️  Updating role from '{current_role}' to 'admin'...")
                
                # Update the role
                users_ref.document(user_doc.id).update({
                    'role': 'admin'
                })
                
                print(f"  ✅ Successfully updated role to 'admin'!")
            else:
                print(f"  ✅ Role is already set to 'admin'")
        
        print()
    
    if not found:
        print("=" * 60)
        print(f"❌ User with email '{admin_email}' not found")
        print("=" * 60)
        print("\nOptions:")
        print("1. Use the Admin Setup in the app (long press Admin button)")
        print("2. Create admin user manually in Firebase Console")
        print("3. Update the admin_email variable in this script")
    else:
        print("=" * 60)
        print("✅ DONE! Admin role has been fixed.")
        print("=" * 60)
        print("\nNext steps:")
        print("1. Close your app completely")
        print("2. Reopen the app")
        print("3. Log out if needed")
        print("4. Log in as admin")
        print("5. You should now see the Admin Dashboard")
        
except Exception as e:
    print(f"\n❌ Error: {str(e)}")
    print("\nTry using the Admin Setup in the app instead:")
    print("1. Open your app")
    print("2. Go to home screen")
    print("3. LONG PRESS the Admin button")
    print("4. Create a new admin account")
