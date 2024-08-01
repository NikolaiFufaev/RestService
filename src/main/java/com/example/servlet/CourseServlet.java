package com.example.servlet;

import com.example.exception.NotFoundException;
import com.example.service.CourseService;
import com.example.service.impl.CourseServiceImpl;
import com.example.servlet.dto.CourseDto;
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

@WebServlet(name = "course", urlPatterns = "/course/*")
public class CourseServlet extends HttpServlet {
    private final transient CourseService courseService = CourseServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public CourseServlet() {
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
                List<CourseDto> courseDtoList = courseService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(courseDtoList);
            } else {
                Long coordinatorId = Long.parseLong(pathPart[1]);
                CourseDto courseDto = courseService.findById(coordinatorId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(courseDto);
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
        Optional<CourseDto> courseResponse;
        try {
            courseResponse = Optional.ofNullable(objectMapper.readValue(json, CourseDto.class));
            CourseDto courseDto = courseResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(courseService.save(courseDto));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect course Object.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<CourseDto> courseDtoResponse;
        try {
            if (req.getPathInfo().contains("/addStudent/")) {
                String[] pathPart = req.getPathInfo().split("/");
                if (pathPart.length > 3 && "addStudent".equals(pathPart[2])) {
                    Long courseId = Long.parseLong(pathPart[1]);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Long studentId = Long.parseLong(pathPart[3]);
                    courseService.addStudentToCourse(courseId, studentId);
                }
            } else {
                courseDtoResponse = Optional.ofNullable(objectMapper.readValue(json, CourseDto.class));
                CourseDto courseDto = courseDtoResponse.orElseThrow(IllegalArgumentException::new);
                courseService.update(courseDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect department Object.";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long courseId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (req.getPathInfo().contains("/deleteStudent/")) {
                if ("deleteStudent".equals(pathPart[2])) {
                    Long studentId = Long.parseLong(pathPart[3]);
                    courseService.deleteStudentFromCourse(courseId, studentId);
                }
            } else {
                courseService.delete(courseId);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Bad request. ";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }
}