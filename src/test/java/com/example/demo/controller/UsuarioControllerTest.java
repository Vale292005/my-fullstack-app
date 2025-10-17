package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
    }

    @Test
    void registrar_MayorDeEdad_deberiaCrearUsuario() throws Exception {
        // Email único para evitar conflictos
        String emailUnico = "test" + System.currentTimeMillis() + "@example.com";

        UsuarioDto dto = new UsuarioDto(
                "Usuario Test",
                emailUnico,
                "Password123!",
                LocalDate.of(1990, 1, 1), // Mayor de edad (35 años)
                Rol.CLIENTE,
                true
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(emailUnico))
                .andExpect(jsonPath("$.nombre").value("Usuario Test"));
    }

    @Test
    void login_Exitoso_deberiaDevolverToken() throws Exception {
        // PASO 1: Crea usuario en BD
        Usuario usuario = new Usuario();
        usuario.setNombre("Login User");
        usuario.setEmail("login@example.com");
        usuario.setContrasenha(passwordEncoder.encode("Password123!"));
        usuario.setEdad(LocalDate.of(1990, 5, 15));
        usuario.setTelefono("3001234567");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // PASO 2: Intenta login
        LoginRequestDto login = new LoginRequestDto(
                "login@example.com",
                "Password123!"
        );


        mockMvc.perform(post("/auth/login") //
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void login_Fallido_credencialesIncorrectas() throws Exception {
        // Crea usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@example.com");
        usuario.setContrasenha(passwordEncoder.encode("CorrectPassword"));
        usuario.setEdad(LocalDate.of(1990, 1, 1));
        usuario.setTelefono("3001234567");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // Login con contraseña incorrecta
        LoginRequestDto login = new LoginRequestDto(
                "test@example.com",
                "WrongPassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
