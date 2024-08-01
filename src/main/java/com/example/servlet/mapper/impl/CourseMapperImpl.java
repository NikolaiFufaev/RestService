package com.example.servlet.mapper.impl;

import com.example.entity.Course;
import com.example.servlet.dto.CourseDto;
import com.example.servlet.mapper.CourseMapper;
import com.example.servlet.mapper.StudentMapper;

import java.util.ArrayList;
import java.util.List;

public class CourseMapperImpl implements CourseMapper {
    private static final StudentMapper studentMapper = StudentMapperImpl.getInstance();
    private static CourseMapper instance;

    private CourseMapperImpl() {

    }

    public static synchronized CourseMapper getInstance() {
        if (instance == null) {
            instance = new CourseMapperImpl();
        }
        return instance;
    }


    @Override
    public Course mapFromDto(CourseDto dto) {
        Course course = new Course();
        if (dto.getId() != null){
            course.setId(dto.getId());
        }
        course.setName(dto.getName());
        if (dto.getStudentDtoList() != null) {
            course.setStudents(studentMapper.mapFromListDto(dto.getStudentDtoList()));
        }
        return course;
    }

    @Override
    public CourseDto mapToDto(Course entity) {
        CourseDto dto = new CourseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        if (entity.getStudents() != null) {
            dto.setStudentDtoList(
                    studentMapper.mapToListDto(entity.getStudents())
            );
        }
        return dto;

    }

    @Override
    public List<CourseDto> mapToListDto(List<Course> listEntity) {
        List<CourseDto> dtoList = new ArrayList<>();
        for (Course course : listEntity) {
            dtoList.add(mapToDto(course));
        }
        return dtoList;
    }

    @Override
    public List<Course> mapFromListDto(List<CourseDto> listDto) {
        List<Course> courseList = new ArrayList<>();
        for (CourseDto dto : listDto) {
            courseList.add(mapFromDto(dto));
        }
        return courseList;
    }
}
