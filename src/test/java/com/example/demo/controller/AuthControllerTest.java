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
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock private UsuarioService usuarioService;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private LoginRequestMapper loginRequestMapper;

    @InjectMocks
    private AuthController authController;

    private UsuarioDto usuarioDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuarioDto = new UsuarioDto("Usuario", "test@example.com", "123", LocalDate.now().minusYears(20), Rol.CLIENTE,true);
    }

    @Test
    void testRegister_MayorDeEdad() {
        when(usuarioService.esMayorDeEdad(usuarioDto.edad())).thenReturn(true);
        when(usuarioMapper.toEntity(usuarioDto)).thenReturn(new Usuario());

        ResponseEntity<?> response = authController.register(usuarioDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(usuarioService).crearUsuario(any(Usuario.class));
    }

    @Test
    void testRegister_MenorDeEdad() {
        when(usuarioService.esMayorDeEdad(usuarioDto.edad())).thenReturn(false);

        ResponseEntity<?> response = authController.register(usuarioDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Debe ser mayor de edad", response.getBody());
    }

    @Test
    void testLogin_Exitoso() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password");
        when(usuarioService.login(dto)).thenReturn("jwt-token");

        ResponseEntity<?> response = authController.login(dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        AuthResponseDto body = (AuthResponseDto) response.getBody();
        assertEquals("jwt-token", body.token());
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
