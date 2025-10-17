package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.DocumentosRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentosRepository documentosRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long usuarioId;

    @BeforeEach
    void setUp() {
        documentosRepository.deleteAll();
        usuarioRepository.deleteAll();
        // Limpiar las tablas antes de cada test
        documentosRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario anfitrión
        Usuario usuario = new Usuario();
        usuario.setNombre("Admin Test");
        usuario.setEmail("usuario@test.com");
        usuario.setContrasenha(passwordEncoder.encode("password"));
        usuario.setEdad(LocalDate.of(1990, 1, 1));
        usuario.setTelefono("3001234567");
        usuario.setRol(Rol.ANFITRION);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);
        this.usuarioId = usuario.getId();

        // Crear documentos asociados a ese usuario
        DocumentosHost documento = new DocumentosHost();
        documento.setEstado("PENDIENTE");
        documento.setAprobado(false);
        documento.setUsuario(usuario);
        documentosRepository.save(documento);
    }

    @Test
    @WithMockUser(username = "usuario@test.com", roles = "ANFITRION")
    void testSubirDocumentos() throws Exception {
        String json = """
                {
                    "documentoIdentidad": "archivo1.pdf",
                    "antecedentes": "archivo2.pdf"
                }
                """;

        mockMvc.perform(post("/anfitrion/documents/upload")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Documentos enviados para revisión"));
    }

    @Test
    @WithMockUser(username = "usuario@test.com", roles = "ANFITRION")
    void testVerEstadoValidacion() throws Exception {
        mockMvc.perform(get("/anfitrion/validation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAprobarSolicitud() throws Exception {
        mockMvc.perform(post("/anfitrion/validation/" + usuarioId + "/approve"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de anfitrión aprobada"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRechazarSolicitud() throws Exception {
        String observacion = "Faltan documentos";

        mockMvc.perform(post("/anfitrion/validation/" + usuarioId + "/reject")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(observacion))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de anfitrión rechazada"));
    }
}




