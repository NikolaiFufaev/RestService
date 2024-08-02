package com.example.service.impl;

import com.example.entity.Course;
import com.example.entity.StudentCourse;
import com.example.exception.NotFoundException;
import com.example.repository.CourseRepository;
import com.example.repository.StudentCourseRepository;
import com.example.repository.impl.CourseRepositoryImpl;
import com.example.repository.impl.StudentCourseRepositoryImpl;
import com.example.service.CourseService;
import com.example.servlet.dto.CourseDto;
import com.example.servlet.mapper.CourseMapper;
import com.example.servlet.mapper.impl.CourseMapperImpl;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    private final StudentCourseRepository studentCourseRepository = StudentCourseRepositoryImpl.getInstance();
    private final CourseRepository courseRepository = CourseRepositoryImpl.getInstance();
    private static final CourseMapper courseMapper = CourseMapperImpl.getInstance();

    private static CourseService instance;


    private CourseServiceImpl() {
    }

    public static synchronized CourseService getInstance() {
        if (instance == null) {
            instance = new CourseServiceImpl();
        }
        return instance;
    }

    @Override
    public CourseDto save(CourseDto courseDto) {
        Course course = courseMapper.mapFromDto(courseDto);
        course = courseRepository.save(course);
        return courseMapper.mapToDto(course);
    }

    @Override
    public void update(CourseDto courseDto) throws NotFoundException {
        Course course = courseMapper.mapFromDto(courseDto);
        courseRepository.update(course);
    }

    @Override
    public CourseDto findById(Long courseId) throws NotFoundException {
        Course course = courseRepository.findById(courseId).orElseThrow(() ->
                new NotFoundException("Course not found"));
        return courseMapper.mapToDto(course);
    }

    @Override
    public List<CourseDto> findAll() {
        List<Course> courseList = courseRepository.findAll();
        return courseMapper.mapToListDto(courseList);
    }

    @Override
    public void delete(Long courseId) throws NotFoundException {
        courseRepository.deleteById(courseId);
    }

    @Override
    public void addStudentToCourse(Long studentId, Long courseId) {
        StudentCourse studentCourse = new StudentCourse(
                null,
                studentId,
                courseId
        );
        studentCourseRepository.save(studentCourse);
    }

    @Override
    public void deleteStudentFromCourse(Long courseId, Long studentId) throws NotFoundException {
        StudentCourse linkUserDepartment = studentCourseRepository.findCourseIdAndStudentId(courseId, studentId)
                .orElseThrow(() -> new NotFoundException("Link many to many Not found."));

        studentCourseRepository.deleteById(linkUserDepartment.getId());
    }

}
