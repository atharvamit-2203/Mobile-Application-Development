# Firebase Setup Guide

## Required Files

This project requires Firebase configuration. You need to:

1. Create a Firebase project at https://console.firebase.google.com/
2. Add an Android app with package name: `com.campusbuddy`
3. Download your `google-services.json` file
4. Place it in `app/google-services.json` (not tracked in git for security)

## Configuration Steps

1. **Firebase Authentication**
   - Enable Email/Password authentication
   - Go to Authentication > Sign-in method
   - Enable "Email/Password"

2. **Firestore Database**
   - Create Firestore database in test mode
   - Collections needed: users, clubs, club_events, canteen_orders, notifications

3. **Storage** (Optional)
   - Enable Firebase Storage for file uploads

## Template

A template file `google-services.json.example` is provided.
Copy it to `google-services.json` and replace placeholders with your actual Firebase credentials.

**Note:** Never commit your actual `google-services.json` file to version control!
