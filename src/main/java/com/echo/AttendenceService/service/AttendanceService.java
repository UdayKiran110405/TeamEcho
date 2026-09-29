package com.echo.AttendenceService.service;

import com.echo.AttendenceService.dto.request.AttendanceRecordRequest;
import com.echo.AttendenceService.dto.request.AttendanceUpdateRequest;
import com.echo.AttendenceService.dto.response.AttendanceResponse;
import com.echo.AttendenceService.entity.AttendanceEntity;
import com.echo.AttendenceService.entity.CourseEntity;
import com.echo.AttendenceService.entity.UserEntity;
import com.echo.AttendenceService.exception.BadRequestException;
import com.echo.AttendenceService.exception.ResourceNotFoundException;
import com.echo.AttendenceService.repository.AttendanceRepository;
import com.echo.AttendenceService.repository.CourseRepository;
import com.echo.AttendenceService.repository.EnrollmentRepository;
import com.echo.AttendenceService.repository.UserRepository;
import com.echo.AttendenceService.security.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Transactional
    public AttendanceResponse recordAttendance(AttendanceRecordRequest request, UserDetailsImpl currentInstructor) {
        CourseEntity course = courseRepository.findByIdWithInstructor(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        // Security check: Only the instructor assigned to the course can mark attendance
        if (!course.getInstructor().getId().equals(currentInstructor.getId())) {
            throw new AccessDeniedException("You are not the designated instructor for this course.");
        }

        UserEntity student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + request.getStudentId()));

        // Security check: Student must be registered in the course
        boolean isEnrolled = enrollmentRepository.existsByStudentIdAndCourseId(request.getStudentId(), request.getCourseId());
        if (!isEnrolled) {
            throw new BadRequestException("Student is not enrolled in this course.");
        }

        // Anti-Duplicate check
        boolean exists = attendanceRepository.existsByCourseIdAndStudentIdAndAttendanceDate(
                request.getCourseId(), request.getStudentId(), request.getAttendanceDate());
        if (exists) {
            throw new BadRequestException("Attendance record already exists for this student on " + request.getAttendanceDate());
        }

        UserEntity instructor = userRepository.getReferenceById(currentInstructor.getId());

        AttendanceEntity attendance = new AttendanceEntity(
                course,
                student,
                instructor,
                request.getAttendanceDate(),
                request.getStatus(),
                request.getRemarks()
        );

        AttendanceEntity saved = attendanceRepository.save(attendance);
        return mapToResponse(saved);
    }

    @Transactional
    public AttendanceResponse updateAttendance(Long attendanceId, AttendanceUpdateRequest request, UserDetailsImpl currentInstructor) {
        AttendanceEntity attendance = attendanceRepository.findByIdWithDetails(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found with id: " + attendanceId));

        // IDOR Prevention: Ensure the modifying instructor is the course instructor
        if (!attendance.getCourse().getInstructor().getId().equals(currentInstructor.getId())) {
            throw new AccessDeniedException("Unauthorized to update attendance records for another instructor's course.");
        }

        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());

        AttendanceEntity updated = attendanceRepository.save(attendance);
        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendanceHistory(Long studentId, UserDetailsImpl currentUser) {
        // Anti-IDOR Check: If user is a student, ensure they are requesting ONLY their own records
        boolean isStudent = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"));
        boolean isPrivileged = currentUser.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_INSTRUCTOR"));

        if (isStudent && !currentUser.getId().equals(studentId) && !isPrivileged) {
            throw new AccessDeniedException("Forbidden: You cannot view attendance history of other students.");
        }

        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        List<AttendanceEntity> records = attendanceRepository.findByStudentIdOrderByAttendanceDateDesc(studentId);
        return records.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private AttendanceResponse mapToResponse(AttendanceEntity entity) {
        return new AttendanceResponse(
                entity.getId(),
                entity.getCourse().getId(),
                entity.getCourse().getCourseCode(),
                entity.getCourse().getCourseName(),
                entity.getStudent().getId(),
                entity.getStudent().getName(),
                entity.getStudent().getEmail(),
                entity.getMarkedBy().getId(),
                entity.getMarkedBy().getName(),
                entity.getAttendanceDate(),
                entity.getStatus(),
                entity.getRemarks(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}