package com.example.service;

import com.example.servlet.dto.StudentDto;

public interface StudentService extends Service<StudentDto, Long> {
    void saveCoordinatorInStudent(Long studentId, Long coordinatorId);
}
