package com.example.demo.controller;

import com.example.demo.dto.ComentarioDto;
import com.example.demo.entity.Comentario;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.ComentarioRepository;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ComentarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private ComentarioRepository comentarioRepository;

    private Usuario usuario;
    private Hotel hotel;
    private Habitacion habitacion;

    @BeforeEach
    void setUp() {
        comentarioRepository.deleteAll();
        habitacionRepository.deleteAll();
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();

        // Crear usuario usando new y getters/setters
        usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@correo.com");
        usuario.setRol(com.example.demo.Enum.Rol.CLIENTE);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // Crear hotel usando new y getters/setters
        hotel = new Hotel();
        hotel.setNombre("Hotel Prueba");
        hotel.setDireccion("Calle Falsa 123");
        hotel.setDescripcion("Hotel de prueba");
        hotelRepository.save(hotel);

        // Crear habitación usando new y getters/setters
        habitacion = new Habitacion();
        habitacion.setNombreHotel("Habitación Deluxe");
        habitacion.setDireccion("Piso 1");
        habitacion.setHotel(hotel);
        habitacion.setPrecio(120.0);
        habitacionRepository.save(habitacion);
    }

    @Test
    void crearComentario_deberiaCrearComentario() throws Exception {
        // Configurar contexto de seguridad manualmente
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "test@correo.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Crear DTO usando constructor de record
        ComentarioDto dto = new ComentarioDto(
                null,
                usuario,
                hotel,
                habitacion,
                "Todo excelente",
                5,
                null
        );

        mockMvc.perform(post("/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Todo excelente"))
                .andExpect(jsonPath("$.calificacion").value(5));
    }

    @Test
    void verComentariosPorHabitacion_deberiaListarComentarios() throws Exception {
        // Guardar comentario usando new y getters/setters
        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setHotel(hotel);
        comentario.setHabitacion(habitacion);
        comentario.setMensaje("Muy buena habitación");
        comentario.setCalificacion(4);
        comentario.setFchCreacion(LocalDateTime.now());
        comentarioRepository.save(comentario);

        mockMvc.perform(get("/comentarios/habitacion/" + habitacion.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mensaje").value("Muy buena habitación"))
                .andExpect(jsonPath("$[0].calificacion").value(4));
    }

    @Test
    void eliminarComentario_deberiaEliminar() throws Exception {
        // Configurar contexto de seguridad manualmente
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "test@correo.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Crear comentario usando new y getters/setters
        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setHotel(hotel);
        comentario.setHabitacion(habitacion);
        comentario.setMensaje("Para eliminar");
        comentario.setCalificacion(3);
        comentario.setFchCreacion(LocalDateTime.now());
        comentarioRepository.save(comentario);

        Long id = comentario.getId();

        mockMvc.perform(delete("/comentarios/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario eliminado"));

        assertThat(comentarioRepository.findById(id)).isEmpty();
    }
}