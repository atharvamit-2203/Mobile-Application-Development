import requests
import json

# Firebase Firestore REST API endpoint
# Replace with your actual project ID
PROJECT_ID = "campusbuddy-f1c10"  # Update this with your Firebase project ID
BASE_URL = f"https://firestore.googleapis.com/v1/projects/{PROJECT_ID}/databases/(default)/documents"

def get_users():
    """Get all users from Firestore"""
    url = f"{BASE_URL}/users"
    response = requests.get(url)
    
    if response.status_code == 200:
        data = response.json()
        return data.get('documents', [])
    else:
        print(f"Error fetching users: {response.status_code}")
        print(response.text)
        return []

def update_user_role(user_id, role):
    """Update user role in Firestore"""
    url = f"{BASE_URL}/users/{user_id}"
    
    # Firestore update payload
    payload = {
        "fields": {
            "role": {
                "stringValue": role
            }
        }
    }
    
    # Use PATCH with updateMask to only update the role field
    params = {
        "updateMask.fieldPaths": "role"
    }
    
    response = requests.patch(url, json=payload, params=params)
    
    if response.status_code == 200:
        print(f"✅ Updated user {user_id} role to '{role}'")
        return True
    else:
        print(f"❌ Error updating user {user_id}: {response.status_code}")
        print(response.text)
        return False

def extract_field_value(field_data):
    """Extract value from Firestore field format"""
    if not field_data:
        return None
    
    # Check different field types
    if 'stringValue' in field_data:
        return field_data['stringValue']
    elif 'integerValue' in field_data:
        return field_data['integerValue']
    elif 'booleanValue' in field_data:
        return field_data['booleanValue']
    
    return None

def main():
    print("=" * 60)
    print("CHECKING AND FIXING ADMIN USER ROLE")
    print("=" * 60)
    
    users = get_users()
    
    if not users:
        print("❌ No users found or error fetching users")
        return
    
    print(f"\n📊 Found {len(users)} users in database\n")
    
    admin_found = False
    
    for user_doc in users:
        # Extract user ID from document name
        # Format: projects/{project}/databases/{database}/documents/users/{userId}
        user_id = user_doc['name'].split('/')[-1]
        
        fields = user_doc.get('fields', {})
        
        # Extract user data
        email = extract_field_value(fields.get('email'))
        role = extract_field_value(fields.get('role'))
        name = extract_field_value(fields.get('name'))
        
        print(f"User: {name or 'N/A'}")
        print(f"  Email: {email or 'N/A'}")
        print(f"  Role: {role or 'NOT SET'}")
        print(f"  ID: {user_id}")
        
        # Check if this might be an admin user
        # You can identify admin by email or other criteria
        if email and ('admin' in email.lower() or role == 'admin'):
            admin_found = True
            
            if role != 'admin':
                print(f"  ⚠️  This appears to be an admin user but role is '{role or 'NOT SET'}'")
                
                # Ask for confirmation
                confirm = input(f"  ❓ Update role to 'admin' for {email}? (y/n): ")
                if confirm.lower() == 'y':
                    update_user_role(user_id, 'admin')
            else:
                print(f"  ✅ Role is correctly set to 'admin'")
        
        print()
    
    if not admin_found:
        print("=" * 60)
        print("⚠️  NO ADMIN USER FOUND")
        print("=" * 60)
        print("\nTo manually set a user as admin:")
        print("1. Find the user's ID from the list above")
        print("2. Run this script again or update directly in Firebase Console")
        print("\nOr enter admin user details now:")
        
        admin_email = input("Admin email (or press Enter to skip): ").strip()
        
        if admin_email:
            # Find user by email
            for user_doc in users:
                user_id = user_doc['name'].split('/')[-1]
                fields = user_doc.get('fields', {})
                email = extract_field_value(fields.get('email'))
                
                if email and email.lower() == admin_email.lower():
                    print(f"\n✅ Found user: {email}")
                    confirm = input("Set this user as admin? (y/n): ")
                    if confirm.lower() == 'y':
                        update_user_role(user_id, 'admin')
                    break
            else:
                print(f"❌ User with email '{admin_email}' not found")
    
    print("\n" + "=" * 60)
    print("DONE")
    print("=" * 60)

if __name__ == "__main__":
    main()
