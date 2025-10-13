package com.example.demo.entity;

import com.example.demo.Enum.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios") // Nombre de la tabla
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String telefono;

    @Column(nullable = false, unique = true) // no se permiten emails repetidos
    private String email;

    private LocalDate edad;

    @Column(nullable = false)
    private String contrasenha;

    @Enumerated(EnumType.STRING) // Guarda el enum como texto
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hoteles = new ArrayList<>();
    public boolean activo;

}

