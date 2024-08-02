package com.example.servlet.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseDto {

    private Long id;
    private String name;
    private List<StudentDto> studentDtoList;

}
