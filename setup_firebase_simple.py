#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Simple Firebase Setup Script using REST API
No service account needed - just your Project ID and API Key
"""

import requests
import json
import time

def get_firebase_config():
    """Get Firebase configuration from user"""
    print("\n📋 You need the following from Firebase Console:")
    print("   Go to: Firebase Console > Project Settings > General")
    print()
    
    project_id = input("Enter your Firebase Project ID: ").strip()
    api_key = input("Enter your Web API Key: ").strip()
    
    return project_id, api_key

def create_auth_user(api_key, email, password):
    """Create a Firebase Auth user"""
    url = f"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key={api_key}"
    
    data = {
        "email": email,
        "password": password,
        "returnSecureToken": True
    }
    
    try:
        response = requests.post(url, json=data)
        if response.status_code == 200:
            result = response.json()
            print(f"  ✅ Created auth user: {email}")
            return result['localId']
        else:
            error = response.json().get('error', {}).get('message', 'Unknown error')
            if 'EMAIL_EXISTS' in error:
                print(f"  ℹ️  Auth user already exists: {email}")
                # Sign in to get the user ID
                url = f"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key={api_key}"
                response = requests.post(url, json={"email": email, "password": password, "returnSecureToken": True})
                if response.status_code == 200:
                    return response.json()['localId']
            else:
                print(f"  ❌ Error: {error}")
            return None
    except Exception as e:
        print(f"  ❌ Error creating user: {e}")
        return None

def add_firestore_document(project_id, collection, data):
    """Add a document to Firestore using REST API"""
    url = f"https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/{collection}"
    
    # Convert data to Firestore format
    fields = {}
    for key, value in data.items():
        # Check boolean FIRST (before int, since bool is subclass of int in Python)
        if isinstance(value, bool):
            fields[key] = {"booleanValue": value}
        elif isinstance(value, str):
            fields[key] = {"stringValue": value}
        elif isinstance(value, int):
            fields[key] = {"integerValue": str(value)}
        elif isinstance(value, float):
            fields[key] = {"doubleValue": value}
        elif isinstance(value, list):
            array_values = []
            for item in value:
                if isinstance(item, str):
                    array_values.append({"stringValue": item})
                elif isinstance(item, int):
                    array_values.append({"integerValue": str(item)})
            fields[key] = {"arrayValue": {"values": array_values}}
    
    payload = {"fields": fields}
    
    try:
        response = requests.post(url, json=payload)
        if response.status_code in [200, 201]:
            return True
        else:
            print(f"    ❌ Error: {response.text}")
            return False
    except Exception as e:
        print(f"    ❌ Error: {e}")
        return False

def setup_database(project_id, api_key):
    """Setup all collections"""
    
    # Create test user first
    print("\n📝 Creating test users in Firebase Auth...")
    user_id = create_auth_user(api_key, "student@mpstme.edu.in", "password123")
    create_auth_user(api_key, "faculty@mpstme.edu.in", "password123")
    create_auth_user(api_key, "admin@mpstme.edu.in", "password123")
    
    print("\n📝 Creating Firestore collections...")
    print("\n⚠️  Note: Firestore REST API without authentication may be limited.")
    print("For full setup, please use the Firebase Console or Admin SDK script.\n")
    
    # Sample data for each collection
    collections_data = {
        'colleges': [
            {
                'name': 'MPSTME Mumbai',
                'code': 'MPSTME',
                'type': 'Engineering College',
                'city': 'Mumbai',
                'state': 'Maharashtra',
                'is_active': True
            }
        ],
        'clubs': [
            {'name': 'Coding Club', 'category': 'Technical', 'member_count': 45, 'max_members': 100, 'is_active': True},
            {'name': 'Robotics Club', 'category': 'Technical', 'member_count': 32, 'max_members': 50, 'is_active': True},
            {'name': 'Music Club', 'category': 'Cultural', 'member_count': 56, 'max_members': 80, 'is_active': True},
            {'name': 'Dance Club', 'category': 'Cultural', 'member_count': 48, 'max_members': 60, 'is_active': True}
        ],
        'events': [
            {
                'title': 'Tech Workshop: Firebase for Mobile Apps',
                'description': 'Learn Firebase integration',
                'event_type': 'workshop',
                'venue': 'Room 301',
                'max_participants': 50,
                'current_participants': 12,
                'is_public': True
            },
            {
                'title': 'Annual Tech Fest 2025',
                'description': 'College-wide technology festival',
                'event_type': 'fest',
                'venue': 'Main Campus',
                'max_participants': 500,
                'current_participants': 234,
                'is_public': True
            }
        ],
        'canteen_items': [
            {'name': 'Veg Burger', 'category': 'Snacks', 'price': 50, 'is_available': True, 'is_vegetarian': True},
            {'name': 'Masala Dosa', 'category': 'Main Course', 'price': 60, 'is_available': True, 'is_vegetarian': True},
            {'name': 'Cold Coffee', 'category': 'Beverages', 'price': 40, 'is_available': True, 'is_vegetarian': True}
        ]
    }
    
    total_docs = sum(len(docs) for docs in collections_data.values())
    created = 0
    
    for collection, documents in collections_data.items():
        print(f"\n📁 {collection}:")
        for doc in documents:
            if add_firestore_document(project_id, collection, doc):
                name = doc.get('name', doc.get('title', 'document'))
                print(f"  ✅ {name}")
                created += 1
            time.sleep(0.5)  # Rate limiting
    
    return created, total_docs

def main():
    print("=" * 70)
    print("🔥 FIREBASE DATABASE SETUP - SIMPLE METHOD")
    print("=" * 70)
    print("\nThis script will:")
    print("  1. Create test users in Firebase Authentication")
    print("  2. Create Firestore collections with sample data")
    print("\n⚠️  IMPORTANT:")
    print("  • Make sure Firestore is enabled (test mode)")
    print("  • Make sure Authentication is enabled (Email/Password)")
    print("=" * 70)
    
    proceed = input("\nContinue? (yes/no): ").strip().lower()
    if proceed != 'yes':
        print("Setup cancelled.")
        return
    
    project_id, api_key = get_firebase_config()
    
    if not project_id or not api_key:
        print("\n❌ Project ID and API Key are required!")
        return
    
    print("\n🚀 Starting setup...")
    created, total = setup_database(project_id, api_key)
    
    print("\n" + "=" * 70)
    print(f"✅ SETUP COMPLETE! Created {created}/{total} documents")
    print("=" * 70)
    print("\n📱 Test Credentials:")
    print("  Email: student@mpstme.edu.in")
    print("  Password: password123")
    print("\n⚠️  Next Steps:")
    print("  1. Go to Firebase Console > Firestore Database")
    print("  2. Verify collections were created")
    print("  3. If some collections are missing, add them manually")
    print("  4. Run your Android app and login!")

if __name__ == "__main__":
    main()
