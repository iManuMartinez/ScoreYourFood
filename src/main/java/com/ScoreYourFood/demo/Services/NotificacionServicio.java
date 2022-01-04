package com.ScoreYourFood.demo.Services;

import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacionServicio {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void enviar(String cuerpo, String titulo, String email) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(email);
        mensaje.setFrom("noreply@score-your-food.com");
        mensaje.setSubject(titulo);
        mensaje.setText(cuerpo);

        mailSender.send(mensaje);
    }

//    public void CodigoSeguridad(String cuerpo, String titulo, String email) {
//        int numero;
//        ArrayList numeros = new ArrayList();
//
//// Genera 5 numeros entre 1 y 50
//        for (int i = 1; i <= 5; i++) {
//            numero = (int) (Math.random() * 50 + 1);
//            if (numeros.contains(numero)) {
//                i--;
//            } else {
//                numeros.add(numero);
//            }
//        }
//        
//        SimpleMailMessage mensaje = new SimpleMailMessage();
//        mensaje.setTo(email);
//        mensaje.setFrom("noreply@score-your-food.com");
//        mensaje.setSubject(titulo);
//        mensaje.setText(cuerpo);
//
//        mailSender.send(mensaje);
//    }

}
