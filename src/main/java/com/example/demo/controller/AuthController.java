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
    //entrar
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
    //salir
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token no proporcionado");
            }

            String token = authHeader.substring(7); // Quita "Bearer "
            servicio.invalidarToken(token);

            return ResponseEntity.ok("Sesión cerrada correctamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al cerrar sesión");
        }
    }
    //confrima emial po token
    @GetMapping("/confirmar")
    public String confirmarEmail(@RequestParam("token") String token) {

        if (!servicio.esTokenValido(token)) {
            return "Token inválido ";
        }

        boolean activado = servicio.activarUsuarioPorToken(token);

        if (activado) {
            return "Correo confirmado correctamente";
        } else {
            return "La cuenta ya estaba activa o no se encontró el usuario.";
        }
    }
    //reenviaconfirmacion de activar cuenta
    @PostMapping("/resend-confirmation")
    public ResponseEntity<String> reenviarConfirmacion(@RequestParam("email") String email) {
        try {
            boolean reenviado = servicio.reenviarEnlaceConfirmacion(email);

            if (reenviado) {
                return ResponseEntity.ok("Se ha reenviado el enlace de confirmación ");
            } else {
                return ResponseEntity.badRequest().body("La cuenta ya está activa o no existe");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al reenviar el correo");
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam("email") String email) {
        try {
            boolean enviado = servicio.iniciarRecuperacionPassword(email);

            if (enviado) {
                return ResponseEntity.ok("Se ha enviado un correo para recuperar tu contraseña.");
            } else {
                return ResponseEntity.badRequest().body("No existe una cuenta con ese correo.");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al procesar la solicitud.");
        }
    }
}


