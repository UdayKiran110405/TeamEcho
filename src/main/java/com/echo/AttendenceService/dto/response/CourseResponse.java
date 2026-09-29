package com.echo.AttendenceService.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CourseResponse {

    private Long id;
    private String courseCode;
    private String courseName;
    private Long instructorId;
    private String instructorName;
    private LocalDateTime createdAt;

    public CourseResponse(Long id, String courseCode, String courseName, Long instructorId, String instructorName, LocalDateTime createdAt) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.createdAt = createdAt;
    }
}