package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ReservaControllerTest {

    private ReservaService reservaService;
    private ReservaController reservaController;

    @BeforeEach
    void setUp() {
        reservaService = mock(ReservaService.class);
        reservaController = new ReservaController(reservaService);
    }

    @Test
    void crearReserva_devuelveReservaCreada() {
        ReservaDto dto = new ReservaDto(
                1L, 1L, 2L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                200.0
        );

        when(reservaService.crearReserva(ArgumentMatchers.eq(dto))).thenReturn(dto);

        ResponseEntity<ReservaDto> response = reservaController.crearReserva(dto);

        verify(reservaService).crearReserva(dto);
        assertThat(response.getBody()).isEqualTo(dto);
    }

    @Test
    void verReservasPropias_devuelveLista() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("1");

        ReservaDto dto = new ReservaDto(
                10L, 1L, 2L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                100.0
        );
        when(reservaService.reservasPorUsuario(1L)).thenReturn(List.of(dto));

        ResponseEntity<List<ReservaDto>> response = reservaController.verReservasPropias(auth);

        verify(reservaService).reservasPorUsuario(1L);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void verTodasReservas_devuelveLista() {
        ReservaDto dto = new ReservaDto(
                1L, 1L, 2L,
                LocalDate.now(),
                LocalDate.now().plusDays(2),
                300.0
        );
        when(reservaService.todasReservas()).thenReturn(List.of(dto));

        ResponseEntity<List<ReservaDto>> response = reservaController.verTodasReservas();

        verify(reservaService).todasReservas();
        assertThat(response.getBody()).hasSize(1);
    }
}

