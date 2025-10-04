package com.example.demo.controller;

import com.example.demo.dto.ComentarioDto;
import com.example.demo.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    // Crear comentario para un usuario
    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<ComentarioDto> crearComentario(
            @PathVariable Integer usuarioId,
            @RequestBody ComentarioDto comentarioDto
    ) {
        ComentarioDto creado = comentarioService.crearComentario(usuarioId, comentarioDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // Listar todos los comentarios
    @GetMapping
    public ResponseEntity<List<ComentarioDto>> listarComentarios() {
        return ResponseEntity.ok(comentarioService.listarComentarios());
    }

    // Listar comentarios por usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ComentarioDto>> listarPorUsuario(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(comentarioService.listarComentariosPorUsuario(usuarioId));
    }

    // Buscar comentario por ID
    @GetMapping("/{comentarioId}")
    public ResponseEntity<ComentarioDto> buscarPorId(@PathVariable Long comentarioId) {
        return ResponseEntity.ok(comentarioService.buscarPorId(comentarioId));
    }

    // Actualizar comentario
    @PutMapping("/{comentarioId}")
    public ResponseEntity<ComentarioDto> actualizarComentario(
            @PathVariable Long comentarioId,
            @RequestBody ComentarioDto dto
    ) {
        return ResponseEntity.ok(comentarioService.actualizarComentario(comentarioId, dto));
    }

    // Eliminar comentario
    @DeleteMapping("/{comentarioId}")
    public ResponseEntity<Void> eliminarComentario(@PathVariable Long comentarioId) {
        comentarioService.eliminarComentario(comentarioId);
        return ResponseEntity.noContent().build();
    }
}
