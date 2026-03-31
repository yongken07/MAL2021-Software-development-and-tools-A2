package com.lms.backend.repository;

import com.lms.backend.model.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InMemoryRepository implements LMSRepository {
    private final List<Student> students = new CopyOnWriteArrayList<>();
    private final List<Course> courses = new CopyOnWriteArrayList<>();
    private final List<Instructor> instructors = new CopyOnWriteArrayList<>();
    private final List<Enrollment> enrollments = new CopyOnWriteArrayList<>();

    // Students
    public void addStudent(Student student) { students.add(student); }
    public List<Student> findAllStudents() { return new ArrayList<>(students); }
    public Optional<Student> findStudentById(Long id) {
        return students.stream().filter(s -> s.id().equals(id)).findFirst();
    }

    // Courses
    public void addCourse(Course course) { courses.add(course); }
    public List<Course> findAllCourses() { return new ArrayList<>(courses); }
    public Optional<Course> findCourseById(Long id) {
        return courses.stream().filter(c -> c.id().equals(id)).findFirst();
    }

    // Instructors
    public void addInstructor(Instructor instructor) { instructors.add(instructor); }
    public List<Instructor> findAllInstructors() { return new ArrayList<>(instructors); }
    public Optional<Instructor> findInstructorById(Long id) {
        return instructors.stream().filter(i -> i.id().equals(id)).findFirst();
    }

    // Enrollments
    public void addEnrollment(Enrollment enrollment) { enrollments.add(enrollment); }
    public List<Enrollment> findAllEnrollments() { return new ArrayList<>(enrollments); }
}
