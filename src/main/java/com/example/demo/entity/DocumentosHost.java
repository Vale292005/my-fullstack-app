package com.example.demo.entity;

import com.example.demo.dto.DocumentosHostDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Table(name="DocumentosHotel")
public class DocumentosHost {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    private String identificacion;
    private String certificadoBancario;
    private String certificadoPropiedad;

    private boolean aprobado;
    private String estado;
    private String observaciones;

    public DocumentosHost(Usuario usuario, DocumentosHostDto dto) {
        this.usuario = usuario;
        this.identificacion = dto.identificacion();
        this.certificadoBancario = dto.certificadoBancario();
        this.certificadoPropiedad = dto.certificadoPropiedad();
        this.estado = "PENDIENTE";
    }

    public DocumentosHost() {}
}

