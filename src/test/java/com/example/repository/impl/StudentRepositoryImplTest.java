package com.example.repository.impl;

import com.example.entity.Coordinator;
import com.example.entity.Student;
import com.example.repository.StudentRepository;
import com.example.util.PropertiesUtil;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Container;

import java.util.Optional;

class StudentRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";
    private static final int containerPort = 5432;
    private static final int localPort = 5432;
    @Container
    public static PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("db")
            .withUsername(PropertiesUtil.getProperties("db.username"))
            .withPassword(PropertiesUtil.getProperties("db.password"))
            .withExposedPorts(containerPort)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(localPort), new ExposedPort(containerPort)))
            ))
            .withInitScript(INIT_SQL);
    public static StudentRepository studentRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        container.start();
        studentRepository = StudentRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(container, "");
    }

    @AfterAll
    static void afterAll() {
        container.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }

    @Test
    void saveEmptyCoordinator() {
        String expectedName = "new name";

        Student student = new Student(
                null,
                expectedName,
                null,
                null);
        student = studentRepository.save(student);
        Optional<Student> resultUser = studentRepository.findById(student.getId());
        Assertions.assertTrue(resultUser.isPresent());
        Assertions.assertEquals(expectedName, resultUser.get().getName());

    }

    @Test
    void saveWithCoordinator() {
        String expectedName = "new name2";
        Coordinator coordinator = new Coordinator(1L, "Степан", null);
        Student student = new Student(
                null,
                expectedName,
                coordinator,
                null);
        student = studentRepository.save(student);
        Optional<Student> resultUser = studentRepository.findById(student.getId());
        Assertions.assertTrue(resultUser.isPresent());
        Assertions.assertEquals(expectedName, resultUser.get().getName());
        Assertions.assertEquals(coordinator.getId(), resultUser.get().getCoordinator().getId());

    }

    @Test
    void updateStudent(){
        String expectedName = "UPDATE Student Name";

        Student studentForUpdate = studentRepository.findById(3L).get();
        String oldStudentName = studentForUpdate.getName();

        studentForUpdate.setName(expectedName);
        studentRepository.update(studentForUpdate);

        Student student = studentRepository.findById(3L).get();

        Assertions.assertNotEquals(expectedName, oldStudentName);
        Assertions.assertEquals(expectedName, student.getName());

    }



    @Test
    void updateStudentNameWithCoordinator(){
        String expectedName = "UPDATE Student Name 2";
        Coordinator newCoordinatorId = new Coordinator(3L,null,null);
        Student studentForUpdate = studentRepository.findById(3L).get();
        String oldStudentName = studentForUpdate.getName();
        long oldCoordinatorId = studentForUpdate.getCoordinator().getId();
        studentForUpdate.setName(expectedName);
        studentForUpdate.setCoordinator(newCoordinatorId);
        studentRepository.update(studentForUpdate);

        Student student = studentRepository.findById(3L).get();

        Assertions.assertNotEquals(expectedName, oldStudentName);
        Assertions.assertEquals(expectedName, student.getName());
        Assertions.assertNotEquals(oldCoordinatorId,student.getCoordinator().getId());

    }
    @Test
    void updateStudentCoordinatorId(){
        long newCoordinatorId = 3L;
        Student studentForUpdate = studentRepository.findById(3L).get();
        long oldCoordinatorId = studentForUpdate.getCoordinator().getId();
        studentRepository.saveCoordinatorByStudentId(studentForUpdate.getId(),newCoordinatorId);
        Student student = studentRepository.findById(3L).get();
        Assertions.assertNotEquals(oldCoordinatorId,student.getCoordinator().getId());


    }

    @Test
    void deleteById() {

        int expectedSize = studentRepository.findAll().size();

        Student tempStudent = new Student(
                null,
                "User for delete name.",
                null,
                null
        );
        studentRepository.save(tempStudent);
        studentRepository.deleteById(tempStudent.getId());
        int roleListAfterSize = studentRepository.findAll().size();

        Assertions.assertEquals(expectedSize, roleListAfterSize);
    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "4; true",
            "100; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Student> student = studentRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, student.isPresent());
        student.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 4;
        int resultSize = studentRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }


}






