package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.DocumentosHostDto;
import com.example.demo.dto.EstadoValidacionDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.DocumentosRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class DocumentosServiceTest {

    private UsuarioRepository usuarioRepository;
    private DocumentosRepository documentosRepository;
    private DocumentosService documentosService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        documentosRepository = mock(DocumentosRepository.class);
        documentosService = new DocumentosService(usuarioRepository, documentosRepository);
    }

    @Test
    void subirDocumentos_guardaDocumentosCorrectamente() {
        String email = "test@mail.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        DocumentosHostDto dto = new DocumentosHostDto("cedula.pdf", "selfie.png", "certificado.pdf");

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        documentosService.subirDocumentos(email, dto);

        verify(documentosRepository).save(any(DocumentosHost.class));
    }

    @Test
    void subirDocumentos_usuarioNoEncontrado_lanzaExcepcion() {
        when(usuarioRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> documentosService.subirDocumentos("x@mail.com", new DocumentosHostDto("a", "b", "c")));
    }

    @Test
    void obtenerEstadoValidacion_devuelveEstadoCorrecto() {
        String email = "mail@mail.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        DocumentosHost docs = new DocumentosHost();
        docs.setAprobado(true);
        docs.setEstado("APROBADO");
        docs.setObservaciones("Todo bien");

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));
        when(documentosRepository.findByUsuario(usuario)).thenReturn(Optional.of(docs));

        EstadoValidacionDto result = documentosService.obtenerEstadoValidacion(email);

        assertThat(result.aprobado()).isTrue();
        assertThat(result.estado()).isEqualTo("APROBADO");
        assertThat(result.observaciones()).isEqualTo("Todo bien");
    }

    @Test
    void obtenerEstadoValidacion_sinDocs_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setEmail("mail@mail.com");

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));
        when(documentosRepository.findByUsuario(usuario)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> documentosService.obtenerEstadoValidacion(usuario.getEmail()));
    }

    @Test
    void aprobarSolicitud_actualizaRolYEstado() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setRol(Rol.CLIENTE);

        DocumentosHost docs = new DocumentosHost();
        docs.setAprobado(false);
        docs.setEstado("PENDIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(documentosRepository.findByUsuario(usuario)).thenReturn(Optional.of(docs));

        documentosService.aprobarSolicitud(1);

        verify(usuarioRepository).save(usuario);
        verify(documentosRepository).save(docs);
        assertThat(usuario.getRol()).isEqualTo(Rol.ANFITRION);
        assertThat(docs.isAprobado()).isTrue();
        assertThat(docs.getEstado()).isEqualTo("APROBADO");
    }

    @Test
    void rechazarSolicitud_actualizaEstadoYObservacion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        DocumentosHost docs = new DocumentosHost();
        docs.setAprobado(true);
        docs.setEstado("PENDIENTE");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(documentosRepository.findByUsuario(usuario)).thenReturn(Optional.of(docs));

        documentosService.rechazarSolicitud(1, "Documentos ilegibles");

        verify(documentosRepository).save(docs);
        assertThat(docs.isAprobado()).isFalse();
        assertThat(docs.getEstado()).isEqualTo("RECHAZADO");
        assertThat(docs.getObservaciones()).isEqualTo("Documentos ilegibles");
    }
}
