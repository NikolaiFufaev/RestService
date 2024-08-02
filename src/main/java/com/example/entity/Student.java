package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Student {

    private Long id;

    private String name;

    private Coordinator coordinator;

    private List<Course> courses;

    public void addCourses(Course course){
       this.courses.add(course);
    }
}
