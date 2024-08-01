package com.example.service.impl;

import com.example.entity.Student;
import com.example.exception.NotFoundException;
import com.example.repository.StudentRepository;
import com.example.repository.impl.StudentRepositoryImpl;
import com.example.service.StudentService;
import com.example.servlet.dto.StudentDto;
import com.example.servlet.mapper.StudentMapper;
import com.example.servlet.mapper.impl.StudentMapperImpl;

import java.util.List;

public class StudentServiceImpl implements StudentService {
    private static StudentService instance;
    private final StudentRepository studentRepository = StudentRepositoryImpl.getInstance();
    private static final StudentMapper studentMapper = StudentMapperImpl.getInstance();

    private StudentServiceImpl() {
    }

    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentServiceImpl();
        }
        return instance;
    }


    @Override
    public StudentDto save(StudentDto studentInDto) {
        Student student = studentMapper.mapFromDto(studentInDto);
        student = studentRepository.save(student);
        return studentMapper.mapToDto(student);
    }

    @Override
    public void update(StudentDto studentDto) throws NotFoundException {
        Student student = studentMapper.mapFromDto(studentDto);
        studentRepository.update(student);
    }

    @Override
    public StudentDto findById(Long studentId) throws NotFoundException {
        Student student = studentRepository.findById(studentId).orElseThrow(() ->
                new NotFoundException("Student not found"));
        return studentMapper.mapToDto(student);
    }

    @Override
    public List<StudentDto> findAll() {
        List<Student> studentList = studentRepository.findAll();
        return studentMapper.mapToListDto(studentList);
    }

    @Override
    public void delete(Long studentId) throws NotFoundException {
        studentRepository.deleteById(studentId);
    }

    @Override
    public void saveCoordinatorInStudent(Long studentId, Long coordinatorId) {
        studentRepository.saveCoordinatorByStudentId(studentId, coordinatorId);
    }
}
