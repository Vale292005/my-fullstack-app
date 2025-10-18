package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.AuthResponseDto;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 👈 para usar TestSecurityConfig
@Transactional
class AuthControllerTest {

    @Autowired
    private AuthController authController;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @BeforeEach
    void setUp() {
        // Limpiar la tabla antes de cada prueba
        usuarioRepository.deleteAll();

        Usuario user = new Usuario();
        user.setNombre("UsuarioLogi");
        user.setEmail("login@example.com");
        user.setTelefono("123");
        user.setEdad(LocalDate.now().minusYears(25));
        user.setContrasenha(passwordEncoder.encode("123"));
        user.setRol(Rol.CLIENTE);
        user.setActivo(true);
        usuarioRepository.save(user);
    }

    @Test
    void testRegister_MayorDeEdad() {
        UsuarioDto usuarioDto = new UsuarioDto(
                "UsuarioLogin",
                "login@example.com",
                "123",
                LocalDate.now().minusYears(25),
                "123",
                Rol.CLIENTE,
                true
        );


        ResponseEntity<?> response = authController.register(usuarioDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testRegister_MenorDeEdad() {
        UsuarioDto menor = new UsuarioDto(
                "UsuarioMenor",
                "menor@example.com",
                "123",
                LocalDate.now().minusYears(10),
                "321",
                Rol.CLIENTE,
                true
        );

        ResponseEntity<?> response = authController.register(menor);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe ser mayor de edad", response.getBody());
    }

    @Test
    void testLogin_Exitoso() {
        // Registrar un usuario antes de loguear
        UsuarioDto usuarioDto = new UsuarioDto(
                "UsuarioLogin",
                "login@example.com",
                "123",
                LocalDate.now().minusYears(25),
                "456",
                Rol.CLIENTE,
                true
        );
        authController.register(usuarioDto);

        LoginRequestDto loginDto = new LoginRequestDto("login@example.com", "123");
        ResponseEntity<?> response = authController.login(loginDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponseDto);
        AuthResponseDto authResponse = (AuthResponseDto) response.getBody();
        assertNotNull(authResponse.token());
    }

    @Test
    void testLogout_ConTokenValido() {
        String token = "Bearer jwt-token";
        ResponseEntity<?> response = authController.logout(token);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testLogout_SinToken() {
        ResponseEntity<?> response = authController.logout(null);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}



