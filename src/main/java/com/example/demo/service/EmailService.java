package com.example.demo.service;
import com.example.demo.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailService emailService;

    public void enviarTokenRecuperacion(Usuario usuario, String token) {
        emailService.enviarCorreo(
                usuario.getEmail(),
                "Recuperación de contraseña",
                "Usa este token para restablecer tu contraseña: " + token
        );
    }
    public void enviarCorreo(String destino, String asunto, String cuerpo) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}

