package com.example.demo.dto;

import com.example.demo.Enum.Rol;

public class UsuarioDto {
    private String nombre;
    private String telefono;
    private String email;
    private int edad;

    private String contrasenha;
    private Rol rol;

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

    public void setEmail(String email) {this.email = email;}

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getContrasenha() { return contrasenha;}

    public void setContrasenha(String contrasenha) { this.contrasenha = contrasenha;}

    public Rol getRol() {return rol;}
    public void setRol(Rol rol) {this.rol = rol;}
}
