package com.echo.AttendenceService.repository;

import com.echo.AttendenceService.entity.AttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<AttendanceEntity, Long> {

    // Database duplicate check before inserting
    boolean existsByCourseIdAndStudentIdAndAttendanceDate(Long courseId, Long studentId, LocalDate attendanceDate);

    // Fetch attendance history for a single student (with course and instructor info)
    @Query("SELECT a FROM AttendanceEntity a " +
            "JOIN FETCH a.course " +
            "JOIN FETCH a.markedBy " +
            "WHERE a.student.id = :studentId " +
            "ORDER BY a.attendanceDate DESC")
    List<AttendanceEntity> findByStudentIdOrderByAttendanceDateDesc(@Param("studentId") Long studentId);

    // Fetch attendance for a specific course session
    @Query("SELECT a FROM AttendanceEntity a " +
            "JOIN FETCH a.student " +
            "WHERE a.course.id = :courseId AND a.attendanceDate = :attendanceDate")
    List<AttendanceEntity> findByCourseIdAndAttendanceDate(
            @Param("courseId") Long courseId,
            @Param("attendanceDate") LocalDate attendanceDate
    );

    // Detailed fetch for update/view operations to prevent LazyInitializationException
    @Query("SELECT a FROM AttendanceEntity a " +
            "JOIN FETCH a.course c " +
            "JOIN FETCH c.instructor " +
            "JOIN FETCH a.student " +
            "WHERE a.id = :id")
    Optional<AttendanceEntity> findByIdWithDetails(@Param("id") Long id);
}