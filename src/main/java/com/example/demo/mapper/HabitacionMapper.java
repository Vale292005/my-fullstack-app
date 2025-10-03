package com.example.demo.mapper;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.entity.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HabitacionMapper {
    HabitacionMapper INSTANCE= Mappers.getMapper(HabitacionMapper.class);
    HabitacionDto toDto(Habitacion habitacion);
    Habitacion toEntity(HabitacionDto habitacionDto);
}
