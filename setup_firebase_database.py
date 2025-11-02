#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Firebase Firestore Database Setup Script
This script will create all necessary collections with sample data
"""

import firebase_admin
from firebase_admin import credentials, firestore, auth
import datetime
import json
import os

def setup_firebase():
    """Initialize Firebase Admin SDK"""
    # You need to download the service account key from Firebase Console
    # Firebase Console > Project Settings > Service Accounts > Generate New Private Key
    
    service_account_path = input("Enter path to your Firebase service account JSON file: ").strip()
    
    if not os.path.exists(service_account_path):
        print(f"❌ Error: File not found at {service_account_path}")
        print("\n📋 To get this file:")
        print("1. Go to Firebase Console: https://console.firebase.google.com")
        print("2. Select your project")
        print("3. Go to Project Settings (gear icon) > Service Accounts")
        print("4. Click 'Generate New Private Key'")
        print("5. Save the JSON file and provide its path")
        return None
    
    try:
        cred = credentials.Certificate(service_account_path)
        firebase_admin.initialize_app(cred)
        print("✅ Firebase initialized successfully!")
        return firestore.client()
    except Exception as e:
        print(f"❌ Error initializing Firebase: {e}")
        return None

def create_users(db):
    """Create users collection with sample data"""
    print("\n📝 Creating users collection...")
    
    users = [
        {
            'email': 'student@mpstme.edu.in',
            'username': 'john_doe',
            'full_name': 'John Doe',
            'role': 'student',
            'college_id': '1',
            'student_id': 'STU2024001',
            'course': 'B TECH CE',
            'branch': 'Computer Engineering',
            'semester': '5',
            'academic_year': '2024-25',
            'batch': '2022-26',
            'cgpa': 8.5,
            'phone_number': '+91 9876543210',
            'department': 'Computer Engineering',
            'bio': 'Passionate about coding and AI',
            'skills': ['Java', 'Python', 'React', 'Firebase'],
            'interests': ['AI', 'Mobile Development', 'Cloud Computing'],
            'is_active': True,
            'is_verified': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'email': 'faculty@mpstme.edu.in',
            'username': 'prof_smith',
            'full_name': 'Dr. Sarah Smith',
            'role': 'faculty',
            'college_id': '1',
            'employee_id': 'FAC001',
            'designation': 'Assistant Professor',
            'department': 'Computer Engineering',
            'specialization': 'Machine Learning',
            'experience_years': 8,
            'research_interests': ['Deep Learning', 'NLP', 'Computer Vision'],
            'phone_number': '+91 9876543211',
            'bio': 'AI researcher and educator',
            'is_active': True,
            'is_verified': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'email': 'admin@mpstme.edu.in',
            'username': 'admin',
            'full_name': 'Admin User',
            'role': 'admin',
            'college_id': '1',
            'department': 'Administration',
            'is_active': True,
            'is_verified': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for user in users:
        doc_ref = db.collection('users').add(user)
        print(f"  ✅ Created user: {user['full_name']} ({user['email']})")
    
    # Also create Firebase Auth users
    try:
        auth.create_user(email='student@mpstme.edu.in', password='password123')
        print(f"  ✅ Created auth user: student@mpstme.edu.in")
    except:
        print(f"  ℹ️  Auth user already exists: student@mpstme.edu.in")

def create_colleges(db):
    """Create colleges collection"""
    print("\n📝 Creating colleges collection...")
    
    colleges = [
        {
            'name': 'MPSTME Mumbai',
            'code': 'MPSTME',
            'type': 'Engineering College',
            'address': 'SVKM Campus, Vile Parle West',
            'city': 'Mumbai',
            'state': 'Maharashtra',
            'pincode': '400056',
            'contact_email': 'info@mpstme.edu.in',
            'contact_phone': '+91-22-42332000',
            'website_url': 'https://mpstme.nmims.edu',
            'established_year': 2006,
            'is_active': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for college in colleges:
        doc_ref = db.collection('colleges').add(college)
        print(f"  ✅ Created college: {college['name']}")

def create_clubs(db):
    """Create clubs collection"""
    print("\n📝 Creating clubs collection...")
    
    clubs = [
        {
            'name': 'Coding Club',
            'description': 'Learn programming and participate in hackathons',
            'category': 'Technical',
            'college_id': '1',
            'max_members': 100,
            'member_count': 45,
            'is_active': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'name': 'Robotics Club',
            'description': 'Build robots and compete in competitions',
            'category': 'Technical',
            'college_id': '1',
            'max_members': 50,
            'member_count': 32,
            'is_active': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'name': 'Music Club',
            'description': 'Express yourself through music',
            'category': 'Cultural',
            'college_id': '1',
            'max_members': 80,
            'member_count': 56,
            'is_active': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'name': 'Dance Club',
            'description': 'Learn various dance forms',
            'category': 'Cultural',
            'college_id': '1',
            'max_members': 60,
            'member_count': 48,
            'is_active': True,
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for club in clubs:
        doc_ref = db.collection('clubs').add(club)
        print(f"  ✅ Created club: {club['name']}")

def create_events(db):
    """Create events collection"""
    print("\n📝 Creating events collection...")
    
    # Events in the future
    events = [
        {
            'title': 'Tech Workshop: Firebase for Mobile Apps',
            'description': 'Learn how to integrate Firebase in your Android apps',
            'event_type': 'workshop',
            'start_time': firestore.SERVER_TIMESTAMP,
            'end_time': firestore.SERVER_TIMESTAMP,
            'venue': 'Room 301, Building A',
            'max_participants': 50,
            'current_participants': 12,
            'registration_deadline': firestore.SERVER_TIMESTAMP,
            'is_public': True,
            'college_id': '1',
            'created_at': firestore.SERVER_TIMESTAMP
        },
        {
            'title': 'Annual Tech Fest 2025',
            'description': 'College-wide technology festival with competitions, workshops, and exhibitions',
            'event_type': 'fest',
            'start_time': firestore.SERVER_TIMESTAMP,
            'end_time': firestore.SERVER_TIMESTAMP,
            'venue': 'Main Campus',
            'max_participants': 500,
            'current_participants': 234,
            'registration_deadline': firestore.SERVER_TIMESTAMP,
            'is_public': True,
            'college_id': '1',
            'created_at': firestore.SERVER_TIMESTAMP
        },
        {
            'title': 'Coding Competition',
            'description': '24-hour coding marathon with exciting prizes',
            'event_type': 'competition',
            'start_time': firestore.SERVER_TIMESTAMP,
            'end_time': firestore.SERVER_TIMESTAMP,
            'venue': 'Computer Lab',
            'max_participants': 100,
            'current_participants': 78,
            'registration_deadline': firestore.SERVER_TIMESTAMP,
            'is_public': True,
            'college_id': '1',
            'created_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for event in events:
        doc_ref = db.collection('events').add(event)
        print(f"  ✅ Created event: {event['title']}")

def create_bookings(db):
    """Create bookings collection"""
    print("\n📝 Creating bookings collection...")
    
    bookings = [
        {
            'title': 'Data Structures Lecture',
            'subject': 'Computer Science',
            'description': 'Advanced topics in Data Structures and Algorithms',
            'booking_type': 'lecture',
            'booking_date': firestore.SERVER_TIMESTAMP,
            'start_time': '10:00 AM',
            'end_time': '11:30 AM',
            'room_id': 'Room 101',
            'status': 'approved',
            'created_by_type': 'faculty',
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        },
        {
            'title': 'Machine Learning Workshop',
            'subject': 'AI/ML',
            'description': 'Hands-on workshop on ML basics',
            'booking_type': 'workshop',
            'booking_date': firestore.SERVER_TIMESTAMP,
            'start_time': '02:00 PM',
            'end_time': '05:00 PM',
            'room_id': 'Lab 201',
            'status': 'pending',
            'created_by_type': 'organization',
            'created_at': firestore.SERVER_TIMESTAMP,
            'updated_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for booking in bookings:
        doc_ref = db.collection('bookings').add(booking)
        print(f"  ✅ Created booking: {booking['title']}")

def create_notifications(db):
    """Create notifications collection"""
    print("\n📝 Creating notifications collection...")
    
    notifications = [
        {
            'title': 'Welcome to Campus Buddy!',
            'message': 'Your account has been created successfully. Explore all features!',
            'type': 'general',
            'is_read': False,
            'priority': 'high',
            'created_at': firestore.SERVER_TIMESTAMP
        },
        {
            'title': 'New Event: Tech Fest 2025',
            'message': 'Register now for the biggest tech event of the year!',
            'type': 'event',
            'is_read': False,
            'priority': 'medium',
            'action_url': '/events/tech-fest-2025',
            'created_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for notification in notifications:
        doc_ref = db.collection('notifications').add(notification)
        print(f"  ✅ Created notification: {notification['title']}")

def create_canteen_items(db):
    """Create canteen_items collection"""
    print("\n📝 Creating canteen_items collection...")
    
    items = [
        {
            'name': 'Veg Burger',
            'description': 'Delicious vegetarian burger with fresh veggies',
            'category': 'Snacks',
            'price': 50,
            'is_available': True,
            'is_vegetarian': True,
            'image_url': '',
            'created_at': firestore.SERVER_TIMESTAMP
        },
        {
            'name': 'Masala Dosa',
            'description': 'South Indian crispy dosa with potato filling',
            'category': 'Main Course',
            'price': 60,
            'is_available': True,
            'is_vegetarian': True,
            'image_url': '',
            'created_at': firestore.SERVER_TIMESTAMP
        },
        {
            'name': 'Cold Coffee',
            'description': 'Refreshing cold coffee',
            'category': 'Beverages',
            'price': 40,
            'is_available': True,
            'is_vegetarian': True,
            'image_url': '',
            'created_at': firestore.SERVER_TIMESTAMP
        }
    ]
    
    for item in items:
        doc_ref = db.collection('canteen_items').add(item)
        print(f"  ✅ Created canteen item: {item['name']}")

def main():
    print("=" * 60)
    print("🔥 FIREBASE FIRESTORE DATABASE SETUP")
    print("=" * 60)
    print("\nThis script will create all necessary collections with sample data.")
    print("\n⚠️  PREREQUISITES:")
    print("1. You must have a Firebase project created")
    print("2. Firestore must be enabled in your Firebase project")
    print("3. You need the Firebase Admin SDK service account JSON file")
    print("\n" + "=" * 60)
    
    proceed = input("\nDo you want to continue? (yes/no): ").strip().lower()
    if proceed != 'yes':
        print("Setup cancelled.")
        return
    
    # Initialize Firebase
    db = setup_firebase()
    if not db:
        return
    
    try:
        # Create all collections
        create_users(db)
        create_colleges(db)
        create_clubs(db)
        create_events(db)
        create_bookings(db)
        create_notifications(db)
        create_canteen_items(db)
        
        print("\n" + "=" * 60)
        print("✅ DATABASE SETUP COMPLETE!")
        print("=" * 60)
        print("\n📊 Created Collections:")
        print("  • users (3 documents)")
        print("  • colleges (1 document)")
        print("  • clubs (4 documents)")
        print("  • events (3 documents)")
        print("  • bookings (2 documents)")
        print("  • notifications (2 documents)")
        print("  • canteen_items (3 documents)")
        print("\n🎉 Your Firebase database is ready to use!")
        print("\n📱 Test Credentials:")
        print("  Email: student@mpstme.edu.in")
        print("  Password: password123")
        
    except Exception as e:
        print(f"\n❌ Error during setup: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    main()
