package com.example.demo.repository;
import com.example.demo.Enum.Rol;
import com.example.demo.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNombre(String nombre);

    List<Usuario> findByRol(Rol rol);

    boolean existsByEmail(String email);
}