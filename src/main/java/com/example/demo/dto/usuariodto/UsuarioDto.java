package com.example.demo.dto.usuariodto;

import com.example.demo.Enum.Rol;

import java.time.LocalDate;

public record UsuarioDto(
        String nombre,
        String telefono,
        String email,
        LocalDate edad,
        Rol rol,
        boolean activo
) {}


