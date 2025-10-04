package com.example.demo.dto;

import java.time.LocalDate;

public record ReservaDto(
        Long id,
        Long usuarioId,
        Long habitacionId,
        LocalDate fchEntrada,
        LocalDate fchSalida,
        double precioTotal
) {
}
