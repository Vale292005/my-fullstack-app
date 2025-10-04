package com.example.demo.repository;

import com.example.demo.entity.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario,Long> {
    List<Comentario> findByUsuario_Id(Integer usuarioId);
}
