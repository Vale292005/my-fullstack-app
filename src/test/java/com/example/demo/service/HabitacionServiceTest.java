package com.example.demo.service;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.mapper.HabitacionMapper;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HabitacionServiceTest {

    @Mock
    private HabitacionRepository habitacionRepository;
    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private HabitacionMapper mapper;

    @InjectMocks
    private HabitacionService habitacionService;

    private Hotel hotel;
    private Habitacion habitacion;
    private HabitacionDto dto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");

        habitacion = new Habitacion();
        habitacion.setId(1L);
        habitacion.setNombreHotel("Habitación A");
        habitacion.setDireccion("Calle Falsa 123");
        habitacion.setMangos(2);
        habitacion.setPrecio(100.0);

        dto = new HabitacionDto(
                1L,
                "Habitación A",
                "Calle Falsa 123",
                2,1L,
                100.0

        );
    }

    @Test
    void listarHabitacionesPorHotel_DeberiaRetornarListaDto() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(habitacionRepository.findByHotel(hotel)).thenReturn(List.of(habitacion));
        when(mapper.toDto(habitacion)).thenReturn(dto);

        List<HabitacionDto> resultado = habitacionService.listarHabitacionesPorHotel(1L);

        assertEquals(1, resultado.size());
        assertEquals("Habitación A", resultado.get(0).nombreHotel());
        verify(hotelRepository).findById(1L);
        verify(habitacionRepository).findByHotel(hotel);
    }

    @Test
    void crearHabitacion_DeberiaGuardarYRetornarDto() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(mapper.toEntity(dto)).thenReturn(habitacion);
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);
        when(mapper.toDto(habitacion)).thenReturn(dto);

        HabitacionDto resultado = habitacionService.crearHabitacion(dto);

        assertEquals("Habitación A", resultado.nombreHotel());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void editarHabitacion_DeberiaActualizarYRetornarDto() {
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));
        when(habitacionRepository.save(any(Habitacion.class))).thenReturn(habitacion);
        when(mapper.toDto(habitacion)).thenReturn(dto);

        HabitacionDto resultado = habitacionService.editarHabitacion(1L, dto);

        assertEquals("Habitación A", resultado.nombreHotel());
        verify(habitacionRepository).save(habitacion);
    }

    @Test
    void eliminarHabitacion_DeberiaEliminarHabitacion() {
        when(habitacionRepository.findById(1L)).thenReturn(Optional.of(habitacion));

        habitacionService.eliminarHabitacion(1L);

        verify(habitacionRepository).delete(habitacion);
    }
}




