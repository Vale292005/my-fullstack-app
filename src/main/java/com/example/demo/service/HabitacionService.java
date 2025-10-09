package com.example.demo.service;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.mapper.HabitacionMapper;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final HotelRepository hotelRepository;
    private final HabitacionMapper mapper;

    public HabitacionService(HabitacionRepository habitacionRepository,
                             HotelRepository hotelRepository,
                             HabitacionMapper mapper) {
        this.habitacionRepository = habitacionRepository;
        this.hotelRepository = hotelRepository;
        this.mapper = mapper;
    }

    // Listar habitaciones de un hotel
    public List<HabitacionDto> listarHabitacionesPorHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        return habitacionRepository.findByHotel(hotel).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Crear habitación
    public HabitacionDto crearHabitacion(HabitacionDto dto) {
        Hotel hotel = hotelRepository.findById(dto.hotelId())
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));

        Habitacion habitacion = mapper.toEntity(dto);
        habitacion.setHotel(hotel);

        Habitacion guardada = habitacionRepository.save(habitacion);
        return mapper.toDto(guardada);
    }

    // Editar habitación
    public HabitacionDto editarHabitacion(Long id, HabitacionDto dto) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        habitacion.setNombreHotel(dto.nombreHotel());
        habitacion.setDireccion(dto.direccion());
        habitacion.setMangos(dto.mangos());
        habitacion.setPrecio(dto.precio());

        Habitacion actualizada = habitacionRepository.save(habitacion);
        return mapper.toDto(actualizada);
    }

    // Eliminar habitación
    public void eliminarHabitacion(Long id) {
        Habitacion habitacion = habitacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        habitacionRepository.delete(habitacion);
    }
}


