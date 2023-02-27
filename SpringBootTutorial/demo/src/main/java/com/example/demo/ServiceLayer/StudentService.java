package com.example.demo.ServiceLayer;

import com.example.demo.dao.StudentDAO;
import com.example.demo.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final StudentDAO studentDAO;

    @Autowired
    public StudentService(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
    }

    public List<Student> getAllStudents() {
        return studentDAO.findAll();
    }

    public void addNewStudent(Student student) {
        Optional<Student> studentOptional = studentDAO.findStudentByEmail(student.getEmail());
        if(studentOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }
        studentDAO.save(student);
        System.out.println(student);
    }
}
