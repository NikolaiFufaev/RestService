package com.example.servlet.mapper.impl;

import com.example.entity.Student;
import com.example.servlet.dto.StudentDto;
import com.example.servlet.mapper.CoordinatorMapper;
import com.example.servlet.mapper.CourseMapper;
import com.example.servlet.mapper.StudentMapper;

import java.util.ArrayList;
import java.util.List;

public class StudentMapperImpl implements StudentMapper {

    public static final CourseMapper courseMapper = CourseMapperImpl.getInstance();
    public static final CoordinatorMapper coordinatorMapper = CoordinatorMapperImpl.getInstance();
    private static StudentMapper instance;

    private StudentMapperImpl() {

    }

    public static synchronized StudentMapper getInstance() {
        if (instance == null) {
            instance = new StudentMapperImpl();
        }
        return instance;
    }

    @Override
    public Student mapFromDto(StudentDto dto) {
        Student student = new Student();
        if (dto.getId() != null) {
            student.setId(dto.getId());
        }
        student.setName(dto.getName());
        if (dto.getCoordinatorDto() != null) {
            student.setCoordinator(coordinatorMapper.mapFromDto(dto.getCoordinatorDto()));
        }
        if (dto.getCourses() != null) {
            student.setCourses(courseMapper.mapFromListDto(dto.getCourses()));
        }
        return student;
    }

    @Override
    public StudentDto mapToDto(Student entity) {
        StudentDto dto = new StudentDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getCoordinator() != null) {
            dto.setCoordinatorDto(coordinatorMapper.mapToDto(entity.getCoordinator()));
        }
        if (entity.getCourses() != null) {
            dto.setCourses(courseMapper.mapToListDto(entity.getCourses()));
        }
        return dto;
    }

    @Override
    public List<StudentDto> mapToListDto(List<Student> listEntity) {
        List<StudentDto> dtoList = new ArrayList<>();
        for (Student student : listEntity) {
            dtoList.add(mapToDto(student));
        }

        return dtoList;
    }

    @Override
    public List<Student> mapFromListDto(List<StudentDto> listDto) {
        List<Student> studentList = new ArrayList<>();
        for (StudentDto dto : listDto) {
            studentList.add(mapFromDto(dto));
        }
        return studentList;
    }
}
