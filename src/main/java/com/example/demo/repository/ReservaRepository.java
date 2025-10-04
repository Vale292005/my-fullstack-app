package com.example.demo.repository;

import com.example.demo.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository {
    List<Reserva> findByUsuario_Id(Integer usuarioId);
    List<Reserva> findByHabitacion_Id(Integer habitacionId);

}
