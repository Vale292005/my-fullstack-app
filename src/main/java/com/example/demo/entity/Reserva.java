package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="usuario_id",nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="habitacion_id",nullable = false)
    private Habitacion habitacion;

    private LocalDate fchEntrada;
    private LocalDate fchSalida;

    private double precioTotal;
}
