# Faculty-Student Connection System

## Overview
The system connects faculty to their students automatically based on timetable data, allowing faculty to see which students they teach across multiple classes.

## How It Works

### 1. Faculty Registration with Google Auth
- **FacultyRegisterWithGoogleActivity.java**
- Faculty sign in using their institutional Google account
- Complete profile with:
  - Employee ID
  - Department (e.g., Computer Science)
  - Specialization (e.g., AI, Database, Web Development)
- Data stored in Firebase Firestore 'faculty' collection

### 2. Course Management
- **FacultyCourseManagementActivity.java** (replaces Assignments)
- Faculty can add courses they teach:
  - Course Name (e.g., Computer Science Engineering)
  - Subject (e.g., Data Structures, Database Management)
  - Semester (e.g., 3, 5, 6)
  - Batch (e.g., 2024-25)
- Courses stored in faculty document as an array

### 3. Timetable-Based Connection
- **TimetableActivity.java** manages class schedules
- Each timetable entry contains:
  - faculty_id: Links to faculty member
  - student_ids: Array of student IDs attending this class
  - course: Course name
  - subject: Subject name
  - day, start_time, end_time, room

### 4. Student Discovery
- **FacultyStudentsActivity.java**
- Automatically finds students by:
  1. Querying timetable for classes taught by this faculty
  2. Extracting all unique student IDs from those classes
  3. Fetching student details from 'users' collection
  4. Displaying with course and semester info
- Filter students by course
- Shows total student count
- Click on student to see full details

## Data Structure

### Faculty Document (Firestore)
`
faculty/{employeeId}
{
  name: "Dr. John Smith"
  email: "john.smith@university.edu"
  employee_id: "FAC001"
  department: "Computer Science"
  specialization: "AI, Machine Learning"
  role: "faculty"
  courses: [
    "CSE - Data Structures (Sem 3)",
    "CSE - Database Management (Sem 5)"
  ]
  created_at: timestamp
}
`

### Timetable Document (Firestore)
`
timetable/{id}
{
  faculty_id: "FAC001"
  subject: "Data Structures"
  course: "Computer Science Engineering"
  semester: "3"
  day: "Monday"
  start_time: "09:00"
  end_time: "10:30"
  room: "CS-101"
  student_ids: ["STU001", "STU002", "STU003", ...]
}
`

### Student Document (Firestore)
`
users/{studentId}
{
  name: "Rahul Sharma"
  email: "rahul@student.edu"
  course: "Computer Science Engineering"
  semester: "3"
  role: "student"
}
`

## Features Implemented

 Faculty registration with Google Auth
 Course management for faculty
 Automatic student discovery from timetable
 Filter students by course
 View student details
 Real-time data from Firebase
 Multiple classes support

## Assignments Feature
 Removed as per user request - replaced with Course Management

## Benefits

1. **Automatic Connection**: No manual linking needed
2. **Multiple Classes**: Faculty can teach multiple subjects to different student groups
3. **Flexible**: Easy to add/remove courses
4. **Scalable**: Works with any number of students/classes
5. **Real-time**: Updates automatically when timetable changes

## Usage Flow

1. Faculty registers using Google Auth
2. Faculty adds courses they teach via Course Management
3. Admin/Faculty creates timetable entries with student_ids
4. Faculty opens "My Students" to see all students
5. Filter by course to see specific class students
6. Click student to view details

## Files Modified/Created

**New Files:**
- FacultyRegisterWithGoogleActivity.java
- FacultyCourseManagementActivity.java

**Updated Files:**
- FacultyStudentsActivity.java (full implementation)
- FacultyDashboard.java (linked to course management)
- AndroidManifest.xml (added new activities)

**Removed Files:**
- AssignmentsActivity.java (as requested)
