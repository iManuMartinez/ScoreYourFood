/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ScoreYourFood.demo.Services;

import com.ScoreYourFood.demo.Entidades.Bar;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.BarRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BarServices {

    @Autowired
    private BarRepository barRepositorio;

    @Transactional
    public void agregarPlato(String nombre, String calle, Integer numero) throws ErrorServicio {

        Bar bar = new Bar();
        validacion(nombre, calle,numero);
        bar.setBar(nombre);
        bar.setCalle(calle);
        bar.setNumero(numero);

        barRepositorio.save(bar);
    }

    @Transactional
    public void modificar(String id, String nombre, String calle,Integer numero) throws ErrorServicio {

        validacion(nombre, calle,numero);

        Optional<Bar> respuesta = barRepositorio.findById(id);
        if (respuesta.isPresent()) {

            Bar bar = respuesta.get();
            bar.setBar(nombre);
            bar.setCalle(calle);
            bar.setNumero(numero);

            barRepositorio.save(bar);

        } else {
            throw new ErrorServicio("No se encontro el plato solicitado");
        }
    }
    @Transactional
    public Bar buscarBarPorId(String id) throws ErrorServicio {
        Optional<Bar> respuesta = barRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Bar plato = respuesta.get();
            return plato;
        } else {
            throw new ErrorServicio("No se encontro el Bar solicitado");
        }
    }
    

    public Bar validarDireccion(String calle,Integer numero) throws ErrorServicio {
        List<Bar> bares = null;
        bares = barRepositorio.buscarPorCalle(calle);
        for (Bar bar : bares) {
            if (Objects.equals(bar.getNumero(), numero)) {
                return bar;
            }
        }
        return null;
    }
    public void validacion(String nombre, String calle,Integer numero) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("Debe ingresar un comentario.");
        }
        if (calle == null) {
            throw new ErrorServicio("Debe indicar una valoracion.");
        }
        if (numero == null) {
            throw new ErrorServicio("Debe indicar una valoracion.");
        }
        if (numero <=0 || numero>=10000) {
            throw new ErrorServicio("Ingreso una numeracion erronea");
        }
        Bar bar = validarDireccion(calle, numero);
        if (bar != null) {
            throw new ErrorServicio("El bar "+bar.getBar()+" se encuentra cargado en esa direccion");
        }
       
    }
}
