package com.example.demo.controller;

import com.example.demo.dto.HotelDto;
import com.example.demo.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hoteles")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // Crear hotel para un usuario
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<HotelDto> crearHotel(
            @PathVariable Integer usuarioId,
            @RequestBody HotelDto hotelDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hotelService.crearHotel(usuarioId, hotelDto));
    }

    // Listar todos los hoteles
    @GetMapping
    public ResponseEntity<List<HotelDto>> listarHoteles() {
        return ResponseEntity.ok(hotelService.listarHoteles());
    }

    // Listar hoteles de un usuario específico
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<HotelDto>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(hotelService.listarHotelPorUsuario(usuarioId));
    }

    // Buscar hotel por ID
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> buscarPorId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.buscarPorId(hotelId));
    }
}
