package org.example.entities;

import java.util.UUID;

public class Student {
    private UUID id;
    private String name;

    public Student(UUID id, String name){
        this.id = id;
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public UUID getId(){
        return this.id;
    }

    public String getStringId(){
        return this.id.toString();
    }


}
