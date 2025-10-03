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
    @JoinColumn(name="usuario_id",nullable = false)
    private Usuario usuario;
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
