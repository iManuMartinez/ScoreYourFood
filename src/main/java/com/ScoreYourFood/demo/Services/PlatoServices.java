package com.ScoreYourFood.demo.Services;

import com.ScoreYourFood.demo.Entidades.Bar;
import com.ScoreYourFood.demo.Entidades.Foto;
import com.ScoreYourFood.demo.Enum.TipoComida;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.PlatoRepository;
import com.ScoreYourFood.demo.Repository.UsuarioRepository;
import com.ScoreYourFood.demo.Entidades.Plato;
import com.ScoreYourFood.demo.Entidades.Usuario;
import com.ScoreYourFood.demo.Repository.BarRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PlatoServices {

    @Autowired
    private UsuarioRepository usuarioRepositorio;
    @Autowired
    private PlatoRepository platoRepositorio;
    @Autowired
    private BarRepository barRepositorio;
    @Autowired
    private FotoServices fotoServicio;

    @Transactional
    public void agregarPlato(MultipartFile archivo, String idUsuario, String comentario, Integer valoracion, TipoComida tipoComida, String nombre, String idBar) throws ErrorServicio {
        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        validacion(comentario, valoracion, tipoComida, nombre);
        Bar bar = barRepositorio.findById(idBar).get();
        Plato plato = new Plato();
        plato.setComentario(comentario);
        plato.setValoracion(valoracion);
        plato.setTipo(tipoComida);
        plato.setUsuario(usuario);
        plato.setAlta(new Date());
        plato.setNombre(nombre);
        plato.setBar(bar);
        Foto foto = fotoServicio.guardar(archivo);
        plato.setFoto(foto);

        platoRepositorio.save(plato);
    }

    @Transactional
    public void modificar(MultipartFile archivo, String idUsuario, String id, String comentario, Integer valoracion, TipoComida tipoComida, String nombre, String idBar) throws ErrorServicio {

        validacion(comentario, valoracion, tipoComida, nombre);

        Optional<Plato> respuesta = platoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Bar bar = barRepositorio.findById(idBar).get();
            Plato plato = respuesta.get();
            plato.setNombre(nombre);
            plato.setComentario(comentario);
            plato.setValoracion(valoracion);
            plato.setTipo(tipoComida);
            plato.setBar(bar);
            String idFoto = null;
            if (plato.getFoto() != null) {
                idFoto = plato.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            plato.setFoto(foto);

            platoRepositorio.save(plato);

        } else {
            throw new ErrorServicio("No se encontro el plato solicitado");
        }
    }

    @Transactional
    public void eliminarPlato(String idUsuario, String idPlato) throws ErrorServicio {
        Optional<Plato> respuesta = platoRepositorio.findById(idPlato);
        if (respuesta.isPresent()) {
            Plato plato = respuesta.get();
            if (plato.getUsuario().getId().equals(idUsuario)) {
                plato.setBaja(new Date());
                platoRepositorio.save(plato);
            }
        } else {
            throw new ErrorServicio("El plato no existe");
        }
    }

    @Transactional
    public Plato buscarPlatoPorId(String id) throws ErrorServicio {
        Optional<Plato> respuesta = platoRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Plato plato = respuesta.get();
            return plato;
        } else {
            throw new ErrorServicio("No se encontro el plato solicitado");
        }
    }

    @Transactional
    public List<Plato> buscarPorUsuario(String id) {
        return platoRepositorio.buscarPorUsuario(id);
    }

    @Transactional
    public List<Plato> buscarPorTipoPlato(TipoComida tipo) throws ErrorServicio {
        List<Plato> platos = null;
        platos = platoRepositorio.buscarPorPlato(tipo);

        if (platos != null) {
            return platos;
        } else {
            throw new ErrorServicio("No se encontro el plato solicitado");
        }
    }

    @Transactional
    public List<Plato> buscarPlatoX() throws ErrorServicio {
        List<Plato> platos = null;
        platos = platoRepositorio.buscarPlatosX();

        if (platos != null) {
            return platos;
        } else {
            throw new ErrorServicio("No se encontro el plato solicitado");
        }
    }

    public void validacion(String comentario, Integer valoracion, TipoComida tipoComida, String nombre) throws ErrorServicio {

        if (comentario == null || comentario.isEmpty()) {
            throw new ErrorServicio("Debe ingresar un comentario.");
        }
        if (valoracion == null) {
            throw new ErrorServicio("Debe indicar una valoracion.");
        }
        if (valoracion<=0 || valoracion>10) {
            throw new ErrorServicio("La valoracion debe ser del 1 al 10.");
        }
        if (tipoComida == null) {
            throw new ErrorServicio("Debe seleccionar un tipo de comida.");
        }
        if (nombre == null) {
            throw new ErrorServicio("Debe seleccionar un tipo de comida.");
        }
    }
}
