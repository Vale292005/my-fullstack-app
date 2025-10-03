package com.example.demo.controller;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.*;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.classfile.Opcode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    @Autowired
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioDto> obtenerTodas() {
        List<Usuario> usuario = service.listarUsuarioServive();
        return usuario.stream().map(UsuarioMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping
    public UsuarioDto crear(UsuarioDto dto) {
        Usuario usuario = UsuarioMapper.toEntity(dto);
        Usuario creado = service.crearUsuario(usuario);
        return UsuarioMapper.toDto(creado);
    }

    @PostMapping("/login)")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginRequestDto loginRequest) {
        Usuario usuario = service.login(loginRequest.getEmail(), loginRequest.getContrsenha());
        return ResponseEntity.ok(UsuarioMapper.toDto(usuario));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody UsuarioDto usuarioDto) {
        try {
            Usuario usuario = UsuarioMapper.toEntity(usuarioDto);
            Usuario creado = service.crearUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(creado));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @PostMapping("/recuperar-contrasenha")
    public ResponseEntity<String> recuperarContrasenha(@RequestBody ResetPasswordRequestDto requestDto) {
        service.enviarTokenRecuperacion(requestDto.getEmail());
        return ResponseEntity.ok("correo enviado");
    }

    @PostMapping("/restablecer-password")
    public ResponseEntity<String> restablecerPassword(@RequestBody ResetPassword dto) {
        service.restablecerPassword(dto.getToken(), dto.getNewPassword());
        return ResponseEntity.ok("contraseña actualizada");
    }

    @PostMapping("/cambiar-contrasenha")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordDto dto) {
        service.cambiarPassword(dto.getEmail(), dto.getCurrentPassword(), dto.getNewPassword());
        return ResponseEntity.ok("contraseña cambiada");
    }

    @GetMapping("/buscar/nombre")
    public ResponseEntity<?> buscarNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.findByNombre(nombre));
    }
    @GetMapping("/buscar/email")
    public ResponseEntity<?> buscarEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.findByEmail(email));
    }
    @GetMapping("/roles")
    public ResponseEntity<?> listByRol(@RequestParam("rol") Rol rol) {
        List<Usuario> usuarios = service.findByRol(rol);  // devuelve List<Usuario>

        if (usuarios.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontraron usuarios con el rol " + rol);
        }

        List<UsuarioDto> dtos = usuarios.stream()
                .map(UsuarioMapper::toDto)  // convertimos a DTO
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }



}
