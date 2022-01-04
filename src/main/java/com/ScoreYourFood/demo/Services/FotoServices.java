/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ScoreYourFood.demo.Services;

import com.ScoreYourFood.demo.Entidades.Foto;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.FotoRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotoServices {
    
    @Autowired
    private FotoRepository fotoRepositorio;
    
    public Foto guardar(MultipartFile archivo) throws ErrorServicio {
        if (archivo!=null && !archivo.isEmpty()) {
            try{
                Foto foto=new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                
                return fotoRepositorio.save(foto);
                
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        return null;
    }
    
    public Foto actualizar(String idFoto,MultipartFile archivo){
        if (archivo!=null) {
            try{
                Foto foto=new Foto();
                if (idFoto!=null) {
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    if (respuesta.isPresent()) {
                        foto=respuesta.get();          
                    }
                }
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                
                return fotoRepositorio.save(foto);
                
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        return null;   
    }
    
    
}
