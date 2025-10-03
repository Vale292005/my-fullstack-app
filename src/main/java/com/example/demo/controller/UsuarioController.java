package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.*;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioMapper usuarioMapper;

    // Listar todos los usuarios
    @GetMapping
    public ResponseEntity<ResponseDTO<List<UsuarioDto>>> obtenerTodas() {
        List<UsuarioDto> usuarioDTO = service.listarUsuarios();
        return ResponseEntity.ok(new ResponseDTO<>(false, usuarioDTO));
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<UsuarioDto> crear(@RequestBody UsuarioDto dto) {
        Usuario usuario = usuarioMapper.toEntity(dto); // ✅ ahora sí
        Usuario creado = service.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(creado));
    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginRequestDto loginRequest) {
        Usuario usuario = service.login(loginRequest.getEmail(), loginRequest.getContrsenha());
        return ResponseEntity.ok(usuarioMapper.toDto(usuario)); // ✅
    }

    // Registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDto usuarioDto) {
        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioDto); // ✅ usa la instancia inyectada
            Usuario creado = service.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(creado)); // ✅
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    // Recuperación de contraseña
    @PostMapping("/recuperar-contrasenha")
    public ResponseEntity<String> recuperarContrasenha(@RequestBody ResetPasswordRequestDto requestDto) {
        service.enviarTokenRecuperacion(requestDto.getEmail());
        return ResponseEntity.ok("Correo enviado");
    }

    // Restablecer contraseña
    @PostMapping("/restablecer-password")
    public ResponseEntity<String> restablecerPassword(@RequestBody ResetPassword dto) {
        service.restablecerPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok("Contraseña actualizada");
    }

    // Cambiar contraseña
    @PostMapping("/cambiar-contrasenha")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordDto dto) {
        service.cambiarPassword(dto.getEmail(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok("Contraseña cambiada");
    }

    // Buscar por nombre
    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasRole('ADMIN')") // solo admin puede buscar por nombre
    public ResponseEntity<ResponseDTO<UsuarioDto>> buscarNombre(@RequestParam String nombre) {
        return service.findByNombre(nombre)
                .map(usuario -> ResponseEntity.ok(
                        new ResponseDTO<>(false, usuarioMapper.toDto(usuario))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseDTO<>(true, null)
                ));
    }

    // Buscar por email
    @GetMapping("/buscar/email")
    @PreAuthorize("hasRole('ADMIN')") //  solo admin puede buscar por email
    public ResponseEntity<ResponseDTO<UsuarioDto>> buscarEmail(@RequestParam String email) {
        return service.findByEmail(email)
                .map(usuario -> ResponseEntity.ok(
                        new ResponseDTO<>(false, usuarioMapper.toDto(usuario))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseDTO<>(true, null)
                ));
    }




    // Listar por rol
    @GetMapping("/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<?>> listByRol(@RequestParam("rol") Rol rol) {
        List<Usuario> usuarios = service.findByRol(rol);

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseDTO<>(true, "No se encontraron usuarios con el rol " + rol));
        }

        List<UsuarioDto> dtos = usuarios.stream()
                .map(usuarioMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ResponseDTO<>(false, dtos));
    }
}
