package com.example.demo.dto;

public record HabitacionDto(
        Long id,
        String nombreHotel,
        String direccion,
        int mangos,
        Long hotelId,
        double precio
) {}

