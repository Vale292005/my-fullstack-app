package com.example.demo.repository;

import com.example.demo.Enum.Rol;
import com.example.demo.entity.Usuario;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;
@Repository

public class UsuarioRepository {
    private Map<Integer,Usuario> usuarios= new HashMap<>();
    private int idSecuencia=1;
    public List<Usuario> findAll(){
        return new ArrayList<>(usuarios.values());}

    public Optional<Usuario>findById(int id){
        return Optional.ofNullable(usuarios.get(id));
    }
    public Usuario save(Usuario usuario){
        if(usuario.getId()==null){
            usuario.setId(idSecuencia++);
        }
        usuarios.put(usuario.getId(),usuario);
        return usuario;
    }
    public Optional<Usuario> findByEmail(String email) {
        return usuarios.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public void delete(int id){
        usuarios.remove(id);
    }


    public Optional<Usuario> findByNombreUsuario(String nombre) {
        return usuarios.values().stream()
                .filter(u -> u.getNombre().equals(nombre))
                .findFirst();
    }

    public Optional<List<Usuario>> findByRol(Rol rol) {
        List<Usuario> usuariosConRol = usuarios.values().stream()
                .filter(usuario -> usuario.getRol() == rol)
                .collect(Collectors.toList());

        if (usuariosConRol.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(usuariosConRol);
        }
    }


}
