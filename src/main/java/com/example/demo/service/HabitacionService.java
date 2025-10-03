package com.example.demo.service;

import com.example.demo.dto.HabitacionDto;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.HabitacionMapper;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final HabitacionMapper habitacionMapper;

    // Crear habitación
    public HabitacionDto crearHabitacion(Integer usuarioId, HabitacionDto dto) {
        Habitacion habitacion = habitacionMapper.toEntity(dto);
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        habitacion.setUsuario(usuario);
        Habitacion creado = habitacionRepository.save(habitacion);
        return habitacionMapper.toDto(creado);
    }

    // Listar TODAS las habitaciones
    public List<HabitacionDto> listarHabitaciones() {
        return habitacionRepository.findAll()
                .stream()
                .map(habitacionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Listar habitaciones por usuario
    public List<HabitacionDto> listarHabitacionesPorUsuario(Integer usuarioId) {
        return habitacionRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(habitacionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Buscar habitación por ID
    public HabitacionDto buscarPorId(Integer habitacionId) {
        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));
        return habitacionMapper.toDto(habitacion);
    }

    // Actualizar habitación
    public HabitacionDto actualizarHabitacion(Integer habitacionId, HabitacionDto dto) {
        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        Habitacion habitacionActualizada = habitacionMapper.toEntity(dto);
        habitacionActualizada.setId(habitacion.getId()); // aseguramos que se actualiza la misma
        habitacionActualizada.setUsuario(habitacion.getUsuario()); // mantenemos la relación con el usuario

        Habitacion guardada = habitacionRepository.save(habitacionActualizada);
        return habitacionMapper.toDto(guardada);
    }

    // Eliminar habitación
    public void eliminarHabitacion(Integer habitacionId) {
        if (!habitacionRepository.existsById(habitacionId)) {
            throw new RuntimeException("Habitación no encontrada");
        }
        habitacionRepository.deleteById(habitacionId);
    }
}

