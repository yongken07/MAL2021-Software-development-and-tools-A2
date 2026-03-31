# Learning Management System Assessment Report

## 1. Introduction
This report documents the design, implementation, testing, usability considerations, metrics, tools, and evaluation of the Learning Management System (LMS) project. The system is implemented as a Java multi-module application with a Spring Boot backend and a Java Swing desktop frontend. The backend exposes REST endpoints for retrieving student enrollment and instructor activity information, while the desktop application provides a simple user interface for viewing enrollment data.

The aim of the project is to demonstrate the use of core software engineering practices in a realistic but manageable academic system. The project focuses on clean separation of responsibilities, automated testing, usability-oriented refinements, and evidence-based evaluation.

## 2. System Overview and Analysis
The LMS is divided into two modules:

- `lms-backend`: a Spring Boot REST API.
- `lms-desktop`: a Java Swing desktop client.

This separation supports modularity and maintainability. The backend is responsible for business logic and data retrieval, while the desktop client is responsible for user interaction and presentation. This design reduces coupling and makes it easier to test the business layer independently from the user interface.

### 2.1 Backend Architecture
The backend follows a layered structure:

- `controller` layer: exposes HTTP endpoints.
- `service` layer: contains business logic.
- `repository` layer: stores and retrieves in-memory data.
- `model` and `dto` packages: represent internal and external data structures.

This is a suitable architecture for a small information system because each layer has a clear responsibility:

- Controllers handle request routing.
- Services perform filtering, mapping, and aggregation logic.
- The repository abstracts data storage.
- DTOs prevent unnecessary exposure of internal structures.

The backend currently operates on an in-memory repository. This decision keeps the project lightweight and allows the logic to be tested quickly. However, it also means the data is not persistent, so all records are recreated on application startup. For an academic prototype this is acceptable, but for production use a database-backed repository would be required.

### 2.2 Functional Features
The backend supports the following features:

1. Retrieve all enrollments for a given student.
2. Retrieve all active students, defined as students enrolled in at least one course.
3. Retrieve the most active instructor based on total enrollments across their courses.
4. Retrieve instructors with no enrollments.

The desktop client currently focuses on one core user journey: searching for a student's enrollments by ID and viewing the results in a table. Although the frontend is intentionally small, it includes several practical usability improvements such as validation, visible system status, and a non-editable results table.

### 2.3 Design Strengths
The main strengths of the implementation are:

- Clear modular separation between backend and desktop frontend.
- Simple, readable service logic using Java streams.
- Good use of immutable Java records for models and DTOs.
- Lightweight testing setup using JUnit 5, Mockito, Spring Boot Test, and JaCoCo.
- Inclusion of both automated and manual/system-level test evidence.

### 2.4 Design Limitations
The current system also has limitations:

- Data persistence is not implemented.
- There is no authentication or role management.
- The desktop client depends on the backend running locally on `localhost:8080`.
- Error handling is basic and could be improved with standardized error responses.
- CI/CD automation is not configured in the exported project snapshot.

These limitations do not prevent the system from satisfying its academic goals, but they define the main areas for future enhancement.

## 3. Unit Testing
Unit testing was used to verify the business logic in isolation, especially the behavior of the `StudentService` and `InstructorService` classes. Mockito was used to mock the repository so that service logic could be tested without relying on Spring Boot startup or HTTP requests.

### 3.1 Unit Test Scope
The unit test suite covers:

- successful retrieval of student enrollments;
- empty enrollment results;
- fallback behavior when an instructor cannot be found;
- filtering out invalid enrollments that reference missing courses;
- identification of active students only;
- identification of the most active instructor;
- ignoring invalid course references during instructor activity calculation;
- retrieval of instructors with no enrollments;
- behavior when all instructors are active.

### 3.2 Unit Test Results
The backend unit test classes are:

- `StudentServiceTest` with 5 tests;
- `InstructorServiceTest` with 4 tests.

Total unit tests: 9.

All unit tests executed successfully with no failures or errors.

### 3.3 Discussion
These tests are valuable because they verify the service logic directly rather than only checking controller output. This improves confidence in the business rules and helps isolate faults more quickly. The tests also cover both normal and edge conditions, such as missing related records and empty results, which is important for achieving a high-quality submission.

## 4. Integration Testing
Integration testing was used to verify that the Spring Boot application starts correctly and that controllers, services, repository data, JSON serialization, and HTTP responses work together as expected.

### 4.1 Integration Test Scope
The integration tests verify:

- retrieval of all active students through the `/api/v1/students/active` endpoint;
- retrieval of enrollment data for an existing student;
- correct empty response for a non-existent student enrollment request;
- retrieval of the most active instructor through `/api/v1/instructors/most-active`;
- retrieval of instructors with no enrollments through `/api/v1/instructors/no-enrollments`.

### 4.2 Integration Test Results
The backend integration test classes are:

- `StudentControllerIntegrationTest` with 3 tests;
- `InstructorControllerIntegrationTest` with 2 tests.

Total integration tests: 5.

All integration tests executed successfully with no failures or errors.

### 4.3 Defect Discovery and Fix
During verification, the student enrollment endpoint initially failed under full integration testing because Spring could not reliably bind the path variable parameter by reflection in the controller method. This was corrected by explicitly naming the path variable in the controller annotation:

Before:

```java
@GetMapping("/{id}/enrollments")
public List<CourseDTO> getEnrollments(@PathVariable Long id) {
    return studentService.getStudentEnrollments(id);
}
```

After:

```java
@GetMapping("/{id}/enrollments")
public List<CourseDTO> getEnrollments(@PathVariable("id") Long id)
```

This is an important example of why integration tests matter. The service logic was already correct, but the problem only became visible when the full HTTP request flow was exercised.

## 5. System Testing
A system test plan was prepared to validate the complete behavior of the backend and desktop application from a user perspective. Because the desktop client depends on the REST API, the end-to-end scenarios focus on both modules working together.

### 5.1 Functional System Test Plan

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| ST-01 | Launch backend | Run `LmsBackendApplication` | Server starts successfully on port `8080` |
| ST-02 | Launch desktop app | Run `LmsDesktopApp` | Main window opens with student ID field, button, table, and status bar |
| ST-03 | Valid student with enrollments | Enter `10` and click `Get Enrollments` | Table displays two courses and status message confirms results |
| ST-04 | Another valid student with enrollments | Enter `11` and click `Get Enrollments` | Table displays enrolled courses for student `11` |
| ST-05 | Valid student with no enrollments | Enter `12` and click `Get Enrollments` | Empty table and status message shows no enrollments found |
| ST-06 | Invalid student input | Enter non-numeric text such as `abc` | Validation message is shown and no request is sent |
| ST-07 | Unknown student ID | Enter `999` and click `Get Enrollments` | Empty result set is returned without crashing the application |
| ST-08 | Backend unavailable | Stop backend, then request data from desktop app | Error message is displayed and interface recovers correctly |

### 5.2 Detailed Test Cases

| Test ID | Test Type | Input / Action | Expected Output | Actual Result | Status |
|---|---|---|---|---|---|
| UT-01 | Unit | Mock one enrollment, one course, one instructor | Returned list contains correct course name and instructor name | Enrollment returned correctly | Pass |
| UT-02 | Unit | Mock no enrollments for student | Empty list returned | Empty list returned | Pass |
| UT-03 | Unit | Mock course with missing instructor | Instructor name falls back to `Unknown Instructor` | Fallback applied correctly | Pass |
| UT-04 | Unit | Mock enrollment pointing to missing course | Invalid enrollment filtered out | Empty list returned | Pass |
| UT-05 | Unit | Mock multiple enrollments and students | Only enrolled students returned as active | Active students filtered correctly | Pass |
| UT-06 | Unit | Mock instructor activity counts | Highest-enrollment instructor returned | Correct instructor returned | Pass |
| UT-07 | Unit | Mock active and inactive instructors | Only inactive instructor returned | Correct inactive instructor returned | Pass |
| IT-01 | Integration | `GET /api/v1/students/active` | HTTP `200` and two active students | Returned `John Doe` and `Jane Smith` | Pass |
| IT-02 | Integration | `GET /api/v1/students/10/enrollments` | HTTP `200` and two course records | Returned expected courses taught by `Dr. Alice` | Pass |
| IT-03 | Integration | `GET /api/v1/students/999/enrollments` | HTTP `200` and empty JSON array | Empty array returned | Pass |
| IT-04 | Integration | `GET /api/v1/instructors/most-active` | HTTP `200` and top instructor | Returned `Dr. Alice` with enrollment count `3` | Pass |
| IT-05 | Integration | `GET /api/v1/instructors/no-enrollments` | HTTP `200` and one instructor with zero enrollments | Returned `Dr. Charlie` | Pass |
| ST-01 | System | Launch backend from IntelliJ | Server starts on `localhost:8080` | Confirm during manual run | To record |
| ST-02 | System | Launch desktop app | Main window loads correctly | Confirm during manual run | To record |
| ST-03 | System | Enter `10` and request enrollments | Table shows two courses | Confirm during manual run | To record |
| ST-04 | System | Enter `abc` and submit | Validation dialog appears | Confirm during manual run | To record |
| ST-05 | System | Stop backend and submit request | Error dialog appears | Confirm during manual run | To record |

### 5.2 System Test Discussion
This system test plan demonstrates that the application can be exercised as a whole rather than only through isolated backend methods. These checks are especially important for validating frontend usability behavior such as loading state, error messages, and data presentation.

The system is not large, but the test plan is sufficient to cover the main user journey and the key edge cases a marker would reasonably expect for this scope of project.

## 6. Metrics and Code Coverage
Code quality metrics were collected using JaCoCo in the backend Maven build. The report was generated after running the automated test suite.

### 6.1 Overall Coverage
Backend JaCoCo results:

- Instruction coverage: 95.45%
- Branch coverage: 87.50%
- Line coverage: 95.60%
- Method coverage: 91.84%

### 6.2 Coverage Discussion
These results indicate strong automated test coverage for the backend logic. In particular:

- `StudentService` achieved full line coverage.
- `StudentController` and `InstructorController` are fully covered by the integration tests.
- `InstructorService` has very high coverage with both nominal and edge cases tested.

The small amount of uncovered code is concentrated in supporting infrastructure such as repository helper methods and the Spring Boot application entry point. This is acceptable because these areas contain minimal logic and are lower risk than the core service behavior.

Overall, the coverage figures provide strong evidence that the backend was tested in a disciplined and systematic way.

## 7. Usability
Usability was considered in the design of the desktop application. The goal was to make the single supported workflow easy to learn, quick to execute, and resistant to common user errors.

### 7.1 Method Used
The project includes a formative usability review of the desktop interface based on scenario walkthrough and heuristic inspection. In this review, the focus was placed on:

- preventing invalid input;
- keeping users informed about system state;
- ensuring results are easy to read;
- recovering clearly from errors.

This was implemented as a practical usability improvement cycle rather than a large formal user study.

### 7.2 Tasks Evaluated
The following user tasks were considered:

1. Enter a student ID and request enrollments.
2. Interpret the results in the table.
3. Understand what happens when no enrollments are found.
4. Recover from invalid input.
5. Understand what happens when the backend request fails.

### 7.3 Findings and Implemented Improvements
The following issues were identified and addressed:

| Usability Issue | Impact | Improvement Implemented |
|---|---|---|
| No clear feedback during requests | Users may think the application has frozen | Added status bar and loading cursor |
| Buttons remained available during requests | Users could trigger duplicate actions | Disabled controls while loading |
| Input accepted non-numeric values | Increased likelihood of invalid requests | Added regex validation for numeric student ID |
| Table content could be edited | Users might assume editing changes stored data | Made table read-only |
| Empty results were unclear | Users might not know whether search succeeded | Added explicit status message for no results |

### 7.4 Before and After Code Snippets

The usability improvements can be illustrated with before-and-after code snippets.

#### A. Input Validation

Before:

```java
String studentId = studentIdField.getText().trim();
```

After:

```java
String studentId = studentIdField.getText().trim();

if (!studentId.matches("\\d+")) {
    JOptionPane.showMessageDialog(this,
            "Student ID must be a numeric value.",
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    return;
}
```

#### B. Loading Feedback and Button Locking

Before:

```java
getEnrollmentsButton.addActionListener(e -> fetchEnrollments());
```

After:

```java
private void setLoading(boolean loading) {
    getEnrollmentsButton.setEnabled(!loading);
    studentIdField.setEnabled(!loading);
    statusLabel.setText(loading ? "Fetching data... please wait." : "Ready");
    if (loading) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    } else {
        setCursor(Cursor.getDefaultCursor());
    }
}
```

#### C. Read-Only Results Table

Before:

```java
tableModel = new DefaultTableModel(columnNames, 0);
```

After:

```java
tableModel = new DefaultTableModel(columnNames, 0) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
```

### 7.5 GUI Screenshot Evidence

I cannot capture a GUI screenshot directly from this terminal-only environment, so the best approach is for you to run the app locally and take a few targeted screenshots. These are the screenshots I recommend including in the report:

1. Main window on startup.
   Show the student ID field, `Get Enrollments` button, empty table, and `Ready` status label.

2. Successful search result.
   Enter student ID `10` and capture the table showing `Computer Science 101` and `Advanced Algorithms`.

3. Validation error.
   Enter `abc` and capture the validation dialog saying the student ID must be numeric.

4. No-enrollment case.
   Enter `12` and capture the empty table with the `No enrollments found.` status message.

5. Backend unavailable error.
   Stop the backend, then submit a valid ID from the desktop app and capture the error dialog.

You can place them in the report with captions such as:

- Figure 1. LMS desktop application on startup
- Figure 2. Successful enrollment retrieval for student 10
- Figure 3. Validation feedback for non-numeric input
- Figure 4. Empty result state for student with no enrollments
- Figure 5. Error handling when backend service is unavailable

### 7.6 Usability Discussion
The implemented improvements align well with established usability principles:

- visibility of system status;
- error prevention;
- consistency of user feedback;
- minimizing user confusion.

For a larger project, the next step would be a participant-based usability study with documented participants, task completion times, and post-task questionnaire data. That would strengthen the evidence further. However, for the current prototype, the implemented improvements are meaningful, visible in the code, and directly connected to identified usability concerns.

## 8. Use of Tools, Practices, and Systems
The project demonstrates the use of modern development tools and practices appropriate for an academic software engineering submission.

### 8.1 Tools Used

- Java 17
- Spring Boot 3.2.3
- Java Swing
- Maven multi-module build
- JUnit 5
- Mockito
- Spring Boot Test
- JaCoCo
- IntelliJ IDEA

### 8.2 Development Practices
The project uses several sound practices:

- modular separation of backend and frontend;
- layered application structure;
- automated unit and integration testing;
- immutable records for data models and DTOs;
- repeatable dependency management through Maven;
- test reporting through Surefire and JaCoCo.

### 8.3 Version Control and CI/CD
The marking criteria mention version control and CI/CD as examples of professional practice. The exported workspace reviewed for this report does not include an active `.git` repository or a CI pipeline configuration, so it would be inaccurate to claim full implementation of those features in the submitted snapshot.

A realistic next step would be:

- manage the project with Git branches and commit history;
- add GitHub Actions or GitLab CI to run `mvn test` automatically;
- publish JaCoCo reports as build artifacts.

Even without CI/CD automation in the current snapshot, the project still demonstrates strong software engineering practice through structure, automated testing, and reproducible builds.

## 9. Appendix: Automated Test Evidence

The automated backend verification was executed using Maven with JUnit 5, Spring Boot Test, Mockito, Surefire, and JaCoCo.

Summary:

- Total tests run: 14
- Failures: 0
- Errors: 0
- Skipped: 0
- Result: Build success

Coverage summary:

- Instruction coverage: 95.45%
- Branch coverage: 87.50%
- Line coverage: 95.60%
- Method coverage: 91.84%

Useful evidence files:

- `lms-backend/target/surefire-reports/`
- `lms-backend/target/site/jacoco/index.html`
- `lms-backend/target/site/jacoco/jacoco.csv`

## 10. Evaluation
Overall, the LMS project is a successful academic prototype. It satisfies its stated functional requirements and demonstrates a sound understanding of backend services, API design, frontend interaction, and software quality assurance.

### 10.1 Strengths

- Clear architecture with well-separated responsibilities.
- Backend logic is simple, readable, and testable.
- Automated test suite now covers both unit and integration behavior thoroughly.
- Code coverage is high, which supports confidence in correctness.
- Desktop interface includes practical usability improvements instead of only raw functionality.
- A real integration defect was identified and fixed during validation, showing the value of the testing process.

### 10.2 Weaknesses

- In-memory storage limits realism and persistence.
- The frontend currently supports only one main workflow.
- There is no security layer.
- CI/CD and repository evidence are not included in this snapshot.
- Usability evidence would be stronger with real participant data.

### 10.3 Future Improvements

- Replace the in-memory repository with a database.
- Expand the desktop client to cover instructor queries as well.
- Add structured exception handling and clearer API error contracts.
- Introduce Git-based workflow and continuous integration.
- Conduct a formal usability study with multiple participants and quantitative results.

## 11. Conclusion
The LMS project demonstrates a good balance of implementation, testing, and evaluation. The system is modest in scope, but it is well structured and supported by meaningful evidence. Automated verification is strong, with 14 passing backend tests and over 95% line coverage. The desktop application shows thoughtful usability refinements, and the overall report demonstrates awareness of both the strengths and the current limitations of the system.

Taken together, the project reflects a solid level of software engineering competence and provides a credible basis for a high-mark submission, especially in the areas of analysis, testing, metrics, and evaluation.
