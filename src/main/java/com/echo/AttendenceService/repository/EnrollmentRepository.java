package com.echo.AttendenceService.repository;

import com.echo.AttendenceService.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {

    // Check if a student is actively registered in a course (crucial before marking attendance)
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    Optional<EnrollmentEntity> findByStudentIdAndCourseId(Long studentId, Long courseId);

    // Fetch enrollments by student with associated course details
    @Query("SELECT e FROM EnrollmentEntity e JOIN FETCH e.course WHERE e.student.id = :studentId")
    List<EnrollmentEntity> findByStudentIdWithCourse(@Param("studentId") Long studentId);

    // Fetch all enrollments for a course with student details
    @Query("SELECT e FROM EnrollmentEntity e JOIN FETCH e.student WHERE e.course.id = :courseId")
    List<EnrollmentEntity> findByCourseIdWithStudent(@Param("courseId") Long courseId);
}