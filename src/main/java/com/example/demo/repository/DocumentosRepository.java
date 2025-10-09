package com.example.demo.repository;


import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface DocumentosRepository extends JpaRepository<DocumentosHost, Long> {

    // Buscar documentos por usuario
    Optional<DocumentosHost> findByUsuario(Usuario usuario);

    // Verificar si ya existen documentos para un usuario
    boolean existsByUsuario(Usuario usuario);
}
