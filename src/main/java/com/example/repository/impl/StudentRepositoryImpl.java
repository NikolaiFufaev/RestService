package com.example.repository.impl;

import com.example.db.ConnectionManager;
import com.example.db.ConnectionManagerImpl;
import com.example.entity.Coordinator;
import com.example.entity.Course;
import com.example.entity.Student;
import com.example.exception.RepositoryException;
import com.example.repository.StudentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepositoryImpl implements StudentRepository {
    private static final String SAVE_SQL = """
            INSERT INTO student ("student_name")
            VALUES (?)
            """;
    private static final String UPDATE_SQL = """
            UPDATE student
            SET student_name = ?
            WHERE EXISTS( SELECT 1
                          FROM student
                          WHERE student_id = ?)
                          AND student_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM student
            WHERE student_id = ?
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT student.student_id,
             student_name,
              student.coordinator_id,
               coordinator_name,
                c.course_id,
                 course_name
            FROM student
            LEFT JOIN "student_course" sc on "student"."student_id" = sc."student_id"
            LEFT JOIN "course" c on c."course_id" = sc."course_id"
            LEFT JOIN "coordinator" c2 on c2."coordinator_id" = "student"."coordinator_id"
            WHERE student.student_id = ?
            """;
    private static final String FIND_ALL_SQL = """
            SELECT student.student_id,
             student_name,
              student.coordinator_id,
               coordinator_name,
                c.course_id,
                 course_name
            FROM student
            LEFT JOIN "student_course" sc on "student"."student_id" = sc."student_id"
            LEFT JOIN "course" c on c."course_id" = sc."course_id"
            LEFT JOIN "coordinator" c2 on c2."coordinator_id" = "student"."coordinator_id"
            ORDER BY student_id;
            """;
    private static final String UPDATE_COORDINATOR_SQL = """
            UPDATE student
            SET coordinator_id = ?
            WHERE EXISTS( SELECT 1
                          FROM coordinator
                          WHERE coordinator.coordinator_id = ?)
                          AND student_id = ?;
            """;

    private static StudentRepository instance;
    private static final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private StudentRepositoryImpl() {

    }

    public static synchronized StudentRepository getInstance() {
        if (instance == null) {
            instance = new StudentRepositoryImpl();
        }
        return instance;
    }


    private static Student createStudent(ResultSet resultSet) throws SQLException {
        Student student;
        student = new Student(
                resultSet.getLong("student_id"),
                resultSet.getString("student_name"),
                new Coordinator(
                        resultSet.getLong("coordinator_id"),
                        resultSet.getString("coordinator_name"),
                        null),
                new ArrayList<>());
        return student;
    }

    private static Course createCourse(ResultSet resultSet) throws SQLException {
        Course course;
        course = new Course(
                resultSet.getLong("course_id"),
                resultSet.getString("course_name"),
                null);
        return course;
    }


    @Override
    public Student save(Student student) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                student.setId(resultSet.getLong("student_id"));
            }

            if (student.getCoordinator() != null) {
                saveCoordinatorByStudentId(student.getId(), student.getCoordinator().getId());
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return student;
    }

    @Override
    public void update(Student student) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, student.getName());
            preparedStatement.setLong(2, student.getId());
            preparedStatement.setLong(3, student.getId());
            preparedStatement.executeUpdate();
            if (student.getCoordinator() != null) {
                saveCoordinatorByStudentId(student.getId(), student.getCoordinator().getId());
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

    }

    @Override
    public void deleteById(Long id) {

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        Student student = null;
        Course course;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (student == null) {
                    student = createStudent(resultSet);
                    course = createCourse(resultSet);
                    student.addCourses(course);
                    continue;
                }
                course = createCourse(resultSet);
                student.addCourses(course);
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return Optional.ofNullable(student);
    }

    @Override
    public List<Student> findAll() {
        List<Student> studentList = new ArrayList<>();
        Student student = null;
        Course course;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)
        ) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (student == null) {
                    student = createStudent(resultSet);
                    course = createCourse(resultSet);
                    student.addCourses(course);
                    studentList.add(student);
                    continue;
                }

                if (student.getId() != resultSet.getLong("student_id")) {
                    student = createStudent(resultSet);
                    studentList.add(student);
                }
                course = createCourse(resultSet);
                student.addCourses(course);

            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return studentList;
    }

    @Override
    public void saveCoordinatorByStudentId(Long studentId, Long coordinatorId) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COORDINATOR_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setLong(1, coordinatorId);
            preparedStatement.setLong(2, coordinatorId);
            preparedStatement.setLong(3, studentId);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }
}