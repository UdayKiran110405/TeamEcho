package com.echo.AttendenceService.controller;

import com.echo.AttendenceService.dto.request.CourseCreateRequest;
import com.echo.AttendenceService.dto.request.EnrollmentRequest;
import com.echo.AttendenceService.dto.response.ApiResponse;
import com.echo.AttendenceService.dto.response.CourseResponse;
import com.echo.AttendenceService.dto.response.StudentResponse;
import com.echo.AttendenceService.security.UserDetailsImpl;
import com.echo.AttendenceService.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CourseCreateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentInstructor) {
        CourseResponse response = courseService.createCourse(request, currentInstructor);
        return new ResponseEntity<>(ApiResponse.success("Course created successfully", response), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getAllCourses() {
        List<CourseResponse> courses = courseService.getAllCourses();
        return ResponseEntity.ok(ApiResponse.success("Courses retrieved successfully", courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable("id") Long id) {
        CourseResponse course = courseService.getCourseById(id);
        return ResponseEntity.ok(ApiResponse.success("Course retrieved successfully", course));
    }

    @PostMapping("/enroll")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<String>> enrollCourse(
            @Valid @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentStudent) {
        courseService.enrollStudent(request.getCourseId(), currentStudent);
        return ResponseEntity.ok(ApiResponse.success("Successfully enrolled in course", null));
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasRole('INSTRUCTOR') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getEnrolledStudents(@PathVariable("id") Long id) {
        List<StudentResponse> students = courseService.getStudentsByCourse(id);
        return ResponseEntity.ok(ApiResponse.success("Enrolled students fetched", students));
    }
}