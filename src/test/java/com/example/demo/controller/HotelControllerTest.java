package com.example.demo.controller;

import com.example.demo.dto.HotelDto;
import com.example.demo.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HotelControllerTest {

    private HotelService hotelService;
    private HotelController hotelController;

    @BeforeEach
    void setUp() {
        hotelService = mock(HotelService.class);
        hotelController = new HotelController(hotelService);
    }

    @Test
    void listarHoteles_devuelveLista() {
        HotelDto dto = new HotelDto(1L, "Hotel Test", "Dir", "Desc", null);
        when(hotelService.buscarHoteles(null, null)).thenReturn(List.of(dto));

        ResponseEntity<List<HotelDto>> response = hotelController.listarHoteles(null, null);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).nombre()).isEqualTo("Hotel Test");
    }

    @Test
    void crearHotel_llamaServicio() {
        HotelDto dto = new HotelDto(1L, "Hotel Nuevo", "Dir", "Desc", null);

        ResponseEntity<String> response = hotelController.crearHotel(dto);

        verify(hotelService).crearHotel(ArgumentMatchers.eq(dto));
        assertThat(response.getBody()).isEqualTo("Hotel creado exitosamente");
    }

    @Test
    void editarHotel_llamaServicio() {
        HotelDto dto = new HotelDto(1L, "Hotel Editado", "Dir", "Desc", null);

        ResponseEntity<String> response = hotelController.editarHotel(1L, dto);

        verify(hotelService).editarHotel(1L, dto);
        assertThat(response.getBody()).isEqualTo("Hotel actualizado correctamente");
    }

    @Test
    void eliminarHotel_llamaServicio() {
        ResponseEntity<String> response = hotelController.eliminarHotel(1L);

        verify(hotelService).eliminarHotel(1L);
        assertThat(response.getBody()).isEqualTo("Hotel eliminado correctamente");
    }
}

