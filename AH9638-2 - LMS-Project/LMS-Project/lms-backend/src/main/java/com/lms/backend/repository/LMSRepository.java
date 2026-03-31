package com.lms.backend.repository;

import com.lms.backend.model.*;
import java.util.List;
import java.util.Optional;

public interface LMSRepository {
    void addStudent(Student student);
    List<Student> findAllStudents();
    Optional<Student> findStudentById(Long id);

    void addCourse(Course course);
    List<Course> findAllCourses();
    Optional<Course> findCourseById(Long id);

    void addInstructor(Instructor instructor);
    List<Instructor> findAllInstructors();
    Optional<Instructor> findInstructorById(Long id);

    void addEnrollment(Enrollment enrollment);
    List<Enrollment> findAllEnrollments(); }