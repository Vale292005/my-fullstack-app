package com.example.demo.controller;

import com.example.demo.dto.DocumentosHostDto;
import com.example.demo.dto.EstadoValidacionDto;
import com.example.demo.service.DocumentosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/anfitrion")
@RequiredArgsConstructor
public class DocumentosController {
    private DocumentosService servicio;
    @PreAuthorize("hasRole('ANFITRION')")
    @PostMapping("/documents/upload")
    public ResponseEntity<?> subirDocumentos(@RequestBody DocumentosHostDto documentos,
                                             Authentication authentication) {
        String email = authentication.getName(); // email del usuario logueado
        servicio.subirDocumentos(email, documentos);
        return ResponseEntity.status(HttpStatus.CREATED).body("Documentos enviados para revisión");
    }
    //ver estado solicitud
    @PreAuthorize("hasRole('ANFITRION')")
    @GetMapping("/validation")
    public ResponseEntity<?> verEstadoValidacion(Authentication authentication) {
        String email = authentication.getName();
        EstadoValidacionDto estado = servicio.obtenerEstadoValidacion(email);
        return ResponseEntity.ok(estado);
    }
    //aprobar solicitus anfitrion
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/validation/{userId}/approve")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable int userId) {
        servicio.aprobarSolicitud(userId);
        return ResponseEntity.ok("Solicitud de anfitrión aprobada");
    }
    //rechazar solicitud de anftrion
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/validation/{userId}/reject")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable int userId,
                                               @RequestBody(required = false) String observacion) {
        servicio.rechazarSolicitud(userId, observacion);
        return ResponseEntity.ok("Solicitud de anfitrión rechazada");
    }

}
