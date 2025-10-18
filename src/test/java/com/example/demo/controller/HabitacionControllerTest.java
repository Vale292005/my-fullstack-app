package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.HabitacionDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private HabitacionRepository habitacionRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Habitacion habitacion;
    @Autowired
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        habitacionRepository.deleteAll();
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setNombre("Admin");
        usuario.setEmail("admin@test.com");
        usuario.setContrasenha(passwordEncoder.encode("1234"));
        usuario.setEdad(LocalDate.of(1980, 1, 1));
        usuario.setTelefono("3000000000");
        usuario.setRol(Rol.ADMIN);
        usuario.setActivo(true);
        usuario = usuarioRepository.save(usuario);

        // Crear hotel asociado al usuario
        hotel = new Hotel();
        hotel.setNombre("Hotel Test");
        hotel.setUsuario(usuario);
        hotel.setDireccion("Calle falsa 123");
        hotel = hotelRepository.save(hotel);

        // (Opcional) Crear habitación para tests de edición/eliminación
        habitacion = new Habitacion();
        habitacion.setId(101l);
        habitacion.setPrecio(100.0);
        habitacion.setHotel(hotel);
        habitacion = habitacionRepository.save(habitacion);
    }


    @Test
    void listarPorHotel_DeberiaRetornarListaHabitaciones() throws Exception {
        // Debes asegurarte que exista una habitación en la base de datos test con hotelId = 1
        mockMvc.perform(get("/rooms/{hotelId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreHotel").exists());
    }

    @Test
    void crear_DeberiaCrearHabitacionYRetornarDto() throws Exception {
        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitacion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreHotel").value("Habitación A"))
                .andExpect(jsonPath("$.precio").value(100.0));
    }

    @Test
    void editar_DeberiaActualizarHabitacion() throws Exception {
        // Asegúrate de crear una habitación previamente con ID = 1L, o cambia este test para encadenar primero una creación
        mockMvc.perform(put("/rooms/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitacion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreHotel").value("Habitación A"));
    }

    @Test
    void eliminar_DeberiaEliminarHabitacion() throws Exception {
        // Asegúrate de tener una habitación con ID = 1L antes de ejecutar este test.
        mockMvc.perform(delete("/rooms/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}


