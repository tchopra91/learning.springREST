package com.learning.springrest.rest;

import java.util.ArrayList;
import java.util.List;

import com.learning.springrest.entity.Student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StudentRestController {

    @GetMapping("/students")
    public List<Student> getStudents() {
        List<Student> students = new ArrayList<>();

        students.add(new Student(1, "Eric", "Anderson", true));
        students.add(new Student(2, "Mary", "Karlson", true));
        students.add(new Student(3, "James", "Bond", true));
        students.add(new Student(4, "Jack", "Rayan", true));

        return students;
    }
}