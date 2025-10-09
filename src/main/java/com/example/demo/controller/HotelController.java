package com.example.demo.controller;

import com.example.demo.dto.HotelDto;
import com.example.demo.service.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // 🟢 GET /hotels — Buscar hoteles
    @GetMapping
    public ResponseEntity<List<HotelDto>> listarHoteles(
            @RequestParam(required = false) String ciudad,
            @RequestParam(required = false) String nombre
    ) {
        List<HotelDto> hoteles = hotelService.buscarHoteles(ciudad, nombre);
        return ResponseEntity.ok(hoteles);
    }

    // 🟡 POST /hotels — Crear hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @PostMapping
    public ResponseEntity<String> crearHotel(@RequestBody HotelDto dto) {
        hotelService.crearHotel(dto);
        return ResponseEntity.ok("Hotel creado exitosamente");
    }

    // 🟠 PUT /hotels/{id} — Editar hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> editarHotel(@PathVariable Long id, @RequestBody HotelDto dto) {
        hotelService.editarHotel(id, dto);
        return ResponseEntity.ok("Hotel actualizado correctamente");
    }

    //  DELETE /hotels/{id} — Eliminar hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHotel(@PathVariable Long id) {
        hotelService.eliminarHotel(id);
        return ResponseEntity.ok("Hotel eliminado correctamente");
    }
}

