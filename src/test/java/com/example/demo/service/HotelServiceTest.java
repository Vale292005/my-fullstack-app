package com.example.demo.service;

import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import com.example.demo.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class HotelServiceTest {

    private HotelRepository hotelRepository;
    private HotelService hotelService;

    @BeforeEach
    void setUp() {
        hotelRepository = mock(HotelRepository.class);
        hotelService = new HotelService(hotelRepository);
    }

    @Test
    void buscarHoteles_sinParametros_devuelveTodos() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Hotel Test");
        hotel.setDireccion("Calle Falsa 123");

        when(hotelRepository.findAll()).thenReturn(List.of(hotel));

        List<HotelDto> result = hotelService.buscarHoteles(null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Hotel Test");
        verify(hotelRepository).findAll();
    }

    @Test
    void crearHotel_guardaHotelCorrectamente() {
        HotelDto dto = new HotelDto(1L, "Hotel Nuevo", "Calle 45", "Bonito", null);

        hotelService.crearHotel(dto);

        ArgumentCaptor<Hotel> captor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository).save(captor.capture());

        Hotel guardado = captor.getValue();
        assertThat(guardado.getNombre()).isEqualTo("Hotel Nuevo");
        assertThat(guardado.getDireccion()).isEqualTo("Calle 45");
    }

    @Test
    void editarHotel_conIdValido_actualizaHotel() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);
        hotel.setNombre("Viejo");

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        HotelDto dto = new HotelDto(1L, "Nuevo Nombre", "Nueva Dirección", "Actualizado", null);

        hotelService.editarHotel(1L, dto);

        assertThat(hotel.getNombre()).isEqualTo("Nuevo Nombre");
        verify(hotelRepository).save(hotel);
    }

    @Test
    void editarHotel_conIdInvalido_lanzaExcepcion() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        HotelDto dto = new HotelDto(1L, "Nuevo", "Dir", "Desc", null);

        assertThrows(RuntimeException.class, () -> hotelService.editarHotel(1L, dto));
    }

    @Test
    void eliminarHotel_existente_eliminaCorrectamente() {
        when(hotelRepository.existsById(1L)).thenReturn(true);

        hotelService.eliminarHotel(1L);

        verify(hotelRepository).deleteById(1L);
    }

    @Test
    void eliminarHotel_inexistente_lanzaExcepcion() {
        when(hotelRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> hotelService.eliminarHotel(1L));
    }
}
