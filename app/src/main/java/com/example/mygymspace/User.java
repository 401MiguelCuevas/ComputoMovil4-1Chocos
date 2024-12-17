package com.example.mygymspace;

public class User {
    private int id;
    private String nombre;

    public User(int id, String name) {
        this.id = id;
        this.nombre = name;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}