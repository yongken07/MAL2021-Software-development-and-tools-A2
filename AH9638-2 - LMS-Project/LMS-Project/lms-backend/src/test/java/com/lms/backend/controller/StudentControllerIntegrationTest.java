package com.lms.backend.controller;

import com.lms.backend.model.Student;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetActiveStudents_Integration() {
        ResponseEntity<Student[]> response = restTemplate.getForEntity("/api/v1/students/active", Student[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().length);
        assertTrue(Arrays.stream(response.getBody()).anyMatch(student -> student.id().equals(10L)));
        assertTrue(Arrays.stream(response.getBody()).anyMatch(student -> student.id().equals(11L)));
    }

    @Test
    void testGetEnrollments_InvalidId() {
        ResponseEntity<Object[]> response = restTemplate.getForEntity("/api/v1/students/999/enrollments", Object[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }

    @Test
    void testGetEnrollments_ExistingStudentReturnsExpectedCourses() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/v1/students/10/enrollments", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Computer Science 101"));
        assertTrue(response.getBody().contains("Advanced Algorithms"));
        assertTrue(response.getBody().contains("Dr. Alice"));
    }
}
