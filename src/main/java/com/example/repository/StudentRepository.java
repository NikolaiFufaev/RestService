package com.example.repository;

import com.example.entity.Student;

public interface StudentRepository extends Repository<Student, Long> {

    void saveCoordinatorByStudentId(Long studentId, Long coordinatorId);
}

