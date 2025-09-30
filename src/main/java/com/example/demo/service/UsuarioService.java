package com.example.demo.service;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;

import java.util.Map;
import java.util.Optional;

public class UsuarioService {
    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return repository.findById(id);
    }
    public Usuario crearUsuario(Usuario usuario){
        Map.Entry<Integer,Usuario>entry=repository.findByNombreUsuario(usuario.getNombre());
        Optional<Usuario> existente= repository.findById(entry.getKey());
        if(existente.isPresent()){
            throw new IllegalArgumentException("El usuario ya existe");
        }return repository.save(usuario);
    }
    public void eliminarUsuario(Integer id){
        repository.delete(id);
    }
}
