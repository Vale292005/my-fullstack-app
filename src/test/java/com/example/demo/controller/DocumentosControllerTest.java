package com.example.demo.controller;

import com.example.demo.service.DocumentosService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DocumentosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentosService documentosService;

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
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.observaciones").exists());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAprobarSolicitud() throws Exception {
        mockMvc.perform(post("/anfitrion/validation/1/approve"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de anfitrión aprobada"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRechazarSolicitud() throws Exception {
        String observacion = "Faltan documentos";

        mockMvc.perform(post("/anfitrion/validation/1/reject")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(observacion))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de anfitrión rechazada"));
    }
}



