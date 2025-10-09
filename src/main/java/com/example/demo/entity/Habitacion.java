package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="Habitaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Habitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombreHotel;
    private String direccion;
    private int mangos;

    @ManyToOne
    @JoinColumn(name="hotel_id",nullable = false)
    private Hotel hotel;
    private double precio;

}
