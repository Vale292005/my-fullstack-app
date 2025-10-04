package com.example.demo.service;

import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.HotelMapper;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;
    private final UsuarioRepository usuarioRepository;
    private final HotelMapper hotelMapper;
    public HotelDto crearHotel(Integer usuarioId, Hotel hotel){
        Usuario usuario=usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("usuario no encontrado"));
        hotel.setUsuario(usuario);
        Hotel creado=hotelRepository.save(hotel);
        return hotelMapper.toDto(creado);
    }
    public List<HotelDto>listarHoteles(){
        return hotelRepository.findAll()
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }
    public List<HotelDto>listarHotelPorUsuario(Integer usuarioId){
        return hotelRepository.findByUsuario_Id(usuarioId)
                .stream()
                .map(hotelMapper::toDto)
                .collect(Collectors.toList());
    }
    public HotelDto buscarPorId(Long hotelId){
        Hotel hotel=hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("no se encuentra hotel por la id"));
        return hotelMapper.toDto(hotel);
    }
}
