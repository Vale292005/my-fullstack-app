package com.example.demo.repository;

import com.example.demo.entity.Comentario;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    List<Comentario> findByHabitacion(Habitacion habitacion);

    List<Comentario> findByHotel(Hotel hotel);

    List<Comentario> findByUsuario(Usuario usuario);
}
