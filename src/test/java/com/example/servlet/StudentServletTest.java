package com.example.servlet;

import com.example.exception.NotFoundException;
import com.example.service.StudentService;
import com.example.service.impl.StudentServiceImpl;
import com.example.servlet.dto.StudentDto;
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
class StudentServletTest {

    private static StudentService mockStudentService;
    @InjectMocks
    private static StudentServlet studentServlet;
    private static StudentServiceImpl oldInstance;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private BufferedReader mockBufferedReader;

    private static void setMock(StudentService mock) {
        try {
            Field instance = StudentServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldInstance = (StudentServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockStudentService = Mockito.mock(StudentService.class);
        setMock(mockStudentService);
        studentServlet = new StudentServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = StudentServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockStudentService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("student/all").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("student/2").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("student/100").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("not found.")).when(mockStudentService).findById(100L);

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("student/2q").when(mockRequest).getPathInfo();

        studentServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("student/2").when(mockRequest).getPathInfo();

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockStudentService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("student/a100").when(mockRequest).getPathInfo();

        studentServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "New student";
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"name\":\"" + expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        studentServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<StudentDto> argumentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        Mockito.verify(mockStudentService).save(argumentCaptor.capture());

        StudentDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        String expectedName = "Update student";
        Mockito.doReturn("student/").when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{\"id\": 4,\"name\": \"" +
                expectedName + "\"}",
                null
        ).when(mockBufferedReader).readLine();

        studentServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<StudentDto> argumentCaptor = ArgumentCaptor.forClass(StudentDto.class);
        Mockito.verify(mockStudentService).update(argumentCaptor.capture());

        StudentDto result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getName());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn(
                "{Bad json:1}",
                null
        ).when(mockBufferedReader).readLine();

        studentServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}