package com.example.demo.mapper;

import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.entity.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LoginRequestMapper {

    LoginRequestMapper INSTANCE = Mappers.getMapper(LoginRequestMapper.class);

    Usuario toEntity(LoginRequestDto dto);
}
