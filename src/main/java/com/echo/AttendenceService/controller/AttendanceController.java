package com.echo.AttendenceService.controller;

import com.echo.AttendenceService.dto.request.AttendanceRecordRequest;
import com.echo.AttendenceService.dto.request.AttendanceUpdateRequest;
import com.echo.AttendenceService.dto.response.ApiResponse;
import com.echo.AttendenceService.dto.response.AttendanceResponse;
import com.echo.AttendenceService.security.UserDetailsImpl;
import com.echo.AttendenceService.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> recordAttendance(
            @Valid @RequestBody AttendanceRecordRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentInstructor) {
        AttendanceResponse response = attendanceService.recordAttendance(request, currentInstructor);
        return new ResponseEntity<>(ApiResponse.success("Attendance marked successfully", response), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<AttendanceResponse>> updateAttendance(
            @PathVariable("id") Long id,
            @Valid @RequestBody AttendanceUpdateRequest request,
            @AuthenticationPrincipal UserDetailsImpl currentInstructor) {
        AttendanceResponse response = attendanceService.updateAttendance(id, request, currentInstructor);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated successfully", response));
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyRole('STUDENT', 'INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getStudentAttendance(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        List<AttendanceResponse> history = attendanceService.getStudentAttendanceHistory(id, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Attendance history retrieved successfully", history));
    }
}