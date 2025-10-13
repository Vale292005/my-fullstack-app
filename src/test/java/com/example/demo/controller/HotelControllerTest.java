package com.example.demo.controller;

import com.example.demo.dto.HotelDto;
import com.example.demo.repository.HotelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll(); // Limpiar la base de datos de test antes de cada test
    }

    @Test
    void crearHotel_deberiaGuardarYDevolverMensaje() throws Exception {
        String hotelJson = """
            {
                "nombre": "Hotel Test",
                "direccion": "Av. Siempre Viva",
                "descripcion": "Un buen lugar"
            }
        """;

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Hotel creado exitosamente")));
    }

    @Test
    void listarHoteles_deberiaRetornarListaJson() throws Exception {
        // Primero, crear un hotel en la base
        String hotelJson = """
            {
                "nombre": "Hotel Test",
                "direccion": "Calle 123",
                "descripcion": "Descripción"
            }
        """;
        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson)).andExpect(status().isCreated());

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Hotel Test"));
    }

    @Test
    void editarHotel_deberiaActualizarYDevolverMensaje() throws Exception {
        // Crear hotel
        String hotelJson = """
            {
                "nombre": "Hotel Original",
                "direccion": "Calle Original",
                "descripcion": "Original"
            }
        """;
        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson)).andExpect(status().isCreated());

        Long hotelId = hotelRepository.findAll().get(0).getId();

        String editadoJson = """
            {
                "id": %d,
                "nombre": "Hotel Editado",
                "direccion": "Nueva Dir",
                "descripcion": "Actualizado"
            }
        """.formatted(hotelId);

        mockMvc.perform(put("/hotels/" + hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editadoJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hotel actualizado correctamente")));
    }

    @Test
    void eliminarHotel_deberiaEliminarYDevolverMensaje() throws Exception {
        // Crear hotel
        String hotelJson = """
            {
                "nombre": "Hotel Borrar",
                "direccion": "Delete St.",
                "descripcion": "Eliminar"
            }
        """;
        mockMvc.perform(post("/hotels")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hotelJson)).andExpect(status().isCreated());

        Long hotelId = hotelRepository.findAll().get(0).getId();

        mockMvc.perform(delete("/hotels/" + hotelId))
                .andExpect(status().isOk())
                .andExpect(content().string("Hotel eliminado correctamente"));
    }
}


