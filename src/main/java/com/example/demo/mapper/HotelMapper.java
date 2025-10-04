package com.example.demo.mapper;

import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface HotelMapper {
    HotelMapper INSTANCE= Mappers.getMapper(HotelMapper.class);
    HotelDto toDto(Hotel hotel);
    Hotel toEntity(HotelDto hotelDto);
}
