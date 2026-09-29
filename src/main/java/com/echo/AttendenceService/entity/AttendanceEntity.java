package com.echo.AttendenceService.entity;

import com.echo.AttendenceService.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "ECHO_ATTENDANCE",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_attendance_entry",
                columnNames = {"course_id", "student_id", "attendance_date"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Owning side: Target course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private CourseEntity course;

    // Owning side: Target student
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private UserEntity student;

    // Owning side: Instructor logging this entry
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marked_by_id", nullable = false)
    private UserEntity markedBy;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 255)
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public AttendanceEntity(CourseEntity course, UserEntity student, UserEntity markedBy, LocalDate attendanceDate, AttendanceStatus status, String remarks) {
        this.course = course;
        this.student = student;
        this.markedBy = markedBy;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
    }
}