package com.example.demo.service;

import com.example.demo.entity.Usuario;
import com.example.demo.repository.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.*;

public class UsuarioService {
    private final UsuarioRepository repository;
    private Map<String, String> tokensPorEmail = new HashMap<>();

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }
    public List<Usuario> listarUsuarioServive(){
        return repository.findAll();
    }
    public Optional<Usuario> obtenerUsuarioPorId(Integer id) {
        return repository.findById(id);
    }
    public Usuario crearUsuario(Usuario usuario){
        Map.Entry<Integer,Usuario>entry=repository.findByNombreUsuario(usuario.getNombre());
        Optional<Usuario> existente= repository.findById(entry.getKey());
        if(existente.isPresent()){
            throw new IllegalArgumentException("El usuario ya existe");
        }return repository.save(usuario);
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


}
