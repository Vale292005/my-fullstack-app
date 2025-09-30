package com.example.demo.controller;

import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.ResetPassword;
import com.example.demo.dto.ResetPasswordRequestDto;
import com.example.demo.dto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    //endPoint GET
    public List<UsuarioDto> obtenerTodas(){
        List<Usuario>usuario=service.listarUsuarioServive();
        return usuario.stream().map(UsuarioMapper::toDto).collect(Collectors.toList());
    }
    //endPoint POST
    public UsuarioDto crear(UsuarioDto dto){
        Usuario usuario=UsuarioMapper.toEntity(dto);
        Usuario creado=service.crearUsuario(usuario);
        return UsuarioMapper.toDto(creado);
    }

    @PostMapping("/login)")
    public ResponseEntity<UsuarioDto> login(@RequestBody LoginRequestDto loginRequest){
        Usuario usuario=service.login(loginRequest.getEmail(),loginRequest.getContrsenha());
        return ResponseEntity.ok(UsuarioMapper.toDto(usuario));
    }
    @PostMapping("/registro")
    public ResponseEntity<UsuarioDto>registrar(@RequestBody UsuarioDto usuarioDto){
        Usuario usuario=UsuarioMapper.toEntity(usuarioDto);
        Usuario creado=service.crearUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(creado));
    }
    @PostMapping("/recuperar-contrasenha")
    public ResponseEntity<String>recuperarContrasenha(@RequestBody ResetPasswordRequestDto requestDto){
        service.enviarTokenRecuperacion(requestDto.getEmail());
        return ResponseEntity.ok("correo enviado");
    }
    @PostMapping("/restablecer-password")
    public ResponseEntity<String> restablecerPassword(@RequestBody ResetPassword dto){
        service.restablecerPassword(dto.getToken(),dto.getNewPassword());
        return ResponseEntity.ok("contraseña actualizada");
    }
    @PostMapping("/cambiar-contrasenha")
    public ResponseEntity<String> cambiarPassword(@RequestBody ChangePasswordDto dto){
        service.cambiarPasword(dto.getCurrentPassword(),dto.getPassword());
        return ResponseEntity.ok("contraseña cambiada");
    }
}
