package com.example.repository.impl;


import com.example.entity.Coordinator;
import com.example.repository.CoordinatorRepository;
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
class CoordinatorRepositoryTest {
    private static final String INIT_SQL = "sql/schema.sql";

    private static int containerPort = 5432;
    private static int localPort = 5432;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;
    public static CoordinatorRepository coordinatorRepository;
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
        coordinatorRepository = CoordinatorRepositoryImpl.getInstance();
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
        String expectedName = "new Coordinator Yo!";
        Coordinator coordinator = new Coordinator(
                null,
                expectedName,
                null
        );
        coordinator = coordinatorRepository.save(coordinator);
        Optional<Coordinator> resultCoordinator = coordinatorRepository.findById(coordinator.getId());

        Assertions.assertTrue(resultCoordinator.isPresent());
        Assertions.assertEquals(expectedName, resultCoordinator.get().getName());
    }

    @Test
    void update() {
        String expectedName = "Update coordinator name";

        Coordinator coordinator = coordinatorRepository.findById(2L).get();
        String oldName = coordinator.getName();
        int expectedSizeUserList = coordinator.getStudents().size();
        coordinator.setName(expectedName);
        coordinatorRepository.update(coordinator);

        Coordinator resultCoordinator = coordinatorRepository.findById(2L).get();
        int resultSizeUserList = resultCoordinator.getStudents().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, resultCoordinator.getName());
        Assertions.assertEquals(expectedSizeUserList, resultSizeUserList);
    }

    @Test
    void deleteById() {
        int expectedSize = coordinatorRepository.findAll().size();

        Coordinator tempDepartment = new Coordinator(null, "New department", List.of());
        tempDepartment = coordinatorRepository.save(tempDepartment);

        int resultSizeBefore = coordinatorRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        coordinatorRepository.deleteById(tempDepartment.getId());
        int resultSizeAfter = coordinatorRepository.findAll().size();

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
        Optional<Coordinator> coordinator = coordinatorRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, coordinator.isPresent());
        coordinator.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @Test
    void findAll() {
        int expectedSize = 3;
        int resultSize = coordinatorRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }


}
