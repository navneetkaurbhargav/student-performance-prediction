package com.studentperformance.repository;

import com.studentperformance.model.domain.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {

    List<StudentCourse> findByStudentId(Long studentId);

    List<StudentCourse> findByCourseId(Long courseCode);

    List<StudentCourse> findByStudentIdAndCourseId(
            Long studentId, Long courseCode);

    @Query("SELECT sc FROM StudentCourse sc WHERE sc.student.id = :studentId")
    List<StudentCourse> findCurrentCoursesByStudentId(@Param("studentId") Long studentId);

}