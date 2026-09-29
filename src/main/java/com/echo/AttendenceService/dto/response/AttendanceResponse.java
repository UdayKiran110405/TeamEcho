package com.echo.AttendenceService.dto.response;

import com.echo.AttendenceService.enums.AttendanceStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceResponse {

    private Long attendanceId;
    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private Long markedById;
    private String markedByName;
    private LocalDate attendanceDate;
    private AttendanceStatus status;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AttendanceResponse(Long attendanceId, Long courseId, String courseCode, String courseName,
                              Long studentId, String studentName, String studentEmail,
                              Long markedById, String markedByName, LocalDate attendanceDate,
                              AttendanceStatus status, String remarks,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.attendanceId = attendanceId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.markedById = markedById;
        this.markedByName = markedByName;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}