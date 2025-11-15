package com.example.demo.controller;

import com.example.demo.dto.HotelDto;
import com.example.demo.entity.Hotel;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.HotelRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hoteles")
public class HotelController {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    // 🟢 GET /hotels — Buscar hoteles
    @GetMapping
    public ResponseEntity<List<HotelDto>> listarHoteles(
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) String nombre
    ) {
        List<HotelDto> hoteles = hotelService.buscarHoteles(direccion, nombre);
        return ResponseEntity.ok(hoteles);
    }

    // 🟡 POST /hotels — Crear hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @PostMapping
    public ResponseEntity<String> crearHotel(@RequestBody HotelDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName(); // email del usuario autenticado

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        Hotel hotel = new Hotel();
        hotel.setNombre(dto.nombre());
        hotel.setDireccion(dto.direccion());
        hotel.setDescripcion(dto.descripcion());
        hotel.setUsuario(usuario);

        hotelRepository.save(hotel);

        return ResponseEntity.ok("Hotel creado exitosamente");
    }


    // 🟠 PUT /hotels/{id} — Editar hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<String> editarHotel(@PathVariable Long id, @RequestBody HotelDto dto) {
        hotelService.editarHotel(id, dto);
        return ResponseEntity.ok("Hotel actualizado correctamente");
    }

    //  DELETE /hotels/{id} — Eliminar hotel (solo anfitrión o admin)
    @PreAuthorize("hasAnyRole('ANFITRION','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarHotel(@PathVariable Long id) {
        hotelService.eliminarHotel(id);
        return ResponseEntity.ok("Hotel eliminado correctamente");
    }
}

