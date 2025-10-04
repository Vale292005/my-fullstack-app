package com.example.demo.dto.usuariodto;

import com.example.demo.Enum.Rol;

public record UsuarioDto(
        String nombre,
        String telefono,
        String email,
        int edad,
        Rol rol
) {}


