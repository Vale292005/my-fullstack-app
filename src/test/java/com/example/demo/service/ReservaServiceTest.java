package com.example.demo.service;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Reserva;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ReservaServiceTest {

    private ReservaRepository reservaRepository;
    private UsuarioRepository usuarioRepository;
    private HabitacionRepository habitacionRepository;
    private ReservaService reservaService;

    @BeforeEach
    void setUp() {
        reservaRepository = mock(ReservaRepository.class);
        usuarioRepository = mock(UsuarioRepository.class);
        habitacionRepository = mock(HabitacionRepository.class);
        reservaService = new ReservaService(reservaRepository, usuarioRepository, habitacionRepository);
    }

    @Test
    void crearReserva_validaDatosYGuarda() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEdad(LocalDate.of(2000, 1, 1));

        Habitacion habitacion = new Habitacion();
        habitacion.setId(2L);
        habitacion.setMangos(3);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(habitacionRepository.findById(2L)).thenReturn(Optional.of(habitacion));

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(10L);
        reservaGuardada.setUsuario(usuario);
        reservaGuardada.setHabitacion(habitacion);
        reservaGuardada.setFchEntrada(LocalDate.now().plusDays(1));
        reservaGuardada.setFchSalida(LocalDate.now().plusDays(3));
        reservaGuardada.setPrecioTotal(200.0);

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        ReservaDto dto = new ReservaDto(
                null,
                1L,
                2L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                200.0
        );

        ReservaDto result = reservaService.crearReserva(dto);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.usuarioId()).isEqualTo(1L);
        assertThat(result.habitacionId()).isEqualTo(2L);
        verify(reservaRepository).save(any(Reserva.class));
    }

    @Test
    void crearReserva_usuarioMenorDeEdad_lanzaExcepcion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEdad(LocalDate.now().minusYears(10)); // menor de edad

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Habitacion habitacion = new Habitacion();
        habitacion.setId(2L);
        when(habitacionRepository.findById(2L)).thenReturn(Optional.of(habitacion));

        ReservaDto dto = new ReservaDto(
                null,
                1L,
                2L,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                200.0
        );

        assertThrows(RuntimeException.class, () -> reservaService.crearReserva(dto));
    }

    @Test
    void reservasPorUsuario_devuelveLista() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Habitacion habitacion = new Habitacion();
        habitacion.setId(2L);

        Reserva reserva = new Reserva();
        reserva.setId(100L);
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFchEntrada(LocalDate.now());
        reserva.setFchSalida(LocalDate.now().plusDays(2));
        reserva.setPrecioTotal(500.0);

        when(reservaRepository.findByUsuarioId(1L)).thenReturn(List.of(reserva));

        List<ReservaDto> result = reservaService.reservasPorUsuario(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(100L);
        verify(reservaRepository).findByUsuarioId(1L);
    }

    @Test
    void todasReservas_devuelveLista() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Habitacion habitacion = new Habitacion();
        habitacion.setId(2L);

        Reserva reserva = new Reserva();
        reserva.setId(50L);
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFchEntrada(LocalDate.now());
        reserva.setFchSalida(LocalDate.now().plusDays(1));
        reserva.setPrecioTotal(100.0);

        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<ReservaDto> result = reservaService.todasReservas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(50L);
    }

    @Test
    void eliminarReserva_existente_eliminaCorrectamente() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.eliminarReserva(1L);

        verify(reservaRepository).delete(reserva);
    }

    @Test
    void eliminarReserva_inexistente_lanzaExcepcion() {
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservaService.eliminarReserva(1L));
    }
}
