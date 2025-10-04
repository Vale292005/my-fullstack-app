package com.example.demo.service;

import com.example.demo.dto.ComentarioDto;
import com.example.demo.entity.Comentario;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.ComentarioMapper;
import com.example.demo.repository.ComentarioRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComentarioMapper comentarioMapper;
    // Crear comentario
    public ComentarioDto crearComentario(Integer usuarioId, ComentarioDto dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comentario comentario = comentarioMapper.toEntity(dto);
        comentario.setUsuario(usuario);

        Comentario guardado = comentarioRepository.save(comentario);
        return comentarioMapper.toDto(guardado);
    }

    // Listar todos los comentarios
    public List<ComentarioDto> listarComentarios() {
        return comentarioRepository.findAll()
                .stream()
                .map(comentarioMapper::toDto)
                .collect(Collectors.toList());
    }

    // Listar comentarios por usuario
    public List<ComentarioDto> listarComentariosPorUsuario(Integer usuarioId) {
        return comentarioRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(comentarioMapper::toDto)
                .collect(Collectors.toList());
    }

    // Buscar comentario por ID
    public ComentarioDto buscarPorId(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
        return comentarioMapper.toDto(comentario);
    }

    // Actualizar comentario
    public ComentarioDto actualizarComentario(Long comentarioId, ComentarioDto dto) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        Comentario actualizado = comentarioMapper.toEntity(dto);
        actualizado.setId(comentario.getId());
        actualizado.setUsuario(comentario.getUsuario());

        Comentario guardado = comentarioRepository.save(actualizado);
        return comentarioMapper.toDto(guardado);
    }

    // Eliminar comentario
    public void eliminarComentario(Long comentarioId) {
        if (!comentarioRepository.existsById(comentarioId)) {
            throw new RuntimeException("Comentario no encontrado");
        }
        comentarioRepository.deleteById(comentarioId);
    }
}
