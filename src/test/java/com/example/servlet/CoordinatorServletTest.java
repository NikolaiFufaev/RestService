package com.example.servlet;

import com.example.exception.NotFoundException;
import com.example.service.CoordinatorService;
import com.example.service.impl.CoordinatorServiceImpl;
import com.example.servlet.dto.CoordinatorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;
@ExtendWith(MockitoExtension.class)
class CoordinatorServletTest {

    private static CoordinatorService mockCoordinatorService;
    @InjectMocks
    private static CoordinatorServlet coordinatorServlet;
    private static CoordinatorServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(CoordinatorService mock) {
        try {
            Field instance = CoordinatorServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (CoordinatorServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockCoordinatorService = Mockito.mock(CoordinatorService.class);
        setMock(mockCoordinatorService);
        coordinatorServlet = new CoordinatorServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = CoordinatorServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockCoordinatorService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("coordinator/all").when(mockRequest).getPathInfo();

        coordinatorServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockCoordinatorService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("coordinator/2").when(mockRequest).getPathInfo();

        coordinatorServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockCoordinatorService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("coordinator/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockCoordinatorService).findById(100L);

        coordinatorServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("coordinator/2q").when(mockRequest).getPathInfo();

        coordinatorServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("coordinator/2").when(mockRequest).getPathInfo();

        coordinatorServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockCoordinatorService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("coordinator/a100").when(mockRequest).getPathInfo();

        coordinatorServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "New coordinator";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        coordinatorServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<CoordinatorDto> argumentCaptor = ArgumentCaptor.forClass(CoordinatorDto.class);
        Mockito.verify(mockCoordinatorService).save(argumentCaptor.capture());

        CoordinatorDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedName = "Update coordinator";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 3,\"name\": \"" +
                expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        coordinatorServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<CoordinatorDto> argumentCaptor = ArgumentCaptor.forClass(CoordinatorDto.class);
        Mockito.verify(mockCoordinatorService).update(argumentCaptor.capture());

        CoordinatorDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        coordinatorServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}