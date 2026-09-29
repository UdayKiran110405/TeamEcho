package com.echo.AttendenceService.repository;

import com.echo.AttendenceService.entity.CourseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    Optional<CourseEntity> findByCourseCode(String courseCode);

    boolean existsByCourseCode(String courseCode);

    // Fetch all courses assigned to a specific instructor
    List<CourseEntity> findByInstructorId(Long instructorId);

    // Security check: verify if an instructor is the authorized teacher of a course
    boolean existsByIdAndInstructorId(Long courseId, Long instructorId);

    // Fetch course along with its instructor to prevent lazy initialization issues
    @Query("SELECT c FROM CourseEntity c JOIN FETCH c.instructor WHERE c.id = :id")
    Optional<CourseEntity> findByIdWithInstructor(@Param("id") Long id);
}