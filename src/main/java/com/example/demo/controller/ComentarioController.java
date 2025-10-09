package com.example.demo.controller;

import com.example.demo.dto.ComentarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioDto> crearComentario(@RequestBody ComentarioDto dto) {
        ComentarioDto creado = comentarioService.crearComentario(dto);
        return ResponseEntity.ok(creado);
    }

    @GetMapping("/habitacion/{habitacionId}")
    public ResponseEntity<List<ComentarioDto>> verComentarios(@PathVariable Long habitacionId) {
        List<ComentarioDto> comentarios = comentarioService.verComentariosPorHabitacion(habitacionId);
        return ResponseEntity.ok(comentarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarComentario(@PathVariable Long id,
                                                     @AuthenticationPrincipal Usuario usuarioActual) {
        comentarioService.eliminarComentario(id, usuarioActual);
        return ResponseEntity.ok("Comentario eliminado");
    }
}

