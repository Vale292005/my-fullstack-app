package com.example.demo.controller;

import com.example.demo.Enum.Rol;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // ⬅️ Desactiva filtros de security
@ActiveProfiles("test")
@Transactional // ⬅️ Rollback automático después de cada test
class ComentarioControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private ComentarioRepository comentarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Hotel hotel;
    private Habitacion habitacion;

    @BeforeEach
    void setUp() {
        // Limpia en orden inverso por las foreign keys
        comentarioRepository.deleteAll();
        habitacionRepository.deleteAll();
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crea usuario con contraseña encriptada
        usuario = new Usuario();
        usuario.setNombre("Test User");
        usuario.setEmail("test@correo.com");
        usuario.setContrasenha(passwordEncoder.encode("password123")); // ⬅️ ENCRIPTA
        usuario.setEdad(LocalDate.of(1990, 1, 1));
        usuario.setTelefono("3001234567");
        usuario.setRol(Rol.CLIENTE);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        Usuario hostUsuario = new Usuario();
        hostUsuario.setNombre("Host User");
        hostUsuario.setEmail("host@correo.com");
        hostUsuario.setContrasenha(passwordEncoder.encode("password123"));
        hostUsuario.setEdad(LocalDate.of(1985, 1, 1));
        hostUsuario.setTelefono("3009999999");
        hostUsuario.setRol(Rol.ANFITRION);
        hostUsuario.setActivo(true);
        hostUsuario = usuarioRepository.save(hostUsuario);

        // Crea hotel
        hotel = new Hotel();
        hotel.setNombre("Hotel Prueba");
        hotel.setDireccion("Calle Falsa 123");
        hotel.setDescripcion("Hotel de prueba");
        hotel.setUsuario(hostUsuario);
        hotel = hotelRepository.save(hotel);

        // Crea habitación
        habitacion = new Habitacion();
        habitacion.setNombreHotel("Habitación Deluxe");
        habitacion.setDireccion("Piso 1");
        habitacion.setHotel(hotel);
        habitacion.setPrecio(120.0);
        habitacion = habitacionRepository.save(habitacion);
    }

    @Test
    void crearComentario_deberiaCrearComentario() throws Exception {
        // NO envíes objetos completos, solo IDs
        String comentarioJson = String.format("""
        {
            "usuarioId": %d,
            "hotelId": %d,
            "habitacionId": %d,
            "mensaje": "Todo excelente",
            "calificacion": 5
        }
        """, usuario.getId(), hotel.getId(), habitacion.getId());

        mockMvc.perform(post("/comentarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(comentarioJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Todo excelente"));
    }

    @Test
    void verComentariosPorHabitacion_deberiaListarComentarios() throws Exception {
        // Crea comentario directamente en BD
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
        // Crea comentario
        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setHotel(hotel);
        comentario.setHabitacion(habitacion);
        comentario.setMensaje("Para eliminar");
        comentario.setCalificacion(3);
        comentario.setFchCreacion(LocalDateTime.now());
        comentario = comentarioRepository.save(comentario);

        Long comentarioId = comentario.getId();

        mockMvc.perform(delete("/comentarios/" + comentarioId))
                .andExpect(status().isOk())
                .andExpect(content().string("Comentario eliminado"));

        // Verifica que fue eliminado
        assertThat(comentarioRepository.findById(comentarioId)).isEmpty();
    }
}