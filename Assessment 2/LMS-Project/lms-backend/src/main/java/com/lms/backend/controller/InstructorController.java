package com.lms.backend.controller;

import com.lms.backend.dto.InstructorDTO;
import com.lms.backend.model.Instructor;
import com.lms.backend.service.InstructorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/instructors")
public class InstructorController {
    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping("/most-active")
    public InstructorDTO getMostActive() {
        return instructorService.getMostActiveInstructor();
    }

    @GetMapping("/no-enrollments")
    public List<Instructor> getNoEnrollments() {
        return instructorService.getInstructorsWithNoEnrollments();
    }
}
