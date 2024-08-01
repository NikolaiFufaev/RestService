package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StudentCourse {
    private Long id;
    @Setter
    private Long studentId;
    @Setter
    private Long courseId;
}
