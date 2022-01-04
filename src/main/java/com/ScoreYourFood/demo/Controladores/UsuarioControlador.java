package com.ScoreYourFood.demo.Controladores;

import com.ScoreYourFood.demo.Entidades.Plato;
import com.ScoreYourFood.demo.Entidades.Usuario;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.UsuarioRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/usuario")
public class UsuarioControlador {

    @Autowired
    private UsuarioServices usuarioServicio;

    @Autowired
    private UsuarioRepository usuarioRepositorio;

    @Autowired
    private PlatoServices platoServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap model) {

        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            Usuario usuario = usuarioServicio.buscarUsuarioPorId(id);
            model.addAttribute("perfil", usuario);
            return "perfil.html";
        } catch (ErrorServicio e) {
            System.out.println("gola");
            System.out.println(e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "perfil.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String registrar(ModelMap modelo, HttpSession session, MultipartFile archivo, @RequestParam String id, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String clave, @RequestParam String clave2) {

        Usuario usuario = null;
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            usuario = usuarioServicio.buscarUsuarioPorId(id);
            usuarioServicio.modificar(archivo, id, nombre, apellido, email, clave, clave2);
            session.setAttribute("usuariosession", usuario);
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            System.out.println(ex.getMessage());
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", usuario);
            return "perfil.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/eliminar-perfil")
    public String eliminar(HttpSession session) {
        try {
            Usuario login = (Usuario) session.getAttribute("usuariosession");
            List<Plato> platos = platoServicio.buscarPorUsuario(login.getId());

            if (platos != null) {
                for (Plato plato : platos) {
                    platoServicio.eliminarPlato(plato.getUsuario().getId(), plato.getId());
                }
            }
            usuarioServicio.eliminar(login.getId());

        } catch (ErrorServicio ex) {
            Logger.getLogger(UsuarioControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "redirect:/logout";
    }
}
