package org.example.Exceptions;

public class NoSuchStudentNameException extends Exception{
    public NoSuchStudentNameException(String name){
        super("Student with name " + name + " doesn't exist.");
    }
}
