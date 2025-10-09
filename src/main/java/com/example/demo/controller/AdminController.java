package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.service.HabitacionService;
import com.example.demo.service.HotelService;
import com.example.demo.service.ReservaService;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final HotelService hotelService;
    private final HabitacionService habitacionService;
    private final ReservaService reservaService;

    public AdminController(UsuarioService usuarioService,
                           HotelService hotelService,
                           HabitacionService habitacionService,
                           ReservaService reservaService) {
        this.usuarioService = usuarioService;
        this.hotelService = hotelService;
        this.habitacionService = habitacionService;
        this.reservaService = reservaService;
    }

    // ---------------- USUARIOS ----------------
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok("Usuario eliminado");
    }

    // ---------------- HOTELES ----------------
    @DeleteMapping("/hotels/{id}")
    public ResponseEntity<String> eliminarHotel(@PathVariable Long id) {
        hotelService.eliminarHotel(id);
        return ResponseEntity.ok("Hotel eliminado");
    }

    // ---------------- HABITACIONES ----------------
    @DeleteMapping("/rooms/{id}")
    public ResponseEntity<String> eliminarHabitacion(@PathVariable Long id) {
        habitacionService.eliminarHabitacion(id);
        return ResponseEntity.ok("Habitación eliminada");
    }

    // ---------------- RESERVAS ----------------
    @PostMapping("/bookings/{id}")
    public ResponseEntity<String> crearReservaAdmin(@PathVariable Long id, @RequestBody ReservaDto dto) {
        reservaService.crearReservaAdmin(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Reserva creada por administrador");
    }

    @DeleteMapping("/bookings/{id}")
    public ResponseEntity<String> eliminarReservaAdmin(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.ok("Reserva eliminada por administrador");
    }

    // solicitud anfitrion
    @GetMapping("/hosts/validation")
    public ResponseEntity<List<DocumentosHost>> revisarSolicitudesHosts() {
        // UsuarioService tiene el método que devuelve los documentos pendientes
        List<DocumentosHost> solicitudes = usuarioService.listarDocumentosPendientes();
        return ResponseEntity.ok(solicitudes);
    }

    @PostMapping("/hosts/validation/{userId}/approve")
    public ResponseEntity<String> aprobarSolicitud(@PathVariable Long userId) {
        usuarioService.aprobarSolicitud(userId);
        return ResponseEntity.ok("Solicitud aprobada");
    }

    @PostMapping("/hosts/validation/{userId}/reject")
    public ResponseEntity<String> rechazarSolicitud(@PathVariable Long userId) {
        usuarioService.rechazarSolicitud(userId);
        return ResponseEntity.ok("Solicitud rechazada");
    }
}
