package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.DocumentosHostDto;
import com.example.demo.dto.EstadoValidacionDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.DocumentosRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentosService {
    private final UsuarioRepository usuarioRepository;
    private final DocumentosRepository documentosRepository;
    public void subirDocumentos(String email, DocumentosHostDto documentos) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DocumentosHost docs = new DocumentosHost(usuario, documentos);
        documentosRepository.save(docs);
    }

    public EstadoValidacionDto obtenerEstadoValidacion(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DocumentosHost docs = documentosRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay documentos cargados"));

        return new EstadoValidacionDto(
                docs.isAprobado(),
                docs.getEstado(),
                docs.getObservaciones()
        );
    }

    public void aprobarSolicitud(int userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DocumentosHost docs = documentosRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay documentos cargados"));

        docs.setAprobado(true);
        docs.setEstado("APROBADO");
        usuario.setRol(Rol.ANFITRION);
        usuarioRepository.save(usuario);
        documentosRepository.save(docs);
    }

    public void rechazarSolicitud(int userId, String observacion) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        DocumentosHost docs = documentosRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay documentos cargados"));

        docs.setAprobado(false);
        docs.setEstado("RECHAZADO");
        docs.setObservaciones(observacion);
        documentosRepository.save(docs);
    }
}


