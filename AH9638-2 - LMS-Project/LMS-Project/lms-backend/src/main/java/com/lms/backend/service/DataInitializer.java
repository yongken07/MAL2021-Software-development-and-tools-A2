package com.lms.backend.service;

import com.lms.backend.model.*;
import com.lms.backend.repository.InMemoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    private final InMemoryRepository repository;

    public DataInitializer(InMemoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        // Sample Instructors
        repository.addInstructor(new Instructor(1L, "Dr. Alice"));
        repository.addInstructor(new Instructor(2L, "Prof. Bob"));
        repository.addInstructor(new Instructor(3L, "Dr. Charlie")); // No students

        // Sample Courses
        repository.addCourse(new Course(101L, "Computer Science 101", 1L));
        repository.addCourse(new Course(102L, "Advanced Algorithms", 1L));
        repository.addCourse(new Course(103L, "Database Systems", 2L));

        // Sample Students
        repository.addStudent(new Student(10L, "John Doe"));
        repository.addStudent(new Student(11L, "Jane Smith"));
        repository.addStudent(new Student(12L, "Tom Brown")); // No enrollments

        // Sample Enrollments
        repository.addEnrollment(new Enrollment(10L, 101L));
        repository.addEnrollment(new Enrollment(10L, 102L));
        repository.addEnrollment(new Enrollment(11L, 101L));
        repository.addEnrollment(new Enrollment(11L, 103L));
    }
}
