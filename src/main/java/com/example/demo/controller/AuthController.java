package com.example.demo.controller;

import com.example.demo.dto.usuariodto.AuthResponseDto;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.LoginRequestMapper;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import com.fasterxml.jackson.databind.ser.std.UUIDSerializer;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService servicio;
    private final UsuarioMapper mapper;
    private final LoginRequestMapper loginMapper;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UsuarioDto dto) {
        if (!servicio.esMayorDeEdad(dto.edad())) {
            return ResponseEntity.badRequest().body("Debe ser mayor de edad");
        }
        Usuario usuario=mapper.toEntity(dto);
        servicio.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado. Verifique su correo.");
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto dto) {
        String token = servicio.login(dto);
        return ResponseEntity.ok(new AuthResponseDto(token, "Inicio de sesión exitoso"));
    }
    @GetMapping("/confirm")
    public ResponseEntity<?> confirmar(@RequestParam String email) {
        servicio.confirmarCuenta(email);
        return ResponseEntity.ok("Cuenta confirmada correctamente");
    }

}
