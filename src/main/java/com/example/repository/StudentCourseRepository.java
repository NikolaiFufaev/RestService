package com.example.repository;

import com.example.entity.StudentCourse;

import java.util.Optional;

public interface StudentCourseRepository extends Repository<StudentCourse,Long> {

    Optional<StudentCourse> findCourseIdAndStudentId(Long courseId, Long studentId);

}
