package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.*;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        Usuario usuario = usuarioMapper.toEntity(dto);
        Usuario creado = service.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(creado));
    }

    // Registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDto usuarioDto) {
        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioDto);
            Usuario creado = service.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioMapper.toDto(creado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    // Recuperar contraseña
    @PostMapping("/recuperar-contrasenha")
    public ResponseEntity<String> recuperarContrasenha(@RequestBody ResetPasswordRequestDto requestDto) {
        service.enviarTokenRecuperacion(requestDto.getEmail());
        return ResponseEntity.ok("Correo enviado");
    }

    // Cambiar contraseña
    @PostMapping("/cambiar-contrasenha")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordDto dto) {
        service.cambiarPassword(dto.getEmail(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok("Contraseña cambiada");
    }

    // Buscar por nombre (solo admin)
    @GetMapping("/buscar/nombre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDto>> buscarNombre(@RequestParam String nombre) {
        return service.findByNombre(nombre)
                .map(usuario -> ResponseEntity.ok(
                        new ResponseDTO<>(false, usuarioMapper.toDto(usuario))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseDTO<>(true, null)
                ));
    }

    // Buscar por email (solo admin)
    @GetMapping("/buscar/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<UsuarioDto>> buscarEmail(@RequestParam String email) {
        return service.findByEmail(email)
                .map(usuario -> ResponseEntity.ok(
                        new ResponseDTO<>(false, usuarioMapper.toDto(usuario))
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseDTO<>(true, null)
                ));
    }

    // Listar por rol (solo admin)
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

    // Ver perfil (usuario autenticado)
    @GetMapping("/me")
    public ResponseEntity<?> getPerfilUsuario(Authentication authentication) {
        String email = authentication.getName();
        UsuarioDto perfil = service.obtenerPerfil(email);
        return ResponseEntity.ok(perfil);
    }

    // Eliminar cuenta propia
    @DeleteMapping("/me")
    public ResponseEntity<?> eliminarMiCuenta(Authentication authentication) {
        String email = authentication.getName();
        service.eliminarCuenta(email);
        return ResponseEntity.noContent().build();
    }

    // Permisos de administrador
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<?> listarUsuarios() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{email}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String email) {
        service.eliminarCuenta(email);
        return ResponseEntity.ok("Usuario eliminado correctamente");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{email}")
    public ResponseEntity<?> editarUsuario(@PathVariable Long id, @RequestBody UsuarioDto dto) {
        service.actualizarUsuario(id, dto);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }
}
