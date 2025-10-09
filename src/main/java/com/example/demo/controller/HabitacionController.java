package com.example.demo.controller;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.service.HabitacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class HabitacionController {

    private final HabitacionService service;

    public HabitacionController(HabitacionService service) {
        this.service = service;
    }

    // GET /rooms/{hotelId} -> Ver habitaciones de un hotel
    @GetMapping("/{hotelId}")
    public ResponseEntity<List<HabitacionDto>> listarPorHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(service.listarHabitacionesPorHotel(hotelId));
    }

    // POST /rooms -> Crear habitación
    @PostMapping
    public ResponseEntity<HabitacionDto> crear(@RequestBody HabitacionDto dto) {
        return ResponseEntity.status(201).body(service.crearHabitacion(dto));
    }

    // PUT /rooms/{id} -> Editar habitación
    @PutMapping("/{id}")
    public ResponseEntity<HabitacionDto> editar(@PathVariable Long id, @RequestBody HabitacionDto dto) {
        return ResponseEntity.ok(service.editarHabitacion(id, dto));
    }

    // DELETE /rooms/{id} -> Eliminar habitación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminarHabitacion(id);
        return ResponseEntity.noContent().build();
    }
}

