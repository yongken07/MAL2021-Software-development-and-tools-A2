package com.lms.backend.controller;

import com.lms.backend.dto.CourseDTO;
import com.lms.backend.model.Student;
import com.lms.backend.service.StudentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}/enrollments")
    public List<CourseDTO> getEnrollments(@PathVariable("id") Long id) {
        return studentService.getStudentEnrollments(id);
    }

    @GetMapping("/active")
    public List<Student> getActiveStudents() {
        return studentService.getActiveStudents();
    }
}
