package com.example.demo.repository;

import com.example.demo.entity.Reserva;
import com.example.demo.entity.Usuario;
import com.example.demo.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Reservas de un usuario específico
    List<Reserva> findByUsuarioId(Long usuarioId);

    // Reservas de una habitación específica
    List<Reserva> findByHabitacion(Habitacion habitacion);

    // Reservas de un usuario y habitación específica
    List<Reserva> findByUsuarioAndHabitacion(Usuario usuario, Habitacion habitacion);
}

