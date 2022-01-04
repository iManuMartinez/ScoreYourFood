package com.ScoreYourFood.demo.Controladores;

import com.ScoreYourFood.demo.Entidades.Usuario;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Services.UsuarioServices;
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
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioServices usuarioServicio;

    @GetMapping("/")
    public String index() {
        return "index.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_USUARIO_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio() {
        return "inicio.html";
    }

    @GetMapping("/login")
    public String login(HttpSession session, @RequestParam(required = false) String error, @RequestParam(required = false) String logout, ModelMap model) {

//        Usuario login = (Usuario) session.getAttribute("usuariosession");
//        if (login != null) {
//            return "redirect:/inicio";
//        }
//        
        if (error != null) {
            model.put("error", "Usuario o clave incorrecta");
        }
        if (logout != null) {
            model.put("logout", "Ha salido correctamente");
        }
        return "login.html";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login != null) {
            return "redirect:/inicio";
        }
        return "registro.html";
    }

    @PostMapping("/registrar")
    public String registrar(HttpSession session, ModelMap modelo, MultipartFile archivo, @RequestParam String nombre, @RequestParam String apellido, @RequestParam String email, @RequestParam String clave, @RequestParam String clave2) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login != null) {
            return "redirect:/inicio";
        }

        try {
            usuarioServicio.registrar(archivo, nombre, apellido, email, clave, clave2);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("apellido", apellido);
            modelo.put("email", email);
            modelo.put("clave", clave);
            modelo.put("clave2", clave2);
            return "registro.html";
        }
        modelo.put("titulo", "Bienvenido a Score Your Food");
        modelo.put("descripcion", "Se ha registrado de manera exitosa");
        return "exito.html";
    }

    @GetMapping("/recuperar")
    public String ModificarClave() throws ErrorServicio {

        return "MandarMail.html";
    }

    @PostMapping("/enviar-codigoDeSeguridad")
    public String CodigoSeguridad(ModelMap modelo, HttpSession session, @RequestParam String email) {
        
        try {
            usuarioServicio.codigoSeguridad(email);
            return "RecuperarClave.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "MandarMail.html";
        }

    }

    @PostMapping("/recuperar-clave")
    public String ModificarClave(ModelMap modelo, HttpSession session, @RequestParam String email, @RequestParam String clave, @RequestParam String clave2, @RequestParam Integer codigo) throws ErrorServicio {

        try {
            usuarioServicio.recuperarClave(email, clave, clave2, codigo);
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("email", email);
            return "RecuperarClave.html";
        }
        modelo.put("titulo", "Cambio de contraseña realizado");
        modelo.put("descripcion", "Se ha cambiado la contraseña de manera exitosa");
        return "exito.html";
    }

}
