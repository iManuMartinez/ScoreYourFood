package com.ScoreYourFood.demo.Controladores;

import com.ScoreYourFood.demo.Entidades.Bar;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.BarRepository;
import com.ScoreYourFood.demo.Services.BarServices;
import com.ScoreYourFood.demo.Services.PlatoServices;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/bar")
@PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
public class BarControlador {

    @Autowired
    private BarRepository barRepositorio;

    @Autowired
    private BarServices barServicio;

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/editar-perfil")
    public String editarPerfil(HttpSession session, @RequestParam(required = false) String id, ModelMap model) throws ErrorServicio {
        Bar bar = new Bar();
        model.put("perfil", bar);
        return "bar.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @PostMapping("/actualizar-perfil")
    public String registrar(ModelMap modelo, HttpSession session, @RequestParam String nombre, @RequestParam String calle, @RequestParam Integer numero) throws ErrorServicio {
        try {
            barServicio.agregarPlato(nombre, calle, numero);
            return "redirect:/inicio";
        } catch (ErrorServicio ex) {
            Bar bar = new Bar();
            bar.setBar(nombre);
            bar.setCalle(calle);
            bar.setNumero(numero);
            modelo.put("perfil", bar);
            modelo.put("error", ex.getMessage());
            return "bar.html";
        }
    }
}
