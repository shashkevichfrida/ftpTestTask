package org.example.Exceptions;

import java.util.UUID;

public class NoSuchStudentIdException extends Exception{
    public NoSuchStudentIdException(UUID id) {
        super("No such student with id: " + id.toString());
    }
}
