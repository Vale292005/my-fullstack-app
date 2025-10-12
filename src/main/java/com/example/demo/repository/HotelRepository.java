package com.example.demo.repository;

import com.example.demo.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByCiudadContainingIgnoreCase(String direccion);
    List<Hotel> findByNombreContainingIgnoreCase(String nombre);
    List<Hotel> findByCiudadContainingIgnoreCaseAndNombreContainingIgnoreCase(String direccion, String nombre);
}
