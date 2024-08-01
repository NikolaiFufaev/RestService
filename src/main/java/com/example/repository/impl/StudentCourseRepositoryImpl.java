package com.example.repository.impl;

import com.example.db.ConnectionManager;
import com.example.db.ConnectionManagerImpl;
import com.example.entity.StudentCourse;
import com.example.exception.RepositoryException;
import com.example.repository.StudentCourseRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentCourseRepositoryImpl implements StudentCourseRepository {


    private static final String SAVE_SQL = """
            INSERT INTO student_course (student_id, course_id)
            SELECT  ?, ?
            WHERE NOT EXISTS(
                            SELECT student_course_id
                            FROM student_course
                            WHERE student_id = ? AND course_id = ?
                            );
            """;
    private static final String UPDATE_SQL = """
            UPDATE student_course
            SET student_id = ?,
            course_id = ?
            WHERE EXISTS( SELECT 1
                          FROM student_course
                          WHERE student_course_id = ?)
            AND student_course_id = ?;
            """;
    private static final String FIND_BY_STUDENT_ID_AND_COURSE_ID_SQL = """
            SELECT * FROM student_course
            WHERE student_id = ? AND course_id = ?
            LIMIT 1;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM student_course
            WHERE student_course_id = ?
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM student_course
            WHERE student_course_id = ?
            LIMIT 1
            """;
    private static final String FIND_ALL_SQL = """
            SELECT * FROM student_course
            ORDER BY course_id
            """;


    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static StudentCourseRepository instance;

    public static synchronized StudentCourseRepository getInstance() {
        if (instance == null) {
            instance = new StudentCourseRepositoryImpl();
        }
        return instance;
    }

    @Override
    public StudentCourse save(StudentCourse studentCourse) {
        try (
                Connection connection = connectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)
        ) {
            preparedStatement.setLong(1, studentCourse.getStudentId());
            preparedStatement.setLong(2, studentCourse.getCourseId());
            preparedStatement.setLong(3, studentCourse.getStudentId());
            preparedStatement.setLong(4, studentCourse.getCourseId());


            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                studentCourse = new StudentCourse(
                        resultSet.getLong("student_course_id"),
                        resultSet.getLong("student_id"),
                        resultSet.getLong("course_id"));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return studentCourse;
    }

    @Override
    public void update(StudentCourse studentCourse) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setLong(1, studentCourse.getStudentId());
            preparedStatement.setLong(2, studentCourse.getCourseId());
            preparedStatement.setLong(3, studentCourse.getId());
            preparedStatement.setLong(4, studentCourse.getId());

            preparedStatement.executeUpdate();
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
    public Optional<StudentCourse> findCourseIdAndStudentId(Long courseId, Long studentId) {
        Optional<StudentCourse> studentCourse = Optional.empty();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_STUDENT_ID_AND_COURSE_ID_SQL)) {

            preparedStatement.setLong(1, studentId);
            preparedStatement.setLong(2, courseId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                studentCourse = Optional.of(
                        createStudentToCourse(resultSet)
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return studentCourse;
    }

    @Override
    public Optional<StudentCourse> findById(Long id) {
        StudentCourse studentCourse = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                studentCourse = createStudentToCourse(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(studentCourse);
    }

    @Override
    public List<StudentCourse> findAll() {
        List<StudentCourse> studentCourses = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                studentCourses.add(createStudentToCourse(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return studentCourses;
    }

    private StudentCourse createStudentToCourse(ResultSet resultSet) throws SQLException {
        StudentCourse studentCourse;
        studentCourse = new StudentCourse(
                resultSet.getLong("student_course_id"),
                resultSet.getLong("student_id"),
                resultSet.getLong("course_id")
        );
        return studentCourse;
    }

}
