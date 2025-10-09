package com.example.demo.repository;


import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentosRepository extends JpaRepository<DocumentosHost, Long> {

    // Buscar documentos por usuario
    Optional<DocumentosHost> findByUsuario(Usuario usuario);

    // Verificar si ya existen documentos para un usuario
    boolean existsByUsuario(Usuario usuario);
}
