package com.example.demo.mapper;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    // Entidad → DTO (ignora la contraseña)
    @Mapping(target = "rol", source = "rol")
    UsuarioDto toDto(Usuario usuario);

    // DTO → Entidad (deja la contraseña como null)
    @Mapping(target = "contrasenha", ignore = true)
    Usuario toEntity(UsuarioDto dto);
}

