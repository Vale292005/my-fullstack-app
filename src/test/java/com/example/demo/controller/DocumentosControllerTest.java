package com.example.demo.controller;

import com.example.demo.dto.DocumentosHostDto;
import com.example.demo.dto.EstadoValidacionDto;
import com.example.demo.service.DocumentosService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DocumentosController.class)
@Import(DocumentosControllerTest.MockConfig.class)
class DocumentosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentosService documentosService;  // mock inyectado manualmente

    @Autowired
    private Authentication authentication;

    static class MockConfig {
        @Bean
        DocumentosService documentosService() {
            return Mockito.mock(DocumentosService.class);
        }

        @Bean
        Authentication authentication() {
            return Mockito.mock(Authentication.class);
        }
    }

    @Test
    @WithMockUser(roles = "ANFITRION")
    void testSubirDocumentos() throws Exception {
        String json = """
                {
                    "documentoIdentidad": "archivo1.pdf",
                    "antecedentes": "archivo2.pdf"
                }
                """;

        Mockito.when(authentication.getName()).thenReturn("usuario@test.com");

        mockMvc.perform(post("/anfitrion/documents/upload")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(content().string("Documentos enviados para revisión"));

        Mockito.verify(documentosService)
                .subirDocumentos(eq("usuario@test.com"), any(DocumentosHostDto.class));
    }

    @Test
    @WithMockUser(roles = "ANFITRION")
    void testVerEstadoValidacion() throws Exception {
        EstadoValidacionDto estado = new EstadoValidacionDto(true, "APROBADO", "Todo correcto");

        Mockito.when(authentication.getName()).thenReturn("usuario@test.com");
        Mockito.when(documentosService.obtenerEstadoValidacion("usuario@test.com"))
                .thenReturn(estado);

        mockMvc.perform(get("/anfitrion/validation")
                        .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aprobado").value(true))
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.observaciones").value("Todo correcto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAprobarSolicitud() throws Exception {
        mockMvc.perform(post("/anfitrion/validation/1/approve"))
                .andExpect(status().isOk())
                .andExpect(content().string("Solicitud de anfitrión aprobada"));

        Mockito.verify(documentosService).aprobarSolicitud(1);
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

        Mockito.verify(documentosService).rechazarSolicitud(1, observacion);
    }
}


