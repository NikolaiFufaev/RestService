package com.example.service;

import com.example.exception.NotFoundException;
import com.example.servlet.dto.CourseDto;

public interface CourseService extends Service<CourseDto, Long> {
    void addStudentToCourse(Long studentId, Long courseId);

    void deleteStudentFromCourse(Long courseId, Long studentId) throws NotFoundException;
}
