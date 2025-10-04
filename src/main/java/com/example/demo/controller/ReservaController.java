package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // Crear reserva
    @PostMapping("/usuario/{usuarioId}/habitacion/{habitacionId}")
    public ResponseEntity<ReservaDto> crearReserva(
            @PathVariable Integer usuarioId,
            @PathVariable Long habitacionId,
            @RequestBody ReservaDto reservaDto
    ) {
        ReservaDto creado = reservaService.crearReserva(usuarioId, habitacionId, reservaDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Listar todas las reservas
    @GetMapping
    public ResponseEntity<List<ReservaDto>> listarReservas() {
        return ResponseEntity.ok(reservaService.listarReservas());
    }

    // Listar reservas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaDto>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(reservaService.listarReservasPorUsuario(usuarioId));
    }

    // Listar reservas por habitación
    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<ReservaDto>> listarPorHabitacion(@PathVariable Long habitacionId) {
        return ResponseEntity.ok(reservaService.listarReservasPorHabitacion(habitacionId));
    }

    // Buscar reserva por ID
    @GetMapping("/{reservaId}")
    public ResponseEntity<ReservaDto> buscarPorId(@PathVariable Long reservaId) {
        return ResponseEntity.ok(reservaService.buscarPorId(reservaId));
    }

    // Actualizar reserva
    @PutMapping("/{reservaId}")
    public ResponseEntity<ReservaDto> actualizarReserva(
            @PathVariable Long reservaId,
            @RequestBody ReservaDto reservaDto
    ) {
        return ResponseEntity.ok(reservaService.actualizarReserva(reservaId, reservaDto));
    }

    // Eliminar reserva
    @DeleteMapping("/{reservaId}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long reservaId) {
        reservaService.eliminarReserva(reservaId);
        return ResponseEntity.noContent().build();
    }
}

