package com.example.demo.controller;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.service.HabitacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    // Crear habitación
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<HabitacionDto> crearHabitacion(
            @PathVariable Integer usuarioId,
            @RequestBody HabitacionDto habitacionDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(habitacionService.crearHabitacion(usuarioId, habitacionDto));
    }

    // Listar todas las habitaciones
    @GetMapping
    public ResponseEntity<List<HabitacionDto>> listarHabitaciones() {
        return ResponseEntity.ok(habitacionService.listarHabitaciones());
    }

    // Listar habitaciones de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HabitacionDto>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(habitacionService.listarHabitacionesPorUsuario(usuarioId));
    }

    // Buscar habitación por ID
    @GetMapping("/{habitacionId}")
    public ResponseEntity<HabitacionDto> buscarPorId(@PathVariable Integer habitacionId) {
        return ResponseEntity.ok(habitacionService.buscarPorId(habitacionId));
    }

    // Actualizar habitación
    @PutMapping("/{habitacionId}")
    public ResponseEntity<HabitacionDto> actualizarHabitacion(
            @PathVariable Integer habitacionId,
            @RequestBody HabitacionDto dto
    ) {
        return ResponseEntity.ok(habitacionService.actualizarHabitacion(habitacionId, dto));
    }

    // Eliminar habitación
    @DeleteMapping("/{habitacionId}")
    public ResponseEntity<Void> eliminarHabitacion(@PathVariable Integer habitacionId) {
        habitacionService.eliminarHabitacion(habitacionId);
        return ResponseEntity.noContent().build();
    }
}
