package com.example.demo.repository;

import com.example.demo.entity.Usuario;

import java.util.*;

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
    public Optional<Usuario>findByEmail(String email){return Optional.ofNullable(usuarios.get(email));}
    public void delete(int id){
        usuarios.remove(id);
    }

    public Map.Entry<Integer, Usuario> findByNombreUsuario(String value){
        for(Map.Entry<Integer,Usuario>entry :usuarios.entrySet()){
            if(entry.getValue().getNombre().equals(value)){
                return entry;
            }
        }return null;
    }

}
