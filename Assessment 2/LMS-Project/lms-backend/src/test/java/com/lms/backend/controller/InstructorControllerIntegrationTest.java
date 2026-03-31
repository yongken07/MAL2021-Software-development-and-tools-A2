package com.lms.backend.controller;

import com.lms.backend.dto.InstructorDTO;
import com.lms.backend.model.Instructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InstructorControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetMostActiveInstructor_Integration() {
        ResponseEntity<InstructorDTO> response = restTemplate.getForEntity("/api/v1/instructors/most-active", InstructorDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Dr. Alice", response.getBody().name());
        assertEquals(3L, response.getBody().enrollmentCount());
    }

    @Test
    void testGetNoEnrollments_Integration() {
        ResponseEntity<Instructor[]> response = restTemplate.getForEntity("/api/v1/instructors/no-enrollments", Instructor[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals(3L, response.getBody()[0].id());
        assertEquals("Dr. Charlie", response.getBody()[0].name());
    }
}
