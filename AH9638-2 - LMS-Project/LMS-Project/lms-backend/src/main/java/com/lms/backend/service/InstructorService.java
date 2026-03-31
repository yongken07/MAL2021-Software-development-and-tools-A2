package com.lms.backend.service;

import com.lms.backend.dto.InstructorDTO;
import com.lms.backend.model.Course;
import com.lms.backend.model.Instructor;
import com.lms.backend.repository.LMSRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InstructorService {
    private final LMSRepository repository;

    public InstructorService(LMSRepository repository) {
        this.repository = repository;
    }

    public InstructorDTO getMostActiveInstructor() {
        Map<Long, Long> instructorEnrollmentCounts = repository.findAllEnrollments().stream()
                .map(e -> repository.findCourseById(e.courseId()).orElse(null))
                .filter(c -> c != null)
                .collect(Collectors.groupingBy(Course::instructorId, Collectors.counting()));

        return repository.findAllInstructors().stream()
                .map(i -> new InstructorDTO(i.id(), i.name(), instructorEnrollmentCounts.getOrDefault(i.id(), 0L)))
                .max(Comparator.comparingLong(InstructorDTO::enrollmentCount))
                .orElse(null);
    }

    public List<Instructor> getInstructorsWithNoEnrollments() {
        List<Long> activeInstructorIds = repository.findAllEnrollments().stream()
                .map(e -> repository.findCourseById(e.courseId()).orElse(null))
                .filter(c -> c != null)
                .map(Course::instructorId)
                .distinct()
                .toList();

        return repository.findAllInstructors().stream()
                .filter(i -> !activeInstructorIds.contains(i.id()))
                .collect(Collectors.toList());
    }
}
