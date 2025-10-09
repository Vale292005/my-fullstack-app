package com.example.demo.repository;

import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, Long> {

    // Listar todas las habitaciones de un hotel específico
    List<Habitacion> findByHotel(Hotel hotel);
}

