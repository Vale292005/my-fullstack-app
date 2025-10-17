package com.example.demo.controller;

import com.example.demo.dto.HabitacionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

    private HabitacionDto dto;

    @BeforeEach
    void setUp() {
        dto = new HabitacionDto(
                1L,
                "Habitación A",
                "Calle Falsa 123",
                2,
                1L,
                100.0
        );
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
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreHotel").value("Habitación A"))
                .andExpect(jsonPath("$.precio").value(100.0));
    }

    @Test
    void editar_DeberiaActualizarHabitacion() throws Exception {
        // Asegúrate de crear una habitación previamente con ID = 1L, o cambia este test para encadenar primero una creación
        mockMvc.perform(put("/rooms/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
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


