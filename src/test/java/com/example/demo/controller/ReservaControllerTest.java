package com.example.demo.controller;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    private Long usuarioId;

    @BeforeEach
    void setUp() throws Exception {
        reservaRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNombre("Usuario Test");
        usuario = usuarioRepository.save(usuario);
        usuarioId = usuario.getId();
    }

    @Test
    void crearReserva_deberiaCrearYDevolverReserva() throws Exception {
        String json = String.format("""
            {
              "id": null,
              "hotelId": 1,
              "habitacionId": 1,
              "usuarioId": %d,
              "fechaEntrada": "%s",
              "fechaSalida": "%s",
              "precio": 200.0
            }
        """, usuarioId,
                LocalDate.now().plusDays(1).toString(),
                LocalDate.now().plusDays(3).toString());

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuarioId").value(usuarioId));
    }

    @Test
    void verReservasPropias_deberiaListarCorrectamente() throws Exception {
        // Primero crea una reserva para ese usuario
        crearReserva_deberiaCrearYDevolverReserva();

        mockMvc.perform(get("/reservas/propias")
                        .param("usuarioId", usuarioId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].usuarioId").value(usuarioId));
    }

    @Test
    void verTodasReservas_deberiaRetornarLista() throws Exception {
        // Crear una reserva
        crearReserva_deberiaCrearYDevolverReserva();

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].precio").value(200.0));
    }
}


