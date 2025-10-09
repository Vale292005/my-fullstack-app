package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.service.ReservaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Crear reserva
    @PostMapping
    public ResponseEntity<ReservaDto> crearReserva(@RequestBody ReservaDto dto) {
        ReservaDto reserva = reservaService.crearReserva(dto);
        return ResponseEntity.ok(reserva);
    }

    // Ver reservas propias (usa Authentication para obtener usuario actual)
    @GetMapping
    public ResponseEntity<List<ReservaDto>> verReservasPropias(Authentication auth) {
        Long usuarioId = Long.parseLong(auth.getName()); // se asume que el JWT tiene el ID del usuario en getName()
        List<ReservaDto> reservas = reservaService.reservasPorUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }

    // Ver todas las reservas (admin o anfitrion)
    @GetMapping("/all")
    public ResponseEntity<List<ReservaDto>> verTodasReservas() {
        List<ReservaDto> reservas = reservaService.todasReservas();
        return ResponseEntity.ok(reservas);
    }
}




