package com.example.demo.dto;

import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ComentarioDto (
        Long id,
        Usuario usuario,
        Hotel hotel,
        Habitacion habitacion,
        String mensaje,
        int calificacion,// 1 a 5 mangos
        LocalDateTime fchCreacion

){
}
