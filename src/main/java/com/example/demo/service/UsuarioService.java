package com.example.demo.service;

import com.example.demo.Enum.Rol;
import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

public class UsuarioService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private Map<String, String> tokensPorEmail = new HashMap<>();

    public UsuarioService(UsuarioRepository repository,BCryptPasswordEncoder passwordEncoder,EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder=passwordEncoder;
        this.emailService=emailService;
    }
    public List<Usuario> listarUsuarioServive(){
        return repository.findAll();
    }
    public Optional<Usuario> findById(Integer id) {
        return repository.findById(id);
    }
    public Optional<List<Usuario>> findByRol(Rol rol){return repository.findByRol(rol);}
    public Usuario crearUsuario(Usuario usuario) {
        Optional<Usuario> existente = repository.findByNombreUsuario(usuario.getNombre());

        if (existente.isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe con ese nombre");
        }

        return repository.save(usuario);
    }


    public void eliminarUsuario(Integer id){
        repository.delete(id);
    }
    public void enviarTokenRecuperacion(String email) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado con ese correo");
        }
        Usuario usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();
        tokensPorEmail.put(email, token);

        emailService.enviarCorreo(
                usuario.getEmail(),
                "Recuperación de contraseña",
                "Usa este token para restablecer tu contraseña: " + token
        );
    }

    public void restablecerPassword(String token, String nuevaContrasenha) {
        String emailEncontrado = null;
        for (Map.Entry<String, String> entry : tokensPorEmail.entrySet()) {
            if (entry.getValue().equals(token)) {
                emailEncontrado = entry.getKey();
                break;
            }
        }

        if (emailEncontrado == null) {
            throw new RuntimeException("Token inválido");
        }
         Optional<Usuario>usuarioOpt=repository.findByEmail(emailEncontrado);
        if(usuarioOpt.isEmpty()){
            throw new RuntimeException("usuario no encontrado");
        }
        Usuario usuario=usuarioOpt.get();
        usuario.setContrasenha(nuevaContrasenha);
        repository.save(usuario);
        tokensPorEmail.remove(emailEncontrado);
    }
    public void cambiarPassword(String email, String currentPassword, String nuevaContrasenha) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        if (!usuario.getContrasenha().equals(currentPassword)) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        usuario.setContrasenha(nuevaContrasenha);
        repository.save(usuario);
    }
    public Usuario login(String email,String contrasenha){
        Usuario usuario = repository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        if(!passwordEncoder.matches(contrasenha,usuario.getContrasenha())){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"contraseña incorrecta");
        }
        return usuario;
    }
    public Optional<Usuario> findByNombre(String nombre) {
        return repository.findByNombreUsuario(nombre);
    }


    public Optional<Usuario> findByEmail(String email){
        return repository.findByEmail(email);
    }

}
