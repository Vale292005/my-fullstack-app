package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.config.JwtUtil;
import com.example.demo.dto.usuariodto.LoginRequestDto;
import com.example.demo.dto.usuariodto.UsuarioDto;
import com.example.demo.entity.DocumentosHost;
import com.example.demo.entity.Usuario;
import com.example.demo.mapper.UsuarioMapper;
import com.example.demo.repository.DocumentosRepository;
import com.example.demo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private final DocumentosRepository documentosRepository;

    private final JwtUtil jwtUtil;
    private final Map<String, String> tokensPorEmail = new HashMap<>();
    private final Set<String> tokensInvalidos = new HashSet<>();


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
    public UsuarioDto findById(Long id) {
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
    public void eliminarUsuario(Long id) {
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

    // Login valida token de ingreso

    public String login(LoginRequestDto dto) {
        Usuario usuario = repository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!usuario.isActivo()) {
            throw new RuntimeException("Cuenta no confirmada");

        }System.out.println("DTO contraseña: " + dto.contrasenha());
        System.out.println("Usuario contraseña en BD: " + usuario.getContrasenha());


        if (!passwordEncoder.matches(dto.contrasenha(), usuario.getContrasenha())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());
        if (!esTokenValido(token)) {
            throw new RuntimeException("Token inválido o expirado");
        }

        return token;
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
    public void invalidarToken(String token) {
        tokensInvalidos.add(token);
    }

    public boolean esTokenValido(String token) {
        return !tokensInvalidos.contains(token);
    }
    public boolean activarUsuarioPorToken(String token) {
        String email = jwtUtil.obtenerEmail(token);
        var usuario = repository.findByEmail(email).orElse(null);

        if (usuario == null) return false;

        if (!usuario.isActivo()) {
            usuario.setActivo(true);
            repository.save(usuario);
            return true;
        }

        return false;
    }
    public boolean reenviarEnlaceConfirmacion(String email) {
        var usuario = repository.findByEmail(email).orElse(null);

        if (usuario == null) return false;
        if (usuario.isActivo()) return false; // ya está activo, no hay nada que reenviar

        // Generar un nuevo token de confirmación
        String token = jwtUtil.generarToken(usuario.getEmail(), usuario.getRol());

        // Enviar email con el enlace de confirmación
        String enlace = "http://localhost:8080/auth/confirmar?token=" + token;//CAMBIAR!!!!!!!!!!!!No olvidarse
        emailService.enviarCorreo(email, "Confirma tu cuenta",
                "Haz clic en el siguiente enlace para activar tu cuenta:\n" + enlace);

        return true;
    }
    public boolean iniciarRecuperacionPassword(String email) {
        var usuario = repository.findByEmail(email).orElse(null);
        if (usuario == null) return false;

        // Generar token temporal solo para reset password
        String token = jwtUtil.generarToken(usuario.getEmail(),usuario.getRol());

        String enlace = "http://localhost:8080/auth/reset-password?token=" + token;
        emailService.enviarCorreo(
                email,
                "Recuperación de contraseña",
                "Haz clic en este enlace para restablecer tu contraseña:\n" + enlace
        );

        return true;
    }

    public void restablecerPassword(String token, String nuevaPassword) {
        // Obtener el email desde el token
        String email = jwtUtil.obtenerEmail(token);

        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setContrasenha(passwordEncoder.encode(nuevaPassword));
        repository.save(usuario);

        // Invalida el token si manejas blacklist
        tokensInvalidos.add(token);
    }

    public UsuarioDto obtenerPerfil(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return usuarioMapper.toDto(usuario);
    }

    public void eliminarCuenta(String email) {
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        repository.delete(usuario);
    }
    public void actualizarUsuario(Long id, UsuarioDto dto) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setNombre(dto.nombre());
        usuario.setTelefono(dto.telefono());
        usuario.setEmail(dto.email());
        usuario.setEdad(dto.edad());
        usuario.setRol(dto.rol());
        usuario.setActivo(dto.activo());

        repository.save(usuario);
    }
    public List<DocumentosHost> listarDocumentosPendientes() {
        return documentosRepository.findByEstado("PENDIENTE");
    }

    public void aprobarSolicitud(Long userId) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        DocumentosHost docs = documentosRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay documentos cargados"));
        docs.setAprobado(true);
        docs.setEstado("APROBADO");
        usuario.setRol(Rol.ANFITRION);
        repository.save(usuario);
        documentosRepository.save(docs);
    }

    public void rechazarSolicitud(Long userId) {
        Usuario usuario = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        DocumentosHost docs = documentosRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException("No hay documentos cargados"));
        docs.setAprobado(false);
        docs.setEstado("RECHAZADO");
        documentosRepository.save(docs);
    }

}

