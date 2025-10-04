package com.example.demo.mapper;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ReservaMapper {
    ReservaMapper INSTANCE= Mappers.getMapper(ReservaMapper.class);
    ReservaDto toDto(Reserva reserva);
    Reserva toEntity(ReservaDto reservaDto);
}
