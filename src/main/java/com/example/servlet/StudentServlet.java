package com.example.servlet;

import com.example.exception.NotFoundException;
import com.example.service.StudentService;
import com.example.service.impl.StudentServiceImpl;
import com.example.servlet.dto.StudentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "student", urlPatterns = "/student/*")
public class StudentServlet extends HttpServlet {
    private final transient StudentService studentService = StudentServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public StudentServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static void printWriter(HttpServletResponse resp, String responseAnswer) throws IOException {
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();

    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer;

        try {
            String[] pathPart = req.getPathInfo().split("/");
            if ("all".equals(pathPart[1])) {
                List<StudentDto> studentDtoList = studentService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(studentDtoList);
            } else {
                Long studentId = Long.parseLong(pathPart[1]);
                StudentDto studentDto = studentService.findById(studentId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(studentDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer;
        Optional<StudentDto> studentResponse;
        try {
            studentResponse = Optional.ofNullable(objectMapper.readValue(json, StudentDto.class));
            StudentDto studentDto = studentResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(studentService.save(studentDto));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect student Object.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<StudentDto> studentResponse;
        try {
            if (req.getPathInfo().contains("/addCoordinator/")) {
                String[] pathPart = req.getPathInfo().split("/");
                if (pathPart.length > 3 && "addCoordinator".equals(pathPart[2])) {
                    Long studentId = Long.parseLong(pathPart[1]);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Long coordinatorId = Long.parseLong(pathPart[3]);
                    studentService.saveCoordinatorInStudent(studentId, coordinatorId);
                }
            } else {

                studentResponse = Optional.ofNullable(objectMapper.readValue(json, StudentDto.class));
                StudentDto studentDto = studentResponse.orElseThrow(IllegalArgumentException::new);
                studentService.update(studentDto);

            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect student Object.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long studentId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            studentService.delete(studentId);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request.";
        }
        printWriter(resp, responseAnswer);
    }


}