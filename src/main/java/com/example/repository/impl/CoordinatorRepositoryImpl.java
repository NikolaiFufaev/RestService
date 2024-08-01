package com.example.repository.impl;

import com.example.db.ConnectionManager;
import com.example.db.ConnectionManagerImpl;
import com.example.entity.Coordinator;
import com.example.entity.Course;
import com.example.entity.Student;
import com.example.exception.RepositoryException;
import com.example.repository.CoordinatorRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoordinatorRepositoryImpl implements CoordinatorRepository {

    private static final String SAVE_SQL = """
                    INSERT INTO coordinator (coordinator_name)
                    values (?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE coordinator
            SET coordinator_name = ?
            WHERE EXISTS( SELECT 1
                          FROM coordinator
                          WHERE coordinator_id = ?)
                          AND coordinator_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM coordinator
            WHERE coordinator_id = ?;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT coordinator.coordinator_id, coordinator_name, s.student_id,  student_name ,c.course_id, course_name
                        FROM coordinator
                                 LEFT JOIN student s on coordinator.coordinator_id = s.coordinator_id
                                 LEFT JOIN "student_course" sc on s."student_id" = sc."student_id"
                                 LEFT JOIN "course" c on sc."course_id" = c."course_id"
                        WHERE coordinator.coordinator_id = ?;""";
    private static final String FIND_ALL_SQL = """
            SELECT coordinator.coordinator_id, coordinator_name, s.student_id,  student_name ,c.course_id, course_name
                        FROM coordinator
                                 LEFT JOIN student s on coordinator.coordinator_id = s.coordinator_id
                                 LEFT JOIN "student_course" sc on s."student_id" = sc."student_id"
                                 LEFT JOIN "course" c on sc."course_id" = c."course_id"
                                 ORDER BY coordinator_id;
            """;
    private static CoordinatorRepository instance;

    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();


    public static synchronized CoordinatorRepository getInstance() {
        if (instance == null) {
            instance = new CoordinatorRepositoryImpl();
        }
        return instance;
    }

    private CoordinatorRepositoryImpl() {
    }

    private static Student createStudent(ResultSet resultSet) throws SQLException {
        Student student;
        student = new Student(
                resultSet.getLong("student_id"),
                resultSet.getString("student_name"),
                new Coordinator(resultSet.getLong("coordinator_id"),
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

    private static Coordinator createCoordinator(ResultSet resultSet) throws SQLException {
        Coordinator coordinator;
        coordinator = new Coordinator(resultSet.getLong("coordinator_id"),
                resultSet.getString("coordinator_name"),
                new ArrayList<>());
        return coordinator;
    }


    @Override
    public Coordinator save(Coordinator coordinator) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, coordinator.getName());
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                coordinator = createCoordinator(resultSet);
            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return coordinator;
    }

    @Override
    public void update(Coordinator coordinator) {
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setString(1, coordinator.getName());
            preparedStatement.setLong(2, coordinator.getId());
            preparedStatement.setLong(3, coordinator.getId());

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
    public Optional<Coordinator> findById(Long id) {
        Coordinator coordinator = null;
        Student student = null;
        Course course;

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (coordinator == null) {
                    coordinator = createCoordinator(resultSet);
                    student = createStudent(resultSet);
                    course = createCourse(resultSet);
                    student.addCourses(course);
                    coordinator.addStudents(student);
                    continue;
                }

                if (student.getId() != resultSet.getLong("student_id")) {
                    student = createStudent(resultSet);
                }
                course = createCourse(resultSet);
                student.addCourses(course);


            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(coordinator);
    }

    @Override
    public List<Coordinator> findAll() {
        List<Coordinator> coordinators = new ArrayList<>();
        Coordinator coordinator = null;
        Student student = null;
        Course course;
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                if (coordinator == null) {
                    coordinator = createCoordinator(resultSet);
                    student = createStudent(resultSet);
                    course = createCourse(resultSet);
                    student.addCourses(course);
                    coordinator.addStudents(student);
                    coordinators.add(coordinator);
                    continue;
                }
                if (coordinator.getId() != resultSet.getLong("coordinator_id")) {
                    coordinator = createCoordinator(resultSet);
                    student = createStudent(resultSet);
                    coordinators.add(coordinator);
                } else if (student.getId() != resultSet.getLong("student_id")) {
                    student = createStudent(resultSet);
                }
                course = createCourse(resultSet);
                student.addCourses(course);

            }

        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return coordinators;
    }
}
