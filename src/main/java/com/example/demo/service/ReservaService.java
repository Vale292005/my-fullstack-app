package com.example.demo.service;

import com.example.demo.dto.ReservaDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Reserva;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.ReservaMapper;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.ReservaRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HabitacionRepository habitacionRepository;
    private final ReservaMapper reservaMapper;

    // Crear reserva
    public ReservaDto crearReserva(Integer usuarioId, Long habitacionId, ReservaDto dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        // Validación de fechas
        if (dto.fchEntrada().isEqual(dto.fchSalida()) || dto.fchEntrada().isAfter(dto.fchSalida())) {
            throw new RuntimeException("La fecha de entrada no puede ser igual o posterior a la fecha de salida");
        }

        // Convertir DTO a entidad
        Reserva reserva = reservaMapper.toEntity(dto);
        reserva.setUsuario(usuario);
        reserva.setHabitacion(habitacion);

        Reserva guardada = reservaRepository.save(reserva);
        return reservaMapper.toDto(guardada);
    }


    // Listar todas las reservas
    public List<ReservaDto> listarReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    // Listar reservas por usuario
    public List<ReservaDto> listarReservasPorUsuario(Integer usuarioId) {
        return reservaRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    // Listar reservas por habitación
    public List<ReservaDto> listarReservasPorHabitacion(Long habitacionId) {
        return reservaRepository.findByHabitacion_Id(habitacionId)
                .stream()
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());
    }

    // Buscar reserva por ID
    public ReservaDto buscarPorId(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        return reservaMapper.toDto(reserva);
    }

    // Actualizar reserva
    public ReservaDto actualizarReserva(Long reservaId, ReservaDto dto) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validación de fechas
        if (dto.fchEntrada().isEqual(dto.fchSalida()) || dto.fchEntrada().isAfter(dto.fchSalida())) {
            throw new RuntimeException("La fecha de entrada no puede ser igual o posterior a la fecha de salida");
        }

        Reserva actualizada = reservaMapper.toEntity(dto);
        actualizada.setId(reserva.getId());
        actualizada.setUsuario(reserva.getUsuario());
        actualizada.setHabitacion(reserva.getHabitacion());

        Reserva guardada = reservaRepository.save(actualizada);
        return reservaMapper.toDto(guardada);
    }

    // Eliminar reserva
    public void eliminarReserva(Long reservaId) {
        if (!reservaRepository.existsById(reservaId)) {
            throw new RuntimeException("Reserva no encontrada");
        }
        reservaRepository.deleteById(reservaId);
    }
}
