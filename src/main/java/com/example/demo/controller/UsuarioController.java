package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    //endPoint GET
    public List<UsuarioDto> obtenerTodas() {
        List<Usuario> usuario = service.listarUsuarioServive();
        return usuario.stream().map(UsuarioMapper::toDto).collect(Collectors.toList());
    }

    //endPoint POST
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
    public ResponseEntity<UsuarioDto> registrar(@RequestBody UsuarioDto usuarioDto) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioDto);
        Usuario creado = service.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(creado));
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

}
