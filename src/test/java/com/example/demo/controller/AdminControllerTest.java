package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Usa application-test.properties
@Transactional
class AdminControllerTest {

    @Autowired
    private AdminController adminController;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    void testEliminarUsuario() {
        Long id = 1L;
        ResponseEntity<String> response = adminController.eliminarUsuario(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Usuario eliminado", response.getBody());
    }

    @Test
    void testEliminarHotel() {
        Long id = 1L;
        ResponseEntity<String> response = adminController.eliminarHotel(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Hotel eliminado", response.getBody());
    }

    @Test
    void testEliminarHabitacion() {
        Long id = 1L;
        ResponseEntity<String> response = adminController.eliminarHabitacion(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Habitación eliminada", response.getBody());
    }

    @Test
    void testCrearReservaAdmin() {
        Long adminId = 1L;

        // ⚠️ Asegúrate de que estos IDs existen en tu base de datos de test
        ReservaDto dto = new ReservaDto(
                1L, // idHotel
                1L, // idHabitacion
                1L, // idUsuario
                LocalDate.of(2025, 10, 10),
                LocalDate.of(2025, 10, 15),
                500.0
        );

        ResponseEntity<String> response = adminController.crearReservaAdmin(adminId, dto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Reserva creada por administrador", response.getBody());
    }

    @Test
    void testEliminarReservaAdmin() {
        Long reservaId = 1L;
        ResponseEntity<String> response = adminController.eliminarReservaAdmin(reservaId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Reserva eliminada por administrador", response.getBody());
    }

    @Test
    void testRevisarSolicitudesHosts() {
        ResponseEntity<List<DocumentosHost>> response = adminController.revisarSolicitudesHosts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAprobarSolicitud() {
        Long userId = 1L;
        ResponseEntity<String> response = adminController.aprobarSolicitud(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitud aprobada", response.getBody());
    }

    @Test
    void testRechazarSolicitud() {
        Long userId = 2L;
        ResponseEntity<String> response = adminController.rechazarSolicitud(userId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Solicitud rechazada", response.getBody());
    }
}

