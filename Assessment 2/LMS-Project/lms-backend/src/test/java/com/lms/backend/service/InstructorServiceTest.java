package com.lms.backend.service;

import com.lms.backend.dto.InstructorDTO;
import com.lms.backend.model.Course;
import com.lms.backend.model.Enrollment;
import com.lms.backend.model.Instructor;
import com.lms.backend.repository.LMSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class InstructorServiceTest {

    @Mock
    private LMSRepository repository;

    @InjectMocks
    private InstructorService instructorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetMostActiveInstructor_ReturnsInstructorWithHighestEnrollmentCount() {
        when(repository.findAllEnrollments()).thenReturn(List.of(
                new Enrollment(10L, 101L),
                new Enrollment(11L, 101L),
                new Enrollment(12L, 102L)
        ));
        when(repository.findCourseById(101L)).thenReturn(Optional.of(new Course(101L, "Computer Science 101", 1L)));
        when(repository.findCourseById(102L)).thenReturn(Optional.of(new Course(102L, "Database Systems", 2L)));
        when(repository.findAllInstructors()).thenReturn(List.of(
                new Instructor(1L, "Dr. Alice"),
                new Instructor(2L, "Prof. Bob"),
                new Instructor(3L, "Dr. Charlie")
        ));

        InstructorDTO result = instructorService.getMostActiveInstructor();

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Dr. Alice", result.name());
        assertEquals(2L, result.enrollmentCount());
    }

    @Test
    void testGetInstructorsWithNoEnrollments_ReturnsOnlyInactiveInstructors() {
        when(repository.findAllEnrollments()).thenReturn(List.of(
                new Enrollment(10L, 101L),
                new Enrollment(11L, 102L)
        ));
        when(repository.findCourseById(101L)).thenReturn(Optional.of(new Course(101L, "Computer Science 101", 1L)));
        when(repository.findCourseById(102L)).thenReturn(Optional.of(new Course(102L, "Database Systems", 2L)));
        when(repository.findAllInstructors()).thenReturn(List.of(
                new Instructor(1L, "Dr. Alice"),
                new Instructor(2L, "Prof. Bob"),
                new Instructor(3L, "Dr. Charlie")
        ));

        List<Instructor> result = instructorService.getInstructorsWithNoEnrollments();

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).id());
        assertEquals("Dr. Charlie", result.get(0).name());
    }

    @Test
    void testGetMostActiveInstructor_IgnoresMissingCourses() {
        when(repository.findAllEnrollments()).thenReturn(List.of(
                new Enrollment(10L, 999L),
                new Enrollment(11L, 101L)
        ));
        when(repository.findCourseById(999L)).thenReturn(Optional.empty());
        when(repository.findCourseById(101L)).thenReturn(Optional.of(new Course(101L, "Computer Science 101", 1L)));
        when(repository.findAllInstructors()).thenReturn(List.of(new Instructor(1L, "Dr. Alice")));

        InstructorDTO result = instructorService.getMostActiveInstructor();

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.enrollmentCount());
    }

    @Test
    void testGetInstructorsWithNoEnrollments_WhenAllInstructorsActive_ReturnsEmptyList() {
        when(repository.findAllEnrollments()).thenReturn(List.of(new Enrollment(10L, 101L)));
        when(repository.findCourseById(101L)).thenReturn(Optional.of(new Course(101L, "Computer Science 101", 1L)));
        when(repository.findAllInstructors()).thenReturn(List.of(new Instructor(1L, "Dr. Alice")));

        List<Instructor> result = instructorService.getInstructorsWithNoEnrollments();

        assertTrue(result.isEmpty());
    }
}
