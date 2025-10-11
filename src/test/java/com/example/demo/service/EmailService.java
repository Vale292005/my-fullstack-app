package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviarCorreo(String destinatario, String asunto, String contenido) {
        // Aquí puedes implementar el envío real de correo (por SMTP, SendGrid, etc.)
        System.out.println("Enviando correo a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Contenido: " + contenido);
    }
}

