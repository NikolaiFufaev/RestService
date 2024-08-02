package com.example.service.impl;

import com.example.entity.Student;
import com.example.exception.NotFoundException;
import com.example.repository.StudentRepository;
import com.example.repository.impl.StudentRepositoryImpl;
import com.example.service.StudentService;
import com.example.servlet.dto.StudentDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class StudentServiceImplTest {

    private static StudentService studentService;
    private static StudentRepository mockStudentRepository;
    private static StudentRepositoryImpl oldStudentInstance;

    private static void setMock(StudentRepository mock) {
        try {
            Field instance = StudentRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldStudentInstance = (StudentRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BeforeAll
    static void beforeAll() {
        mockStudentRepository = Mockito.mock(StudentRepository.class);
        setMock(mockStudentRepository);

        studentService = StudentServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = StudentRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldStudentInstance);

    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockStudentRepository);
    }


    @Test
    void save() {
        Long expectedId = 1L;

        StudentDto dto = new StudentDto(null, "student #2", null,null);
        Student student = new Student(expectedId, "student #10",null, List.of());

        Mockito.doReturn(student).when(mockStudentRepository).save(Mockito.any(Student.class));

        StudentDto result = studentService.save(dto);

        Assertions.assertEquals(expectedId, result.getId());

    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        StudentDto dto = new StudentDto(expectedId, "student update #1",null,null);

        studentService.update(dto);

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        Mockito.verify(mockStudentRepository).update(argumentCaptor.capture());

        Student result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Student> student = Optional.of(new Student(expectedId, "student found #1",null, List.of()));

        Mockito.doReturn(student).when(mockStudentRepository).findById(Mockito.anyLong());

        StudentDto dto = studentService.findById(expectedId);

        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findAll() {
        studentService.findAll();
        Mockito.verify(mockStudentRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        studentService.delete(expectedId);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockStudentRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }

}