package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.repository.DocumentosRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private EmailService emailService;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private DocumentosRepository documentosRepository;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@example.com");
        usuario.setNombre("Usuario");
        usuario.setContrasenha("password");
        usuario.setActivo(true);
        usuario.setRol(Rol.CLIENTE);
    }

    @Test
    void testListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));
        when(usuarioMapper.toDto(usuario)).thenReturn(new UsuarioDto("Usuario", "test@example.com", "123", LocalDate.now(),"245", Rol.CLIENTE,false ));

        List<UsuarioDto> result = usuarioService.listarUsuarios();

        assertEquals(1, result.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    void testCrearUsuario_Exitoso() {
        when(usuarioRepository.findByNombre("Usuario")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario nuevo = new Usuario();
        nuevo.setNombre("Usuario");
        nuevo.setContrasenha("12345");

        Usuario result = usuarioService.crearUsuario(nuevo);

        assertEquals("Usuario", result.getNombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testLogin_Exitoso() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "password");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password", "password")).thenReturn(true);
        when(jwtUtil.generarToken("test@example.com", Rol.CLIENTE)).thenReturn("jwt-token");

        String token = usuarioService.login(dto);

        assertEquals("jwt-token", token);
    }

    @Test
    void testLogin_FallaPorCredenciales() {
        LoginRequestDto dto = new LoginRequestDto("test@example.com", "wrong");
        when(usuarioRepository.findByEmail("test@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "password")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> usuarioService.login(dto));
    }

    @Test
    void testEsMayorDeEdad() {
        assertTrue(usuarioService.esMayorDeEdad(LocalDate.now().minusYears(18)));
        assertFalse(usuarioService.esMayorDeEdad(LocalDate.now().minusYears(17)));
    }

    @Test
    void testInvalidarYValidarToken() {
        String token = "abc123";
        usuarioService.invalidarToken(token);
        assertFalse(usuarioService.esTokenValido(token));
    }
}

