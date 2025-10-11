package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.HotelService;
import com.example.demo.service.ReservaService;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private HotelService hotelService;
    @Mock
    private HabitacionService habitacionService;
    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------------- USUARIOS ----------------
    @Test
    void testEliminarUsuario() {
        Long id = 1L;

        ResponseEntity<String> response = adminController.eliminarUsuario(id);

        verify(usuarioService).eliminarUsuario(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado", response.getBody());
    }

    // ---------------- HOTELES ----------------
    @Test
    void testEliminarHotel() {
        Long id = 1L;

        ResponseEntity<String> response = adminController.eliminarHotel(id);

        verify(hotelService).eliminarHotel(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hotel eliminado", response.getBody());
    }

    // ---------------- HABITACIONES ----------------
    @Test
    void testEliminarHabitacion() {
        Long id = 1L;

        ResponseEntity<String> response = adminController.eliminarHabitacion(id);

        verify(habitacionService).eliminarHabitacion(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Habitación eliminada", response.getBody());
    }

    // ---------------- RESERVAS ----------------
    @Test
    void testCrearReservaAdmin() {
        Long id = 1L;
        ReservaDto dto = new ReservaDto(
                1L, 2L, 3L,
                LocalDate.of(2025, 10, 10),
                LocalDate.of(2025, 10, 15),
                500.0
        );

        ResponseEntity<String> response = adminController.crearReservaAdmin(id, dto);

        verify(reservaService).crearReservaAdmin(id, dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Reserva creada por administrador", response.getBody());
    }

    @Test
    void testEliminarReservaAdmin() {
        Long id = 1L;

        ResponseEntity<String> response = adminController.eliminarReservaAdmin(id);

        verify(reservaService).eliminarReserva(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva eliminada por administrador", response.getBody());
    }

    // ---------------- HOSTS ----------------
    @Test
    void testRevisarSolicitudesHosts() {
        DocumentosHost doc = new DocumentosHost();
        when(usuarioService.listarDocumentosPendientes()).thenReturn(List.of(doc));

        ResponseEntity<List<DocumentosHost>> response = adminController.revisarSolicitudesHosts();

        verify(usuarioService).listarDocumentosPendientes();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testAprobarSolicitud() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.aprobarSolicitud(userId);

        verify(usuarioService).aprobarSolicitud(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitud aprobada", response.getBody());
    }

    @Test
    void testRechazarSolicitud() {
        Long userId = 1L;

        ResponseEntity<String> response = adminController.rechazarSolicitud(userId);

        verify(usuarioService).rechazarSolicitud(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitud rechazada", response.getBody());
    }
}
