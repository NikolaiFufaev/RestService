package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Coordinator {

    private Long id;

    private String name;

    private List<Student> students;

    public void addStudents(Student student) {
        this.students.add(student);
    }
}
