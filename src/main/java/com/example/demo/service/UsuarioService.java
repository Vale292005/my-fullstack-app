package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final UsuarioMapper usuarioMapper;

    private final Map<String, String> tokensPorEmail = new HashMap<>();

    // Listar todos los usuarios en formato DTO
    public List<UsuarioDto> listarUsuarios() {
        return repository.findAll()
                .stream()
                .map(usuarioMapper::toDto)
                .toList();
    }
    public void confirmarCuenta(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(true);
        repository.save(usuario);
    }

    // Buscar usuario por ID
    public UsuarioDto findById(Integer id) {
        Usuario user = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return usuarioMapper.toDto(user);
    }

    // Buscar por rol
    public List<Usuario> findByRol(Rol rol) {
        return repository.findByRol(rol);
    }

    // Crear usuario nuevo
    public Usuario crearUsuario(Usuario usuario) {
        if (repository.findByNombre(usuario.getNombre()).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe con ese nombre");
        }
        usuario.setContrasenha(passwordEncoder.encode(usuario.getContrasenha()));
        return repository.save(usuario);
    }

    // Eliminar usuario
    public void eliminarUsuario(Integer id) {
        repository.deleteById(id);
    }

    // Enviar token de recuperación de contraseña
    public void enviarTokenRecuperacion(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ese correo"));

        String token = UUID.randomUUID().toString();
        tokensPorEmail.put(email, token);

        emailService.enviarCorreo(
                usuario.getEmail(),
                "Recuperación de contraseña",
                "Usa este token para restablecer tu contraseña: " + token
        );
    }

    // Restablecer contraseña con token
    public void restablecerPassword(String token, String nuevaContrasenha) {
        String emailEncontrado = tokensPorEmail.entrySet().stream()
                .filter(e -> e.getValue().equals(token))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        Usuario usuario = repository.findByEmail(emailEncontrado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setContrasenha(passwordEncoder.encode(nuevaContrasenha));
        repository.save(usuario);
        tokensPorEmail.remove(emailEncontrado);
    }

    // Cambiar contraseña con validación
    public void cambiarPassword(String email, String currentPassword, String nuevaContrasenha) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(currentPassword, usuario.getContrasenha())) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        usuario.setContrasenha(passwordEncoder.encode(nuevaContrasenha));
        repository.save(usuario);
    }

    // Login

    public String login(LoginRequestDto dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new RuntimeException("Cuenta no confirmada");
        }

        if (!passwordEncoder.matches(dto.password(), usuario.getContrasenha())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());
    }

    // Buscar por nombre
    public Optional<Usuario> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    // Buscar por email
    public Optional<Usuario> findByEmail(String email) {
        return repository.findByEmail(email);
    }
    public boolean esMayorDeEdad(LocalDate fechaNacimiento) {
        LocalDate hoy = LocalDate.now();
        Period edad = Period.between(fechaNacimiento, hoy);
        return edad.getYears() >= 18;
    }

}

