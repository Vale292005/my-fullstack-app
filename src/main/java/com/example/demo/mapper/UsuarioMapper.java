package com.example.demo.mapper;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.entity.Usuario;

public class UsuarioMapper {
    public static UsuarioDto toDto(Usuario u){
        UsuarioDto dto=new UsuarioDto();
        dto.setNombre(u.getNombre());
        dto.setTelefono(u.getTelefono());
        dto.setEmail(u.getEmail());
        dto.setEdad(u.getEdad());
        dto.setContrasenha((u.getContrasenha()));
        dto.setRol(u.getRol());
        return dto;
    }
    public static Usuario toEntity(UsuarioDto dto){
        Usuario usuario=new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setTelefono(dto.getTelefono());
        usuario.setEmail(dto.getEmail());
        usuario.setEdad(dto.getEdad());
        usuario.setContrasenha((dto.getContrasenha()));
        usuario.setRol(dto.getRol());
        return usuario;
    }
}
