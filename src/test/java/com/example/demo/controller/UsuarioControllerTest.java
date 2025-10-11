package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.AuthResponseDto;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.LoginRequestMapper;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AuthControllerTest {

    @Mock
    private UsuarioService usuarioService;
    @Mock
    private UsuarioMapper usuarioMapper;
    @Mock
    private LoginRequestMapper loginRequestMapper;

    @InjectMocks
    private AuthController authController;

    private UsuarioDto usuarioDto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioDto = new UsuarioDto(
                "Valeria",
                "12345",
                "valeria@example.com",
                LocalDate.of(2005,10,10),
                Rol.CLIENTE,
                true
        );
        usuario = new Usuario();
    }

    @Test
    void testRegister_MayorDeEdad() {
        when(usuarioService.esMayorDeEdad(usuarioDto.edad())).thenReturn(true);
        when(usuarioMapper.toEntity(usuarioDto)).thenReturn(usuario);
        when(usuarioService.crearUsuario(usuario)).thenReturn(usuario);

        ResponseEntity<?> response = authController.register(usuarioDto);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Usuario registrado. Verifique su correo.", response.getBody());
    }

    @Test
    void testRegister_MenorDeEdad() {
        when(usuarioService.esMayorDeEdad(usuarioDto.edad())).thenReturn(false);

        ResponseEntity<?> response = authController.register(usuarioDto);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Debe ser mayor de edad", response.getBody());
    }

    @Test
    void testLogin() {
        LoginRequestDto loginDto = new LoginRequestDto("valeria@example.com", "12345");
        when(usuarioService.login(loginDto)).thenReturn("fake-jwt-token");

        ResponseEntity<?> response = authController.login(loginDto);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthResponseDto);

        AuthResponseDto authResponse = (AuthResponseDto) response.getBody();
        assertEquals("fake-jwt-token", authResponse.token());
        assertEquals("Inicio de sesión exitoso", authResponse.mensaje());
    }

    @Test
    void testConfirmarCuenta() {
        doNothing().when(usuarioService).confirmarCuenta("valeria@example.com");

        ResponseEntity<?> response = authController.confirmar("valeria@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Cuenta confirmada correctamente", response.getBody());
    }

    @Test
    void testLogout_Success() {
        String token = "Bearer fake-token";
        doNothing().when(usuarioService).invalidarToken("fake-token");

        ResponseEntity<?> response = authController.logout(token);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Sesión cerrada correctamente", response.getBody());
    }

    @Test
    void testLogout_InvalidHeader() {
        ResponseEntity<?> response = authController.logout("InvalidToken");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Token no proporcionado", response.getBody());
    }

    @Test
    void testConfirmarEmail_TokenInvalido() {
        when(usuarioService.esTokenValido("bad-token")).thenReturn(false);

        String response = authController.confirmarEmail("bad-token");

        assertEquals("Token inválido ", response);
    }

    @Test
    void testConfirmarEmail_TokenValidoYActivado() {
        when(usuarioService.esTokenValido("good-token")).thenReturn(true);
        when(usuarioService.activarUsuarioPorToken("good-token")).thenReturn(true);

        String response = authController.confirmarEmail("good-token");

        assertEquals("Correo confirmado correctamente", response);
    }

    @Test
    void testConfirmarEmail_TokenValidoPeroNoActivado() {
        when(usuarioService.esTokenValido("token")).thenReturn(true);
        when(usuarioService.activarUsuarioPorToken("token")).thenReturn(false);

        String response = authController.confirmarEmail("token");

        assertEquals("La cuenta ya estaba activa o no se encontró el usuario.", response);
    }

    @Test
    void testForgotPassword_Success() {
        when(usuarioService.iniciarRecuperacionPassword("valeria@example.com")).thenReturn(true);

        ResponseEntity<String> response = authController.forgotPassword("valeria@example.com");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Se ha enviado un correo para recuperar tu contraseña.", response.getBody());
    }

    @Test
    void testForgotPassword_NoExisteCuenta() {
        when(usuarioService.iniciarRecuperacionPassword("noexiste@example.com")).thenReturn(false);

        ResponseEntity<String> response = authController.forgotPassword("noexiste@example.com");

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No existe una cuenta con ese correo.", response.getBody());
    }
}
