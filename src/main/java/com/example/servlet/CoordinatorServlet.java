package com.example.servlet;

import com.example.exception.NotFoundException;
import com.example.service.CoordinatorService;
import com.example.service.impl.CoordinatorServiceImpl;
import com.example.servlet.dto.CoordinatorDto;
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

@WebServlet(name = "coordinator" ,urlPatterns = "/coordinator/*")
public class CoordinatorServlet extends HttpServlet {
    private final transient CoordinatorService coordinatorService = CoordinatorServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public CoordinatorServlet() {
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

        String responseAnswer = "";

        try {
            String[] pathPart = req.getPathInfo().split("/");
            if ("all".equals(pathPart[1])) {
                List<CoordinatorDto> coordinatorDtoList = coordinatorService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(coordinatorDtoList);
            } else {
                Long coordinatorId = Long.parseLong(pathPart[1]);
                CoordinatorDto coordinatorDto = coordinatorService.findById(coordinatorId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(coordinatorDto);
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

        String responseAnswer = null;
        Optional<CoordinatorDto> coordinatorResponse;
        try {
            coordinatorResponse = Optional.ofNullable(objectMapper.readValue(json, CoordinatorDto.class));
            CoordinatorDto coordinatorDto = coordinatorResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(coordinatorService.save(coordinatorDto));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect coordinator Object.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<CoordinatorDto> coordinatorResponse;
        try {
            coordinatorResponse = Optional.ofNullable(objectMapper.readValue(json, CoordinatorDto.class));
            CoordinatorDto coordinatorDto = coordinatorResponse.orElseThrow(IllegalArgumentException::new);
            coordinatorService.update(coordinatorDto);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Incorrect coordinator Object.";
        }
        printWriter(resp, responseAnswer);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = "";
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long coordinatorId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            coordinatorService.delete(coordinatorId);
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
