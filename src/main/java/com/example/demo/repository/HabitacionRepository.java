package com.example.demo.repository;

import com.example.demo.entity.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion,Long> {
    List<Habitacion> findByUsuario_Id(Integer usuarioId);

}
