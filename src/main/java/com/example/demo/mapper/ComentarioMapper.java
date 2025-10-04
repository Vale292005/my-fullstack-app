package com.example.demo.mapper;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ComentarioMapper {
    ComentarioMapper INSTANCE= Mappers.getMapper(ComentarioMapper.class);
    ReservaDto toDto(Reserva reserva);
    Reserva toEntity(ReservaDto reservaDto);
}
