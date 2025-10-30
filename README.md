# ACPulse Backend

> **Feel the Pulse of AUCA** — A smarter, connected, and dynamic campus experience.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Overview

**ACPulse** is a comprehensive campus digital management platform designed to revolutionize campus operations at the Adventist University of Central Africa (AUCA). The system provides real-time insights into lecturer availability, room utilization, personnel status, and campus-wide notifications, fostering a more efficient, transparent, and connected academic environment.

This repository contains the **Phase 1 backend system**, which implements core APIs, authentication infrastructure, hierarchical location management, and real-time lecturer-room mapping capabilities.

## Key Features

### 🔐 Authentication & Authorization
- Multi-role user system supporting **STUDENT**, **LECTURER**, **STAFF**, and **ADMIN** roles
- Secure JWT-based authentication with refresh token support
- Administrative verification workflow for new user approvals
- Role-based access control (RBAC) for endpoint protection

### 🏫 Room & Lecturer Management
- Real-time room occupancy tracking and management
- Lecturer availability status monitoring
- Dynamic room booking with extension and release capabilities
- Conflict resolution for room scheduling

### 📍 Hierarchical Location System
- Five-tier geographic hierarchy: Province → District → Sector → Cell → Village
- Recursive relationship modeling for flexible location queries
- Campus-wide location mapping and search functionality

### 📬 Notification System
- Automated notifications for status changes and approvals
- Read/unread tracking with persistence
- Multi-channel notification delivery support

### 🛠️ Administrative Tools
- Comprehensive verification request management
- Office status and assignment controls
- User management and role assignment interfaces
- System-wide configuration and monitoring

## Architecture

### Project Structure

```
acpulse-backend/
├── src/
│   ├── main/
│   │   ├── java/com/acpulse/
│   │   │   ├── config/              # Security & configuration beans
│   │   │   ├── controller/          # REST API endpoints
│   │   │   ├── dto/                 # Data transfer objects
│   │   │   ├── model/               # JPA entity models
│   │   │   ├── repository/          # Spring Data repositories
│   │   │   ├── security/            # JWT & authentication utilities
│   │   │   ├── service/             # Business logic layer
│   │   │   └── exception/           # Custom exception handlers
│   │   └── resources/
│   │       ├── application.properties   # Application configuration
│   │       └── data.sql                 # Database seed scripts
│   └── test/
│       └── postman-tests/           # API test collection
├── pom.xml                          # Maven dependencies
└── README.md
```

### Technology Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Database** | PostgreSQL / H2 |
| **Security** | Spring Security + JWT (to be added) |
| **Email** | JavaMailSender (to be added) |
| **Testing** | Postman |
| **Build Tool** | Maven 3.8+ |

## Getting Started

### Prerequisites

Ensure you have the following installed:
- **Java Development Kit (JDK)** 21 or higher
- **Apache Maven** 3.8+
- **PostgreSQL** 12+ or H2 Database
- **Postman** (for API testing)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/acpulse-backend.git
   cd acpulse-backend
   ```

2. **Configure database connection**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/acpulse
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The server will start at `http://localhost:8080`

### Initial Data

The application automatically seeds essential data on startup:

| Entity | Sample Data |
|--------|-------------|
| **Roles** | STUDENT, LECTURER, STAFF, ADMIN |
| **Admin Account** | Email: `admin@auca.ac.rw` <br/> Password: `Admin123!` |
| **Locations** | Kigali → Gasabo → Kacyiru |
| **Rooms** | A-101, A-102 |
| **Offices** | Admin-101 |
| **Semester** | Fall 2024/2025 |

## API Testing

Comprehensive API tests are available in the `src/test/postman-tests/` directory.

**Test Collection Includes:**
- ✅ User authentication (registration, login, token validation)
- ✅ Lecturer room operations (occupy, extend, release)
- ✅ Admin verification workflows
- ✅ Staff office management
- ✅ Notification endpoints
- ✅ Location hierarchy queries

Each test includes request templates, authentication headers, expected responses, and automated validation scripts.

📂 **Access the full test suite:** `src/test/postman-tests/Individual_Postman_Tests.md`

## API Documentation

API endpoints follow RESTful conventions:

- **Authentication:** `/api/auth/*`
- **Users:** `/api/users/*`
- **Rooms:** `/api/rooms/*`
- **Lecturers:** `/api/lecturers/*`
- **Notifications:** `/api/notifications/*`
- **Locations:** `/api/locations/*`

Detailed endpoint documentation with request/response examples is available in the Postman collection.

## Roadmap

### Future Enhancements

- [ ] **WebSocket Integration** — Real-time lecturer status updates
- [ ] **Frontend Development** — React or Flutter dashboard interface
- [ ] **Student Booking System** — Self-service room reservations
- [ ] **Analytics Dashboard** — AI-powered insights on room utilization and attendance patterns
- [ ] **Mobile Application** — Native iOS and Android apps
- [ ] **Integration APIs** — Connect with existing campus systems (LMS, attendance)

## Contributing

This is an academic project. For collaboration or extension proposals, please contact the author directly.

## Author

**Joseph Manizabayo**  
Software Engineering Student  
Adventist University of Central Africa (AUCA)

📧 Email: [josephmanizabayo7@gmail.com](mailto:josephmanizabayo7@gmail.com)  
🎓 Institution: AUCA  
📅 Year: 2025

## License

This project is licensed under the AUCA WebTech class License.

**Attribution Required:** Any reuse, modification, or extension of this codebase must credit the original author.

---

<div align="center">

**© 2025 Joseph Manizabayo — All Rights Reserved**

*Original concept and implementation by Joseph Manizabayo*

</div>
