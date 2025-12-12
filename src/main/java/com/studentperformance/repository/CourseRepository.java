package com.studentperformance.repository;

import com.studentperformance.model.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Course findByCourseCode(String courseCode);

    List<Course> findByDepartment(String department);

    @Query("SELECT c FROM Course c WHERE c.courseName LIKE %:keyword% OR c.courseCode LIKE %:keyword%")
    List<Course> searchCourses(@Param("keyword") String keyword);

    List<Course> findBySemester(String semester);
}