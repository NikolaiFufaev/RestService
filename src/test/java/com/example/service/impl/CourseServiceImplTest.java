package com.example.service.impl;

import com.example.entity.Course;
import com.example.exception.NotFoundException;
import com.example.repository.CourseRepository;
import com.example.repository.impl.CourseRepositoryImpl;
import com.example.service.CourseService;
import com.example.servlet.dto.CourseDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class CourseServiceImplTest {

    private static CourseService courseService;
    private static CourseRepository mockCourseRepository;
    private static CourseRepositoryImpl oldInstance;

    private static void setMock(CourseRepository mock) {
        try {
            Field instance = CourseRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (CourseRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockCourseRepository = Mockito.mock(CourseRepository.class);
        setMock(mockCourseRepository);
        courseService = CourseServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = CourseRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockCourseRepository);
    }


    @Test
    void save() {
        Long expectedId = 1L;

        CourseDto dto = new CourseDto(null, "course #2", null);
        Course course = new Course(expectedId, "course #10", null);
        Mockito.doReturn(course).when(mockCourseRepository).save(Mockito.any(Course.class));
        CourseDto result = courseService.save(dto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        CourseDto dto = new CourseDto(expectedId, "course update #1", null);
        courseService.update(dto);
        ArgumentCaptor<Course> argumentCaptor = ArgumentCaptor.forClass(Course.class);
        Mockito.verify(mockCourseRepository).update(argumentCaptor.capture());
        Course result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Course> course = Optional.of(new Course(expectedId, "course found #1", List.of()));
        Mockito.doReturn(course).when(mockCourseRepository).findById(Mockito.anyLong());
        CourseDto dto = courseService.findById(expectedId);
        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findAll() {
        courseService.findAll();
        Mockito.verify(mockCourseRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        courseService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockCourseRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}