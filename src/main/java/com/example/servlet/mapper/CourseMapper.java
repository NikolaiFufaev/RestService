package com.example.servlet.mapper;

import com.example.entity.Course;
import com.example.servlet.dto.CourseDto;

import java.util.List;

public interface CourseMapper extends Mapper<Course, CourseDto> {
    List<Course> mapFromListDto(List<CourseDto> listDto);

}
