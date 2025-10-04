package com.example.demo.dto.usuariodto;

public class LoginRequestDto {
    private String email;
    private String contrsenha;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrsenha() {
        return contrsenha;
    }

    public void setContrsenha(String contrsenha) {
        this.contrsenha = contrsenha;
    }
}
