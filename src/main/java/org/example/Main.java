package org.example;

import org.example.Exceptions.NoSuchStudentIdException;
import org.example.Exceptions.NoSuchStudentNameException;
import org.example.entities.Student;
import org.example.ftp.Ftp;
import org.example.services.impl.StudentServiceImpl;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class Main {

    private static final StudentServiceImpl service = new StudentServiceImpl();
    private static final Ftp ftp = new Ftp();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            ftp.openConnection();
            ftp.loadStudents(service);

            boolean exit = false;
            while (!exit) {
                System.out.println("\nMenu:");
                System.out.println("1. Get a list of students by name");
                System.out.println("2. Get a student by id");
                System.out.println("3. Add student");
                System.out.println("4. Delete student by id");
                System.out.println("5. Exit");
                System.out.print("Select an action (1-5): ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:

                        System.out.print("Enter the student name: ");
                        String name = scanner.nextLine();
                        List<Student> studentsByName = service.getByName(name);
                        for (Student student: studentsByName) {
                            System.out.println(student.getId().toString() + " " + student.getName());
                        }

                        break;

                    case 2:

                        System.out.print("Enter the student id: ");
                        String idString = scanner.nextLine();
                        UUID id = UUID.fromString(idString);
                        Student student = service.getById(id);
                        System.out.println(student.getId().toString() + " " + student.getName());
                        break;
                    case 3:
                        System.out.print("Enter the student name: ");
                        name = scanner.nextLine();
                        service.addStudent(name);
                        ftp.saveStudentsToFile(service);
                        System.out.println("Students:");
                        for (Student stud : service.students) {
                            System.out.println(stud.getId().toString() + " " + stud.getName());
                        }
                        break;

                    case 4:
                        System.out.print("Enter the student id: ");
                        idString = scanner.nextLine();
                        id = UUID.fromString(idString);
                        service.deleteById(id);
                        ftp.saveStudentsToFile(service);
                        System.out.println("Students:");
                        for (Student stud : service.students) {
                            System.out.println(stud.getId().toString() + " " + stud.getName());
                        }
                        break;

                    case 5:
                        exit = true;
                        break;

                    default:
                        System.out.println("Wrong choice.");
                        break;
                }
            }
            ftp.closeConnection();
        } catch (NoSuchStudentIdException | NoSuchStudentNameException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
