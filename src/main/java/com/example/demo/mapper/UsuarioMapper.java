package com.example.demo.mapper;

import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    // Entidad → DTO
    UsuarioDto toDto(Usuario usuario);

    // DTO → Entidad
    Usuario toEntity(UsuarioDto dto);
}

