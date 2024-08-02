package com.example.repository.impl;

import com.example.entity.StudentCourse;
import com.example.repository.StudentCourseRepository;
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
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

@Testcontainers
@Tag("DockerRequired")
class StudentCourseRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";

    private static final int containerPort = 5432;
    private static final int localPort = 5432;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;
    public static StudentCourseRepository studentCourseRepository;
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


    @BeforeAll
    static void beforeAll() {
        container.start();
        studentCourseRepository = StudentCourseRepositoryImpl.getInstance();
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
    void save() {
        Long expectedStudentId = 1L;
        Long expectedCourse = 3L;
        StudentCourse link = new StudentCourse(
                null,
                expectedStudentId,
                expectedCourse
        );
        link = studentCourseRepository.save(link);
        Optional<StudentCourse> resultLink = studentCourseRepository.findById(link.getId());

        Assertions.assertTrue(resultLink.isPresent());
        Assertions.assertEquals(expectedStudentId, resultLink.get().getStudentId());
        Assertions.assertEquals(expectedCourse, resultLink.get().getCourseId());
    }

    @Test
    void update() {
        Long expectedStudentId = 2L;
        Long expectedCourseId = 3L;

        StudentCourse link = studentCourseRepository.findById(2L).get();

        Long oldCourseId = link.getCourseId();
        Long oldStudentId = link.getStudentId();

        Assertions.assertNotEquals(expectedStudentId, oldStudentId);
        Assertions.assertNotEquals(expectedCourseId, oldCourseId);

        link.setStudentId(expectedStudentId);
        link.setCourseId(expectedCourseId);

        studentCourseRepository.update(link);

        StudentCourse resultLink = studentCourseRepository.findById(2L).get();

        Assertions.assertEquals(link.getId(), resultLink.getId());
        Assertions.assertEquals(expectedStudentId, resultLink.getStudentId());
        Assertions.assertEquals(expectedCourseId, resultLink.getCourseId());
    }

    @Test
    void deleteById() {
        int expectedSize = studentCourseRepository.findAll().size();

        StudentCourse link = new StudentCourse(null, 1L, 3L);
        link = studentCourseRepository.save(link);

        int resultSizeBefore = studentCourseRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        studentCourseRepository.deleteById(link.getId());

        int resultSizeAfter = studentCourseRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSizeAfter);

    }

    @DisplayName("Find Students by Course Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, 1, true",
            "1, 3, false"
    })
    void findCourseIdAndStudentId(Long studentId, Long courseId, Boolean expectedValue) {
        Optional<StudentCourse> link = studentCourseRepository.findCourseIdAndStudentId(courseId, studentId);

        Assertions.assertEquals(expectedValue, link.isPresent());
    }

    @DisplayName("Delete by Id.")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true, 1, 1",
            "3, true, 2, 1",
            "1000, false, 0, 0"
    })
    void findById(Long expectedId, Boolean expectedValue, Long expectedUserId, Long expectedDepartmentId) {
        Optional<StudentCourse> link = studentCourseRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, link.isPresent());
        if (link.isPresent()) {
            Assertions.assertEquals(expectedId, link.get().getId());
            Assertions.assertEquals(expectedUserId, link.get().getStudentId());
            Assertions.assertEquals(expectedDepartmentId, link.get().getCourseId());
        }
    }

    @Test
    void findAll() {
        int expectedSize = 7;
        int resultSize = studentCourseRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }
}