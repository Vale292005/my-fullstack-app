package com.example.demo.dto;

import com.example.demo.entity.Usuario;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public record HabitacionDto(
        Long id,
        String nombreHotel,
        String direccion,
        int mangos,
        @ManyToOne
        @JoinColumn(name="usuario_id",nullable = false)
        Usuario usuario
        ) {

}
