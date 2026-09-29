package com.echo.AttendenceService.dto.request;

import com.echo.AttendenceService.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttendanceUpdateRequest {

    @NotNull(message = "Attendance status is required")
    private AttendanceStatus status;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    public AttendanceUpdateRequest(AttendanceStatus status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }
}