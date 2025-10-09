package com.example.demo.service;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Reserva;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HabitacionRepository habitacionRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          UsuarioRepository usuarioRepository,
                          HabitacionRepository habitacionRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.habitacionRepository = habitacionRepository;
    }

    // Crear reserva
    public ReservaDto crearReserva(ReservaDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Habitacion habitacion = habitacionRepository.findById(dto.habitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // Validación de edad mínima (≥18)
        if (usuario.getEdad().plusYears(18).isAfter(LocalDate.now())) {
            throw new RuntimeException("Usuario debe ser mayor de edad para reservar");
        }

        // Validación de fechas
        if (dto.fchEntrada().isBefore(LocalDate.now()) || dto.fchSalida().isBefore(LocalDate.now())) {
            throw new RuntimeException("Fechas no pueden estar en el pasado");
        }
        if (!dto.fchEntrada().isBefore(dto.fchSalida())) {
            throw new RuntimeException("Check-in debe ser antes que check-out");
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFchEntrada(dto.fchEntrada());
        reserva.setFchSalida(dto.fchSalida());
        reserva.setPrecioTotal(dto.precioTotal());

        Reserva guardada = reservaRepository.save(reserva);

        return new ReservaDto(
                guardada.getId(),
                guardada.getUsuario().getId(),
                guardada.getHabitacion().getId(),
                guardada.getFchEntrada(),
                guardada.getFchSalida(),
                guardada.getPrecioTotal()
        );
    }

    // Obtener reservas propias
    public List<ReservaDto> reservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(r -> new ReservaDto(
                        r.getId(),
                        r.getUsuario().getId(),
                        r.getHabitacion().getId(),
                        r.getFchEntrada(),
                        r.getFchSalida(),
                        r.getPrecioTotal()
                ))
                .collect(Collectors.toList());
    }

    // Obtener todas las reservas (para admin o anfitriones)
    public List<ReservaDto> todasReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(r -> new ReservaDto(
                        r.getId(),
                        r.getUsuario().getId(),
                        r.getHabitacion().getId(),
                        r.getFchEntrada(),
                        r.getFchSalida(),
                        r.getPrecioTotal()
                ))
                .collect(Collectors.toList());
    }
    public void crearReservaAdmin(Long usuarioId, ReservaDto dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Habitacion habitacion = habitacionRepository.findById(dto.habitacionId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // Validaciones de fechas y edad si quieres aplicarlas también
        if (usuario.getEdad().isAfter(LocalDate.now().minusYears(18))) {
            throw new RuntimeException("El usuario debe ser mayor de edad");
        }
        if (!dto.fchEntrada().isBefore(dto.fchSalida())) {
            throw new RuntimeException("La fecha de entrada debe ser antes de la de salida");
        }

        double precioTotal = habitacion.getMangos() * (dto.fchSalida().toEpochDay() - dto.fchEntrada().toEpochDay());

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);
        reserva.setFchEntrada(dto.fchEntrada());
        reserva.setFchSalida(dto.fchSalida());
        reserva.setPrecioTotal(precioTotal);

        reservaRepository.save(reserva);
    }

    // Eliminar reserva por admin
    public void eliminarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        reservaRepository.delete(reserva);
    }
}



