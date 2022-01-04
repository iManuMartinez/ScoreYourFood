/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ScoreYourFood.demo.Controladores;

import com.ScoreYourFood.demo.Entidades.Plato;
import com.ScoreYourFood.demo.Entidades.Usuario;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Services.PlatoServices;
import com.ScoreYourFood.demo.Services.UsuarioServices;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foto")
public class FotoControlador {
    
    @Autowired
    private UsuarioServices usuarioServicio;
    
    @Autowired
    private PlatoServices platoServicio;
    
    @GetMapping("/usuario/{id}")
    public ResponseEntity<byte[]> fotoUusario(@PathVariable String id){
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            if (usuario.getFoto()==null) {
                throw new ErrorServicio("El usuario no tiene foto asignada");
            }
            byte[] foto = usuario.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(foto,headers,HttpStatus.OK);
        } catch (ErrorServicio ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/plato/{id}")
    public ResponseEntity<byte[]> fotoPlato(@PathVariable String id){
        try {
            Plato plato = platoServicio.buscarPlatoPorId(id);
            if (plato.getFoto()==null) {
                throw new ErrorServicio("El plato no tiene foto asignada");
            }
            byte[] foto = plato.getFoto().getContenido();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(foto,headers,HttpStatus.OK);
        } catch (ErrorServicio ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
