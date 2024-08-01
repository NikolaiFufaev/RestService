package com.example.repository.impl;

import com.example.db.ConnectionManager;
import com.example.db.ConnectionManagerImpl;
import com.example.entity.Coordinator;
import com.example.entity.Course;
import com.example.entity.Student;
import com.example.exception.RepositoryException;
import com.example.repository.CourseRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseRepositoryImpl implements CourseRepository {

    private static final String SAVE_SQL = """
            INSERT INTO course (course_name)
            VALUES (?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE course
            SET course_name = ?
            WHERE EXISTS( SELECT 1
                          FROM course
                          WHERE course_id = ?)
                          AND course_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM course
            WHERE course_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT course.course_id,
             course_name,
              s.student_id,
               student_name,
                s.coordinator_id,
                 coordinator_name
            FROM course
            LEFT JOIN "student_course" sc on "course"."course_id" = sc."course_id"
            LEFT JOIN "student" s on s."student_id" = sc."student_id"
            LEFT JOIN "coordinator" c on c."coordinator_id" = s."coordinator_id"
            WHERE course.course_id = ?;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT course.course_id,
             course_name,
              s.student_id,
               student_name,
                s.coordinator_id,
                 coordinator_name
            FROM course
            LEFT JOIN "student_course" sc on "course"."course_id" = sc."course_id"
            LEFT JOIN "student" s on s."student_id" = sc."student_id"
            LEFT JOIN "coordinator" c on c."coordinator_id" = s."coordinator_id"
            ORDER BY course_id;
            """;


    private static CourseRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private CourseRepositoryImpl() {
    }

    public static synchronized CourseRepository getInstance() {
        if (instance == null) {
            instance = new CourseRepositoryImpl();
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
                null);
        return student;
    }

    private static Course createCourse(ResultSet resultSet) throws SQLException {
        Course course;
        course = new Course(
                resultSet.getLong("course_id"),
                resultSet.getString("course_name"),
                new ArrayList<>());
        return course;
    }

    @Override
    public Course save(Course course) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, course.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                course.setId(resultSet.getLong("course_id"));
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return course;
    }

    @Override
    public void update(Course course) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, course.getName());
            preparedStatement.setLong(2, course.getId());
            preparedStatement.setLong(3, course.getId());
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
    public Optional<Course> findById(Long id) {
        Student student;
        Course course = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (course == null) {
                    course = createCourse(resultSet);
                    student = createStudent(resultSet);
                    course.addStudent(student);
                    continue;
                }
                course.addStudent(createStudent(resultSet));

            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(course);
    }

    @Override
    public List<Course> findAll() {
        List<Course> courseList = new ArrayList<>();
        Student student;
        Course course = null;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (course == null) {
                    course = createCourse(resultSet);
                    student = createStudent(resultSet);
                    course.addStudent(student);
                    courseList.add(course);
                    continue;
                }

                if (course.getId() != resultSet.getLong("course_id")) {
                    course = createCourse(resultSet);
                    courseList.add(course);
                }
                student = createStudent(resultSet);
                course.addStudent(student);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return courseList;
    }

}