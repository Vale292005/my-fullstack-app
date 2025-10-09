package com.example.demo.service;

import com.example.demo.dto.ComentarioDto;
import com.example.demo.entity.Comentario;
import com.example.demo.entity.Habitacion;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.ComentarioRepository;
import com.example.demo.repository.HabitacionRepository;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final HotelRepository hotelRepository;
    private final HabitacionRepository habitacionRepository;

    public ComentarioDto crearComentario(ComentarioDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Hotel hotel = hotelRepository.findById(dto.hotel().getId())
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
        Habitacion habitacion = habitacionRepository.findById(dto.habitacion().getId())
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        Comentario comentario = new Comentario();
        comentario.setUsuario(usuario);
        comentario.setHotel(hotel);
        comentario.setHabitacion(habitacion);
        comentario.setMensaje(dto.mensaje());
        comentario.setCalificacion(dto.calificacion());
        comentario.setFchCreacion(LocalDateTime.now());


        Comentario saved = comentarioRepository.save(comentario);

        return new ComentarioDto(saved.getId(), usuario, hotel, habitacion,
                saved.getMensaje(), saved.getCalificacion(), saved.getFchCreacion());
    }

    public List<ComentarioDto> verComentariosPorHabitacion(Long habitacionId) {
        Habitacion habitacion = habitacionRepository.findById(habitacionId)
                .orElseThrow(() -> new RuntimeException("Habitación no encontrada"));

        return comentarioRepository.findByHabitacion(habitacion)
                .stream()
                .map(c -> new ComentarioDto(c.getId(), c.getUsuario(), c.getHotel(),
                        c.getHabitacion(), c.getMensaje(), c.getCalificacion(), c.getFchCreacion()))
                .collect(Collectors.toList());
    }

    public void eliminarComentario(Long comentarioId, Usuario usuarioActual) {
        Comentario comentario = comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        // permisos: propio, admin, o anfitrión del hotel
        boolean esPropio = comentario.getUsuario().getId().equals(usuarioActual.getId());
        boolean esAdmin = usuarioActual.getRol().name().equals("ADMIN");
        boolean esAnfitrionDelHotel = usuarioActual.getRol().name().equals("ANFITRION") &&
                comentario.getHotel().getUsuario().getId().equals(usuarioActual.getId());

        if (!esPropio && !esAdmin && !esAnfitrionDelHotel) {
            throw new RuntimeException("No tiene permiso para eliminar este comentario");
        }

        comentarioRepository.delete(comentario);
    }
}

