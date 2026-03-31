# Learning Management System (LMS) - Project Documentation

## 1. Project Overview
This project is a simplified Learning Management System consisting of a Spring Boot REST API (Backend) and a Java Swing Desktop Application (Frontend).

## 2. Project Structure
```text
LMS-Project/
├── lms-backend/                 # Spring Boot API
│   ├── src/main/java/           # Java sources
│   ├── src/test/java/           # Unit & Integration tests
│   └── pom.xml                  # Maven configuration
├── lms-desktop/                 # Swing GUI Application
│   ├── src/main/java/           # Java sources
│   └── pom.xml                  # Maven configuration
└── pom.xml                      # Parent Maven configuration
```

## 3. Part A: Backend (Spring Boot)
The backend manages Students, Instructors, Courses, and Enrollments in memory.

### Features (Endpoints):
- `GET /api/v1/students/{id}/enrollments`: Courses for a student.
- `GET /api/v1/students/active`: Students in >=1 course.
- `GET /api/v1/instructors/most-active`: Highest total enrollments.
- `GET /api/v1/instructors/no-enrollments`: Zero student enrollments.

### How to Run (Using IntelliJ GUI - No Maven Installation Needed):
IntelliJ has built-in Maven, so you don't need `mvn` on your terminal.

**Option A: The Run Icon (Easiest)**
- **Backend**: Open `lms-backend/src/main/java/com/lms/backend/LmsBackendApplication.java`. Click the green play icon `▶` next to `public class...`.
- **Desktop**: Open `lms-desktop/src/main/java/com/lms/desktop/LmsDesktopApp.java`. Click the green play icon `▶`.

**Option B: The Maven Tool Window**
1. On the right side of IntelliJ, click the **"Maven"** tab.
2. Expand **"lms-backend"** > **"Plugins"** > **"spring-boot"** > **"spring-boot:run"**. Double-click it.
3. Expand **"lms-desktop"** > **"Plugins"** > **"exec"** > **"exec:java"**. Double-click it.

## 5. Usability Testing Simulation
### Identified Issues & Improvements:
1. **Issue**: Lack of request feedback.
   **Improvement**: Added a status bar and disabled buttons during network calls.
2. **Issue**: Numeric input was not enforced.
   **Improvement**: Added Regex validation for the Student ID field.
3. **Issue**: Table readability.
   **Improvement**: Set the table to read-only and ensured automatic scrolling.

## 6. Example API Response (F1)
`GET /api/v1/students/10/enrollments`
```json
[
  {
    "id": 101,
    "name": "Computer Science 101",
    "instructorName": "Dr. Alice"
  },
  {
    "id": 102,
    "name": "Advanced Algorithms",
    "instructorName": "Dr. Alice"
  }
]
```

## 7. Importing into IntelliJ IDEA
This project is configured as a **Maven Multi-Module Project**.

1. Open **IntelliJ IDEA**.
2. Click **Open** or **File > Open**.
3. Select the root directory `LMS-Project` (the one containing this README and the root `pom.xml`).
4. IntelliJ will detect the Maven configuration and automatically import both `lms-backend` and `lms-desktop` as modules.
5. If prompted, click **"Load Maven Project"**.

### Running from IntelliJ:
- **Backend**: Find `LmsBackendApplication` in `lms-backend/src/main/java` and click the green "Run" icon.
- **Desktop**: Find `LmsDesktopApp` in `lms-desktop/src/main/java` and click the green "Run" icon.
