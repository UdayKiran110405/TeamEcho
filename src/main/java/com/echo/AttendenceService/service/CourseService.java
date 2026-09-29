package com.echo.AttendenceService.service;

import com.echo.AttendenceService.dto.request.CourseCreateRequest;
import com.echo.AttendenceService.dto.response.CourseResponse;
import com.echo.AttendenceService.dto.response.StudentResponse;
import com.echo.AttendenceService.entity.CourseEntity;
import com.echo.AttendenceService.entity.EnrollmentEntity;
import com.echo.AttendenceService.entity.UserEntity;
import com.echo.AttendenceService.exception.BadRequestException;
import com.echo.AttendenceService.exception.ResourceNotFoundException;
import com.echo.AttendenceService.repository.CourseRepository;
import com.echo.AttendenceService.repository.EnrollmentRepository;
import com.echo.AttendenceService.repository.UserRepository;
import com.echo.AttendenceService.security.UserDetailsImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public CourseService(CourseRepository courseRepository,
                         UserRepository userRepository,
                         EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request, UserDetailsImpl currentInstructor) {
        if (courseRepository.existsByCourseCode(request.getCourseCode())) {
            throw new BadRequestException("Course code already exists: " + request.getCourseCode());
        }

        UserEntity instructor = userRepository.getReferenceById(currentInstructor.getId());
        CourseEntity course = new CourseEntity(request.getCourseCode(), request.getCourseName(), instructor);
        CourseEntity savedCourse = courseRepository.save(course);

        return mapToResponse(savedCourse);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        CourseEntity course = courseRepository.findByIdWithInstructor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
        return mapToResponse(course);
    }

    @Transactional
    public void enrollStudent(Long courseId, UserDetailsImpl currentStudent) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(currentStudent.getId(), courseId)) {
            throw new BadRequestException("Student is already enrolled in this course.");
        }

        CourseEntity course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        UserEntity student = userRepository.getReferenceById(currentStudent.getId());

        EnrollmentEntity enrollment = new EnrollmentEntity(student, course);
        enrollmentRepository.save(enrollment);
    }

    private CourseResponse mapToResponse(CourseEntity course) {
        return new CourseResponse(
                course.getId(),
                course.getCourseCode(),
                course.getCourseName(),
                course.getInstructor().getId(),
                course.getInstructor().getName(),
                course.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<com.echo.AttendenceService.dto.response.StudentResponse> getStudentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseIdWithStudent(courseId).stream()
                .map(e -> new com.echo.AttendenceService.dto.response.StudentResponse(
                        e.getStudent().getId(),
                        e.getStudent().getName(),
                        e.getStudent().getEmail()
                ))
                .collect(Collectors.toList());
    }
}