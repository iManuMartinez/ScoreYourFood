package com.ScoreYourFood.demo.Controladores;

import com.ScoreYourFood.demo.Entidades.Bar;
import com.ScoreYourFood.demo.Entidades.Plato;
import com.ScoreYourFood.demo.Entidades.Usuario;
import com.ScoreYourFood.demo.Enum.TipoComida;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.BarRepository;
import com.ScoreYourFood.demo.Services.PlatoServices;
import com.ScoreYourFood.demo.Services.UsuarioServices;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/plato")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class PlatoControlador {

    @Autowired
    private BarRepository barRepositorio;

    @Autowired
    private PlatoServices platoServicio;

    @Autowired
    private UsuarioServices usuarioServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam(required = false) String id, @RequestParam(required = false) String accion, ModelMap model) {

        if (accion == null) {
            accion = "Crear";
        }

        Usuario login = (Usuario) session.getAttribute("usuariosession");

        if (login == null) {
            return "redirect:/";
        }

        Plato plato = new Plato();
        if (id != null && !id.isEmpty()) {
            try {
                plato = platoServicio.buscarPlatoPorId(id);
            } catch (ErrorServicio ex) {
                model.put("error", ex.getMessage());
            }
        }
        List<Bar> bares = barRepositorio.findAll();
        model.put("accion", accion);
        model.put("bares", bares);
        model.put("perfil", plato);
        model.put("tipos", TipoComida.values());

        return "plato.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String registrar(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String comentario, @RequestParam Integer valoracion, @RequestParam TipoComida tipo, @RequestParam String bar) throws ErrorServicio {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/";
        }
        try {
            if (id == null || id.isEmpty()) {
                platoServicio.agregarPlato(archivo, login.getId(), comentario, valoracion, tipo, nombre, bar);
            } else {
                platoServicio.modificar(archivo, login.getId(), id, comentario, valoracion, tipo, nombre, bar);
            }
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            Plato plato = new Plato();
            if (id != null && !id.isEmpty()) {
                plato = platoServicio.buscarPlatoPorId(id);
                plato.setNombre(nombre);
                plato.setComentario(comentario);
                plato.setValoracion(valoracion);
                modelo.put("accion", "Actualizar");
            }
            List<Bar> bares = barRepositorio.findAll();
            modelo.put("accion", "Crear");
            modelo.put("bares", bares);
            modelo.put("tipos", TipoComida.values());
            modelo.put("perfil", plato);
            modelo.put("error", ex.getMessage());
            return "plato.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/eliminar-perfil")
    public String eliminar(HttpSession session, @RequestParam String id) {
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            platoServicio.eliminarPlato(login.getId(), id);
        } catch (ErrorServicio ex) {
            Logger.getLogger(PlatoControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/plato/mis-platos";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/lista-platos")
    public String listaPlatos(ModelMap model) throws ErrorServicio {
        List<Plato> platos = platoServicio.buscarPlatoX();
        model.put("platos", platos);
        return "lista.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/ver-platos")
    public String verPlatos(ModelMap model, @RequestParam TipoComida tipo) throws ErrorServicio {
        List<Plato> platos = platoServicio.buscarPorTipoPlato(tipo);
        model.put("platos", platos);
        model.put("tipo", tipo);
        return "lista.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/mis-platos")
    public String misPlatos(HttpSession session, ModelMap model) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/";
        }

        List<Plato> platos = platoServicio.buscarPorUsuario(login.getId());
        model.put("platos", platos);
        model.put("usuario", login);
        return "misplatos.html";
    }

    @GetMapping("/platos-usuario")
    public String platosUsuarios(HttpSession session, @RequestParam String id, ModelMap model) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null) {
            return "redirect:/";
        }
        Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
        List<Plato> platos = platoServicio.buscarPorUsuario(id);

        model.put("usuario", usuario);
        model.put("platos", platos);
        return "misplatos.html";
    }

}
