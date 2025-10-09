package com.example.demo.dto;

public record EstadoValidacionDto(
        boolean aprobado,
        String estado,
        String observaciones
) {}
