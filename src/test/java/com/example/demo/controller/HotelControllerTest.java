package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario hostUsuario;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // 🧑 Crear usuario ADMIN simulado
        hostUsuario = new Usuario();
        hostUsuario.setNombre("Host User");
        hostUsuario.setEmail("host@test.com");
        hostUsuario.setContrasenha(passwordEncoder.encode("password"));
        hostUsuario.setEdad(LocalDate.of(1985, 1, 1));
        hostUsuario.setTelefono("3009999999");
        hostUsuario.setRol(Rol.ADMIN);
        hostUsuario.setActivo(true);
        hostUsuario = usuarioRepository.save(hostUsuario);

        // 🧠 Simular autenticación en el contexto de Spring Security
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        hostUsuario.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void crearHotel_deberiaGuardarYDevolverMensaje() throws Exception {
        String hotelJson = """
            {
                "nombre": "Hotel Test",
                "direccion": "Calle 123",
                "descripcion": "Hotel de prueba"
            }
        """;

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(hotelJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hotel creado exitosamente")));
    }

    @Test
    void listarHoteles_deberiaRetornarListaJson() throws Exception {
        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk());
    }

    @Test
    void editarHotel_deberiaActualizarYDevolverMensaje() throws Exception {
        Hotel h = new Hotel();
        h.setNombre("Original");
        h.setDireccion("Dir original");
        h.setUsuario(hostUsuario); // ✅ asignar usuario
        h = hotelRepository.save(h);

        String editJson = """
            {
                "nombre": "Hotel Editado",
                "direccion": "Dir Nueva",
                "descripcion": "Actualizado"
            }
        """;

        mockMvc.perform(put("/hotels/" + h.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(editJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Hotel actualizado correctamente")));
    }

    @Test
    void eliminarHotel_deberiaEliminarYDevolverMensaje() throws Exception {
        Hotel h = new Hotel();
        h.setNombre("Eliminar");
        h.setDireccion("Delete St");
        h.setUsuario(hostUsuario);
        h = hotelRepository.save(h);

        mockMvc.perform(delete("/hotels/" + h.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Hotel eliminado correctamente"));
    }
}

