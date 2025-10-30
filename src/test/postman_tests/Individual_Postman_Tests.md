# ACPulse Backend — API Testing Guide!

**Comprehensive Postman Test Suite**  
*By Joseph Manizabayo ([josephmanizabayo7@gmail.com](mailto:josephmanizabayo7@gmail.com))*

---

## Table of Contents

- [Environment Setup](#environment-setup)
- [Test Categories](#test-categories)
- [Authentication Tests](#authentication-tests)
- [Room Management Tests](#room-management-tests)
- [Location Services Tests](#location-services-tests)
- [Lecturer Management Tests](#lecturer-management-tests)
- [Administrative Tests](#administrative-tests)
- [Staff Management Tests](#staff-management-tests)
- [Notification Tests](#notification-tests)
- [Test Summary](#test-summary)

---

## Environment Setup

### Collection Variables

Configure the following variables in your Postman environment:

| Variable | Value | Description |
|----------|-------|-------------|
| `base_url` | `http://localhost:8080` | API base URL |
| `admin_id` | `1` | Pre-seeded admin user ID |
| `student_id` | *Dynamic* | Set after student registration |
| `lecturer_id` | *Dynamic* | Set after lecturer registration |
| `room_id` | *Dynamic* | Set after room occupation |

### Initial Server Verification

**Test 1: Verify Server Status**

```http
GET {{base_url}}/api/auth/login
```

**Test Script:**
```javascript
pm.test("Server Running", () => {
  pm.response.to.have.status(400);
});
```

**Expected Result:** HTTP 400 (confirms backend is operational)

---

## Authentication Tests

### User Registration

#### Test 2: Register Student Account

```http
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Test Student",
  "email": "student1@test.com",
  "password": "Password123!",
  "roleType": "STUDENT",
  "identificationNumber": "STU2025001",
  "phoneNumber": "+250788123456",
  "department": "Computer Science",
  "locationId": 1
}
```

**Test Script:**
```javascript
pm.test("201 Created", () => {
  pm.response.to.have.status(201);
  pm.collectionVariables.set("student_id", pm.response.json().userId);
});
```

**Expected Result:** HTTP 201, student ID stored in collection variables

---

#### Test 3: Register Lecturer Account

```http
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Dr. Test Lecturer",
  "email": "lecturer1@test.com",
  "password": "Password123!",
  "roleType": "LECTURER",
  "identificationNumber": "LEC2025001",
  "phoneNumber": "+250788123456",
  "department": "Mathematics",
  "locationId": 1
}
```

**Test Script:**
```javascript
pm.test("201 Created", () => {
  pm.response.to.have.status(201);
  pm.collectionVariables.set("lecturer_id", pm.response.json().userId);
});
```

**Expected Result:** HTTP 201, lecturer ID stored in collection variables

---

### Validation Tests

#### Test 4: Invalid Registration — Missing Required Field

```json
{
  "email": "test@test.com",
  "password": "Password123!",
  "roleType": "STUDENT",
  "identificationNumber": "ID123"
}
```

**Expected Result:** HTTP 400 — "Name is required"

---

#### Test 5: Invalid Registration — Weak Password

```json
{
  "name": "Test",
  "email": "test@test.com",
  "password": "short",
  "roleType": "STUDENT",
  "identificationNumber": "ID123"
}
```

**Expected Result:** HTTP 400 — "Password must be at least 8 characters"

---

#### Test 6: Invalid Registration — Malformed Email

```json
{
  "name": "Test",
  "email": "invalid-email",
  "password": "Password123!",
  "roleType": "STUDENT",
  "identificationNumber": "ID123"
}
```

**Expected Result:** HTTP 400 — "Invalid email format"

---

### Login Tests

#### Test 7: Admin Login

```http
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "admin@auca.ac.rw",
  "password": "Admin123!"
}
```

**Expected Result:** HTTP 200, Token = null, Role = ADMIN

---

#### Test 8: Lecturer Login (Post-Approval)

```json
{
  "email": "lecturer1@test.com",
  "password": "Password123!"
}
```

**Expected Result:**
- HTTP 200 if account is approved
- HTTP 400 "Account pending" if verification is pending

---

#### Test 9: Invalid Login Attempt

```json
{
  "email": "invalid@test.com",
  "password": "wrong"
}
```

**Expected Result:** HTTP 400 — "Invalid credentials"

---

## Room Management Tests

#### Test 10: Search Room by Number

```http
GET {{base_url}}/api/rooms/search?roomNumber=A-101
```

**Expected Result:** HTTP 200, Room object returned

---

#### Test 11: Retrieve All Rooms

```http
GET {{base_url}}/api/rooms
```

**Expected Result:** HTTP 200, Array of 6 room objects

---

#### Test 18: Occupy Room

```http
POST {{base_url}}/api/lecturer/occupy-room
Content-Type: application/json
```

**Request Body:**
```json
{
  "roomNumber": "A-101",
  "endTime": "2025-10-30T16:00:00",
  "customMessage": "Math lecture in progress"
}
```

**Expected Result:** HTTP 200 — "Room occupied successfully"

---

#### Test 19: Extend Room Occupation

```http
PUT {{base_url}}/api/lecturer/extend-room/{{room_id}}?lecturerId={{lecturer_id}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "newEndTime": "2025-10-30T17:00:00"
}
```

**Expected Result:** HTTP 200 — "Room occupation extended"

---

#### Test 20: Release Room

```http
POST {{base_url}}/api/lecturer/release-room/{{room_id}}?lecturerId={{lecturer_id}}
```

**Expected Result:** HTTP 200 — "Room released successfully"

---

## Location Services Tests

#### Test 12: Get Locations by Type

```http
GET {{base_url}}/api/locations?type=PROVINCE
```

**Expected Result:** HTTP 200, Array of 5 provinces

---

#### Test 13: Get Location Children

```http
GET {{base_url}}/api/locations/1/children
```

**Expected Result:** HTTP 200, Array of districts under Kigali

---

#### Test 14: Get Location Hierarchy

```http
GET {{base_url}}/api/locations/hierarchy/1
```

**Expected Result:** HTTP 200, Object containing `current` location and `path` array

---

## Lecturer Management Tests

#### Test 15: Search Lecturers

```http
GET {{base_url}}/api/lecturers/search?query=Test
```

**Expected Result:** HTTP 200, Array of matching lecturers

---

#### Test 16: Get Lecturer Status

```http
GET {{base_url}}/api/lecturers/status?lecturerId={{lecturer_id}}
```

**Expected Result:** HTTP 200, Lecturer status object

---

#### Test 17: Update Lecturer Status

```http
PUT {{base_url}}/api/lecturers/status?lecturerId={{lecturer_id}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "AWAY",
  "customMessage": "In meeting"
}
```

**Expected Result:** HTTP 200 — "Status updated successfully"

---

## Administrative Tests

#### Test 21: Get Pending Verification Requests

```http
GET {{base_url}}/api/admin/verification-requests?status=PENDING
```

**Expected Result:** HTTP 200, Array of pending verification requests

---

#### Test 22: Approve User Verification

```http
POST {{base_url}}/api/admin/verification-requests/1/approve?adminId={{admin_id}}
```

**Expected Result:** HTTP 200 — "User approved successfully"

---

#### Test 23: Reject User Verification

```http
POST {{base_url}}/api/admin/verification-requests/2/reject?adminId={{admin_id}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "reason": "Invalid ID number"
}
```

**Expected Result:** HTTP 200 — "User rejected"

---

## Staff Management Tests

#### Test 24: Get Staff Office Information

```http
GET {{base_url}}/api/staff/office?staffUserId=3
```

**Expected Result:** HTTP 200, Office object

---

#### Test 25: Update Office Status

```http
PUT {{base_url}}/api/staff/office/status?staffUserId=3
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "OPEN"
}
```

**Expected Result:** HTTP 200 — "Office status updated successfully"

---

## Notification Tests

#### Test 26: Get User Notifications

```http
GET {{base_url}}/api/notifications?userId={{student_id}}
```

**Expected Result:** HTTP 200, Array of notification objects

---

#### Test 27: Get Unread Notification Count

```http
GET {{base_url}}/api/notifications/unread-count?userId={{student_id}}
```

**Expected Result:** HTTP 200, Object containing unread count

**Example Response:**
```json
{
  "count": 0
}
```

---

#### Test 28: Mark Notification as Read

```http
PUT {{base_url}}/api/notifications/1/read?userId={{student_id}}
```

**Expected Result:** HTTP 200 — "Notification marked as read"

---

## Test Summary

### Coverage Overview

| Category | Endpoints Tested | Test Count |
|----------|------------------|------------|
| **Authentication** | Register, Login, Validation | 9 |
| **Room Management** | Search, Retrieve, Occupy, Extend, Release | 5 |
| **Location Services** | Hierarchy, Type Filter, Children | 3 |
| **Lecturer Management** | Search, Status Operations | 3 |
| **Administrative** | Verification Requests, Approvals | 3 |
| **Staff Management** | Office Operations | 2 |
| **Notifications** | CRUD, Read Status | 3 |
| **Total** | — | **28 Tests** |

---

## Prerequisites

- **Backend Server:** ACPulse Backend running on `http://localhost:8080`
- **Database:** Initialized with seed data via `DataInitializer`
- **Postman:** Version 10.0 or higher recommended
- **Pre-seeded Data:**
    - Admin account: `admin@auca.ac.rw` / `Admin123!`
    - Sample rooms, locations, and offices

---

## Running the Tests

### Sequential Execution

For best results, execute tests in the order presented in this document:

1. Run **Authentication Tests** first to create test users
2. Execute **Administrative Tests** to approve newly registered users
3. Proceed with **Room Management**, **Lecturer**, and **Staff** tests
4. Validate **Notification** functionality last

### Automated Test Scripts

Each test includes validation scripts that:
- Verify HTTP status codes
- Extract and store dynamic IDs in collection variables
- Validate response structure and content

---

## Notes

- All tests assume the backend is running with seeded data
- Dynamic variables (`student_id`, `lecturer_id`, `room_id`) are automatically populated during test execution
- Some tests require prior test completion (e.g., login requires registration and approval)
- Timestamps in room occupation tests should be adjusted to current date/time for production testing

---

## Project Information

**Project:** ACPulse Backend — AUCA Campus Pulse  
**Author:** Joseph Manizabayo  
**Email:** [josephmanizabayo7@gmail.com](mailto:josephmanizabayo7@gmail.com)  
**Institution:** Adventist University of Central Africa (AUCA)  
**Year:** 2025

---

<div align="center">

**© 2025 Joseph Manizabayo — All Rights Reserved**

</div>