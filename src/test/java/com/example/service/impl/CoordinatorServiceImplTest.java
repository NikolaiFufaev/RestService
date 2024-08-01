package com.example.service.impl;

import com.example.entity.Coordinator;
import com.example.exception.NotFoundException;
import com.example.repository.CoordinatorRepository;
import com.example.repository.impl.CoordinatorRepositoryImpl;
import com.example.service.CoordinatorService;
import com.example.servlet.dto.CoordinatorDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

class CoordinatorServiceImplTest {
    private static CoordinatorService coordinatorService;
    private static CoordinatorRepository coordinatorRepository;
    private static CoordinatorRepositoryImpl oldInstance;

    private static void setMock(CoordinatorRepository mock) {
        try {
            Field instance = CoordinatorRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (CoordinatorRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        coordinatorRepository = Mockito.mock(CoordinatorRepository.class);
        setMock(coordinatorRepository);
        coordinatorService = CoordinatorServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = CoordinatorRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(coordinatorRepository);
    }


    @Test
    void save() {
        Long expectedId = 1L;

        CoordinatorDto dto = new CoordinatorDto(null, "coordinator #2", null);
        Coordinator coordinator = new Coordinator(expectedId, "coordinator #10", null);
        Mockito.doReturn(coordinator).when(coordinatorRepository).save(Mockito.any(Coordinator.class));
        CoordinatorDto result = coordinatorService.save(dto);
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void update() throws NotFoundException {
        Long expectedId = 1L;

        CoordinatorDto dto = new CoordinatorDto(expectedId, "coordinator update #1", null);
        coordinatorService.update(dto);
        ArgumentCaptor<Coordinator> argumentCaptor = ArgumentCaptor.forClass(Coordinator.class);
        Mockito.verify(coordinatorRepository).update(argumentCaptor.capture());
        Coordinator result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getId());
    }

    @Test
    void findById() throws NotFoundException {
        Long expectedId = 1L;

        Optional<Coordinator> coordinator = Optional.of(new Coordinator(expectedId, "coordinator found #1", List.of()));
        Mockito.doReturn(coordinator).when(coordinatorRepository).findById(Mockito.anyLong());
        CoordinatorDto dto = coordinatorService.findById(expectedId);
        Assertions.assertEquals(expectedId, dto.getId());
    }

    @Test
    void findAll() {
        coordinatorService.findAll();
        Mockito.verify(coordinatorRepository).findAll();
    }

    @Test
    void delete() throws NotFoundException {
        Long expectedId = 100L;

        coordinatorService.delete(expectedId);
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(coordinatorRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result);
    }
}