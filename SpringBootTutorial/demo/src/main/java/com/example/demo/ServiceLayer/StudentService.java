package com.example.demo.ServiceLayer;

import com.example.demo.dao.StudentDAO;
import com.example.demo.model.Student;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
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

    public void deleteStudent(Long studentId) {

        boolean exists = studentDAO.existsById(studentId);
        if(!exists){
            throw new IllegalStateException("Student with id " + studentId + " does not exists");
        }
        studentDAO.deleteById(studentId);
    }

    @Transactional
    public void updateStudent(Long studentId, String name, String email) {
        Student student = studentDAO.findById(studentId).orElseThrow(() -> new IllegalStateException("student with id " + studentId + " does not exist"));
        if(name != null && name.length() > 0 && !Objects.equals(student.getName(), name)){
            student.setName(name);
        }
        if(email != null && email.length() > 0 && !Objects.equals(student.getEmail(), email)){
            Optional<Student> studentOptional = studentDAO.findStudentByEmail(email);
            if(studentOptional.isPresent()){
                throw new IllegalStateException("email taken");
            }
            student.setEmail(email);
        }
    }
}
