package com.example.servlet.mapper;

import com.example.entity.Student;
import com.example.servlet.dto.StudentDto;

import java.util.List;

public interface StudentMapper extends Mapper<Student, StudentDto> {
    List<Student> mapFromListDto(List<StudentDto> dtoList);

}
