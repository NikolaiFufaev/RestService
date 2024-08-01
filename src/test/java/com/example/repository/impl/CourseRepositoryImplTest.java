package com.example.repository.impl;

import com.example.entity.Course;
import com.example.repository.CourseRepository;
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

import java.util.List;
import java.util.Optional;


@Testcontainers
@Tag("DockerRequired")
public class CourseRepositoryImplTest {
    private static final String INIT_SQL = "sql/schema.sql";

    private static int containerPort = 5432;
    private static int localPort = 5432;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;
    public static CourseRepository courseRepository;
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
        courseRepository = CourseRepositoryImpl.getInstance();
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
        String expectedName = "new Course";
        Course course = new Course(
                null,
                expectedName,
                null
        );
        course = courseRepository.save(course);
        Optional<Course> resultCourse = courseRepository.findById(course.getId());

        Assertions.assertTrue(resultCourse.isPresent());
        Assertions.assertEquals(expectedName, resultCourse.get().getName());

    }

    @Test
    void update() {
        String expectedName = "Update course name";

        Course course = courseRepository.findById(2L).get();
        String oldName = course.getName();
        int expectedSizeStudents = course.getStudents().size();
        course.setName(expectedName);
        courseRepository.update(course);

        Course resultCourse = courseRepository.findById(2L).get();
        int resultSizeStudents = resultCourse.getStudents().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, resultCourse.getName());
        Assertions.assertEquals(expectedSizeStudents, resultSizeStudents);
    }

    @Test
    void deleteById() {
        int expectedSize = courseRepository.findAll().size();

        Course tempCourse = new Course(null, "New course", List.of());
        tempCourse = courseRepository.save(tempCourse);

        int resultSizeBefore = courseRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        courseRepository.deleteById(tempCourse.getId());
        int resultSizeAfter = courseRepository.findAll().size();
        Assertions.assertEquals(expectedSize, resultSizeAfter);

    }

    @DisplayName("Find by ID")
    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "1000, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Course> course = courseRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, course.isPresent());
        course.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 3;
        int resultSize = courseRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }


}
