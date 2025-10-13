package com.example.demo.service;

import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import com.example.demo.repository.HotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    private final HotelRepository hotelRepository;

    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public List<HotelDto> buscarHoteles(String ciudad, String nombre) {
        List<Hotel> hoteles;

        if (ciudad != null && nombre != null) {
            hoteles = hotelRepository.findByDireccionContainingIgnoreCaseAndNombreContainingIgnoreCase(ciudad, nombre);
        } else if (ciudad != null) {
            hoteles = hotelRepository.findByDireccionContainingIgnoreCase(ciudad);
        } else if (nombre != null) {
            hoteles = hotelRepository.findByNombreContainingIgnoreCase(nombre);
        } else {
            hoteles = hotelRepository.findAll();
        }

        return hoteles.stream()
                .map(h -> new HotelDto(
                        h.getId(),
                        h.getNombre(),
                        h.getDireccion(),
                        h.getDescripcion(),
                        h.getUsuario()
                ))
                .collect(Collectors.toList());

    }

    public void crearHotel(HotelDto dto) {
        Hotel hotel = new Hotel();
        hotel.setNombre(dto.nombre());
        hotel.setDireccion(dto.direccion());
        hotel.setDescripcion(dto.descripcion());
        hotelRepository.save(hotel);
    }

    public void editarHotel(Long id, HotelDto dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
        hotel.setNombre(dto.nombre());
        hotel.setDireccion(dto.direccion());
        hotel.setDescripcion(dto.descripcion());
        hotelRepository.save(hotel);
    }

    public void eliminarHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel no encontrado");
        }
        hotelRepository.deleteById(id);
    }
}


