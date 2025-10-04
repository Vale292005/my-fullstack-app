package com.example.demo.dto;

import com.example.demo.entity.Usuario;

public record HotelDto (
        Long id,
        String nombre,
        String direccion,
        Usuario usuario
){
}
