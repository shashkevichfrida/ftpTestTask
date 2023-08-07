package org.example.services.impl;

import org.example.Exceptions.NoSuchStudentIdException;
import org.example.Exceptions.NoSuchStudentNameException;
import org.example.entities.Student;
import org.example.services.StudentService;

import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class StudentServiceImpl implements StudentService {
    private List<Student> studentsPrivate = new ArrayList<>();
    public List<Student> students = Collections.unmodifiableList(studentsPrivate);
    @Override
    public List<Student> getByName(String name) throws NoSuchStudentNameException {
        List<Student> studentList = studentsPrivate.stream().filter(student -> student.getName().equals(name)).collect(Collectors.toList());
        if (studentList.isEmpty()){
            throw new NoSuchStudentNameException(name);
        }
        return studentList;
    }

    @Override
    public Student getById(UUID id) throws NoSuchStudentIdException {
        return studentsPrivate.stream().filter(student -> student.getId().equals(id)).findFirst().orElseThrow(() -> new NoSuchStudentIdException(id));
    }

    @Override
    public Student addStudent(String name) {
        Student student = new Student(UUID.randomUUID(), name);
        studentsPrivate.add(student);
        Collections.sort(studentsPrivate, Comparator.comparing(Student::getName));
        return student;
    }

    @Override
    public void deleteById(UUID id) throws NoSuchStudentIdException {
        Student student = studentsPrivate.stream().filter(stud -> stud.getId().equals(id)).findFirst().orElseThrow(() -> new NoSuchStudentIdException(id));
        studentsPrivate.remove(student);
    }

    public void addStudentToList(Student student){
        studentsPrivate.add(student);
    }

    public void clearStudents(){
        studentsPrivate.clear();
    }
}
