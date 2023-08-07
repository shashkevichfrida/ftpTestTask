package org.example.services;

import org.example.Exceptions.NoSuchStudentIdException;
import org.example.Exceptions.NoSuchStudentNameException;
import org.example.entities.Student;

import java.util.List;
import java.util.UUID;

public interface StudentService {
    public List<Student> getByName(String name) throws NoSuchStudentNameException;
    public Student getById(UUID id) throws NoSuchStudentIdException;
    public Student addStudent(String name);
    public void deleteById(UUID id) throws NoSuchStudentIdException;
}
