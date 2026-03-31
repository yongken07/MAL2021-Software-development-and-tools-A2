package com.lms.backend.service;

import com.lms.backend.dto.CourseDTO;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private LMSRepository repository;

    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetStudentEnrollments_Success() {
        // Arrange
        Long studentId = 1L;
        Long courseId = 101L;
        Long instructorId = 50L;
        
        when(repository.findAllEnrollments()).thenReturn(List.of(new Enrollment(studentId, courseId)));
        when(repository.findCourseById(courseId)).thenReturn(Optional.of(new Course(courseId, "Java 101", instructorId)));
        when(repository.findInstructorById(instructorId)).thenReturn(Optional.of(new Instructor(instructorId, "Dr. Smith")));

        // Act
        List<CourseDTO> enrollments = studentService.getStudentEnrollments(studentId);

        // Assert
        assertEquals(1, enrollments.size());
        assertEquals("Java 101", enrollments.get(0).name());
        assertEquals("Dr. Smith", enrollments.get(0).instructorName());
    }

    @Test
    void testGetStudentEnrollments_NoEnrollments() {
        // Arrange
        Long studentId = 1L;
        when(repository.findAllEnrollments()).thenReturn(List.of());

        // Act
        List<CourseDTO> enrollments = studentService.getStudentEnrollments(studentId);

        // Assert
        assertTrue(enrollments.isEmpty());
    }

    @Test
    void testGetStudentEnrollments_UnknownInstructorFallsBackToDefaultLabel() {
        Long studentId = 1L;
        Long courseId = 101L;
        Long missingInstructorId = 999L;

        when(repository.findAllEnrollments()).thenReturn(List.of(new Enrollment(studentId, courseId)));
        when(repository.findCourseById(courseId)).thenReturn(Optional.of(new Course(courseId, "Distributed Systems", missingInstructorId)));
        when(repository.findInstructorById(missingInstructorId)).thenReturn(Optional.empty());

        List<CourseDTO> enrollments = studentService.getStudentEnrollments(studentId);

        assertEquals(1, enrollments.size());
        assertEquals("Unknown Instructor", enrollments.get(0).instructorName());
    }

    @Test
    void testGetStudentEnrollments_MissingCourseIsIgnored() {
        Long studentId = 1L;

        when(repository.findAllEnrollments()).thenReturn(List.of(new Enrollment(studentId, 404L)));
        when(repository.findCourseById(404L)).thenReturn(Optional.empty());

        List<CourseDTO> enrollments = studentService.getStudentEnrollments(studentId);

        assertTrue(enrollments.isEmpty());
    }

    @Test
    void testGetActiveStudents_ReturnsDistinctEnrolledStudentsOnly() {
        when(repository.findAllEnrollments()).thenReturn(List.of(
                new Enrollment(10L, 101L),
                new Enrollment(10L, 102L),
                new Enrollment(11L, 101L)
        ));
        when(repository.findAllStudents()).thenReturn(List.of(
                new com.lms.backend.model.Student(10L, "John Doe"),
                new com.lms.backend.model.Student(11L, "Jane Smith"),
                new com.lms.backend.model.Student(12L, "Tom Brown")
        ));

        List<com.lms.backend.model.Student> activeStudents = studentService.getActiveStudents();

        assertEquals(2, activeStudents.size());
        assertTrue(activeStudents.stream().anyMatch(student -> student.id().equals(10L)));
        assertTrue(activeStudents.stream().anyMatch(student -> student.id().equals(11L)));
        assertFalse(activeStudents.stream().anyMatch(student -> student.id().equals(12L)));
    }
}
