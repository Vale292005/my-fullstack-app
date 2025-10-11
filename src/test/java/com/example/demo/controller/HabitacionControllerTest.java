package com.example.demo.controller;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.service.HabitacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitacionController.class)
class HabitacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HabitacionService habitacionService;

    @Autowired
    private ObjectMapper objectMapper;

    private HabitacionDto dto;

    @TestConfiguration
    static class TestConfig {
        @Bean
        HabitacionService habitacionService() {
            return Mockito.mock(HabitacionService.class);
        }
    }

    @BeforeEach
    void setUp() {
        dto = new HabitacionDto(1L, "Habitación A", "Calle Falsa 123", 2,1L,100.0);
    }

    @Test
    void listarPorHotel_DeberiaRetornarListaHabitaciones() throws Exception {
        Mockito.when(habitacionService.listarHabitacionesPorHotel(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/rooms/{hotelId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreHotel").value("Habitación A"))
                .andExpect(jsonPath("$[0].direccion").value("Calle Falsa 123"));
    }

    @Test
    void crear_DeberiaCrearHabitacionYRetornarDto() throws Exception {
        Mockito.when(habitacionService.crearHabitacion(any(HabitacionDto.class))).thenReturn(dto);

        mockMvc.perform(post("/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreHotel").value("Habitación A"))
                .andExpect(jsonPath("$.precio").value(100.0));
    }

    @Test
    void editar_DeberiaActualizarHabitacion() throws Exception {
        Mockito.when(habitacionService.editarHabitacion(eq(1L), any(HabitacionDto.class))).thenReturn(dto);

        mockMvc.perform(put("/rooms/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreHotel").value("Habitación A"));
    }

    @Test
    void eliminar_DeberiaEliminarHabitacion() throws Exception {
        mockMvc.perform(delete("/rooms/{id}", 1L))
                .andExpect(status().isNoContent());

        Mockito.verify(habitacionService).eliminarHabitacion(1L);
    }
}

