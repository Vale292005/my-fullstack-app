package com.example.demo.entity;

import com.example.demo.Enum.Rol;

public class Usuario {
    private Integer id;
    private String nombre;
    private String telefono;
    private String email;
    private int edad;
    private String contrasenha;
    private Rol rol;

    public Usuario(){}
    public Usuario(int id, String nombre, String telefono, String email, int edad, String contrasenha,Rol rol) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
        this.edad = edad;
        this.contrasenha=contrasenha;
        this.rol=rol;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContrasenha() {
        return contrasenha;
    }

    public void setContrasenha(String contrasenha) {
        this.contrasenha = contrasenha;
    }
}
