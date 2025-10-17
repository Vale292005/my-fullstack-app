package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Reserva;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.ReservaRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class ReservaControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private HabitacionRepository habitacionRepository;
    @Autowired private ReservaRepository reservaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Usuario usuario;
    private Hotel hotel;
    private Habitacion habitacion;

    @BeforeEach
    void setUp() {

        // Limpia en orden correcto
        reservaRepository.deleteAll();
        habitacionRepository.deleteAll();
        hotelRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crea usuario
        usuario = new Usuario();
        usuario.setNombre("Usuario Test");
        usuario.setEmail("reserva@test.com");
        usuario.setContrasenha(passwordEncoder.encode("password"));
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
        hotel.setNombre("Hotel Test");
        hotel.setDireccion("Calle 123");
        hotel.setDescripcion("Hotel de prueba");
        hotel.setUsuario(hostUsuario);
        hotel = hotelRepository.save(hotel);

        // Crea habitación
        habitacion = new Habitacion();
        habitacion.setNombreHotel("Habitación Standard");
        habitacion.setDireccion("Piso 2");
        habitacion.setHotel(hotel);
        habitacion.setPrecio(200.0);
        habitacion = habitacionRepository.save(habitacion);
    }

    @Test
    void crearReserva_deberiaCrearYDevolverReserva() throws Exception {
        LocalDate entrada = LocalDate.now().plusDays(1);
        LocalDate salida = LocalDate.now().plusDays(3);

        String reservaJson = String.format("""
        {
          "hotelId": %d,
          "habitacionId": %d,
          "usuarioId": %d,
          "fchEntrada": "%s",
          "fchSalida": "%s",
          "precio": 200.0
        }
        """,
                hotel.getId(),
                habitacion.getId(),
                usuario.getId(),
                entrada.toString(),
                salida.toString()
        );

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reservaJson))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void verReservasPropias_deberiaListarCorrectamente() throws Exception {
        // Crea reserva
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFchEntrada(LocalDate.now().plusDays(1));
        reserva.setFchSalida(LocalDate.now().plusDays(3));
        reserva.setPrecioTotal(200.0);
        reservaRepository.save(reserva);

        // VERIFICA LA RUTA CORRECTA
        mockMvc.perform(get("/reservas/lista/" + usuario.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void verTodasReservas_deberiaRetornarLista() throws Exception {
        // Crea dos reservas
        Reserva reserva1 = new Reserva();
        reserva1.setUsuario(usuario);
        reserva1.setHabitacion(habitacion);
        reserva1.setFchEntrada(LocalDate.now().plusDays(1));
        reserva1.setFchSalida(LocalDate.now().plusDays(2));
        reserva1.setPrecioTotal(200.0);
        reservaRepository.save(reserva1);
        Reserva reserva2 = new Reserva();
        reserva2.setUsuario(usuario);
        reserva2.setHabitacion(habitacion);
        reserva2.setFchEntrada(LocalDate.now().plusDays(5));
        reserva2.setFchSalida(LocalDate.now().plusDays(7));
        reserva2.setPrecioTotal(400.0);
        reservaRepository.save(reserva2);

        mockMvc.perform(get("/reservas/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].precioTotal").exists());
    }

//    @Test
//    void cancelarReserva_deberiaCancelar() throws Exception {
//        // Crea reserva
//        Reserva reserva = new Reserva();
//        reserva.setUsuario(usuario);
//        reserva.setHabitacion(habitacion);
//        reserva.setFchEntrada(LocalDate.now().plusDays(1));
//        reserva.setFchSalida(LocalDate.now().plusDays(2));
//        reserva.setPrecioTotal(200.0);
//        reserva = reservaRepository.save(reserva);
//
//        mockMvc.perform(delete("/admin/bookings/" + reserva.getId()))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
}


