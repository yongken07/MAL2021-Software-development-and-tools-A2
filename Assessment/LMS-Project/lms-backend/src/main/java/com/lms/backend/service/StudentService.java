package com.lms.backend.service;

import com.lms.backend.dto.CourseDTO;
import com.lms.backend.model.Enrollment;
import com.lms.backend.model.Student;
import com.lms.backend.repository.LMSRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {
    private final LMSRepository repository;

    public StudentService(LMSRepository repository) {
        this.repository = repository;
    }

    public List<CourseDTO> getStudentEnrollments(Long studentId) {
        return repository.findAllEnrollments().stream()
                .filter(e -> e.studentId().equals(studentId))
                .map(e -> repository.findCourseById(e.courseId())
                        .map(c -> new CourseDTO(c.id(), c.name(), 
                            repository.findInstructorById(c.instructorId())
                                .map(i -> i.name()).orElse("Unknown Instructor")))
                        .orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public List<Student> getActiveStudents() {
        List<Long> enrolledStudentIds = repository.findAllEnrollments().stream()
                .map(Enrollment::studentId)
                .distinct()
                .toList();

        return repository.findAllStudents().stream()
                .filter(s -> enrolledStudentIds.contains(s.id()))
                .collect(Collectors.toList());
    }
}
