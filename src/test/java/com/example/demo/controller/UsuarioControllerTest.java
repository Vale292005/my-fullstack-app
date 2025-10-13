package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void registrar_MayorDeEdad_deberiaCrearUsuario() throws Exception {
        UsuarioDto dto = new UsuarioDto(
                "Usuario",
                "test@example.com",
                "123456",
                LocalDate.now().minusYears(20),
                Rol.CLIENTE,
                true
        );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registrar_MenorDeEdad_deberiaDarConflict() throws Exception {
        UsuarioDto dto = new UsuarioDto(
                "Menor",
                "menor@example.com",
                "123456",
                LocalDate.now().minusYears(10),
                Rol.CLIENTE,
                true
        );

        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_Exitoso_deberiaDevolverToken() throws Exception {
        // Primero Registrar
        UsuarioDto dto = new UsuarioDto(
                "LoginUser",
                "login@example.com",
                "123456",
                LocalDate.now().minusYears(25),
                Rol.CLIENTE,
                true
        );
        mockMvc.perform(post("/usuarios/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        LoginRequestDto login = new LoginRequestDto("login@example.com", "123456");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
