package com.ScoreYourFood.demo.Services;

import com.ScoreYourFood.demo.Entidades.Foto;
import com.ScoreYourFood.demo.Errores.ErrorServicio;
import com.ScoreYourFood.demo.Repository.UsuarioRepository;
import com.ScoreYourFood.demo.Entidades.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServices implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FotoServices fotoServicio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {
        String id = null;
        validar(id,nombre, apellido, mail, clave, clave2);
        
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(mail);
        usuario.setEstado(true);
        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);
        String encriptada = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptada);
        usuarioRepository.save(usuario);
        notificacionServicio.enviar("Bienvenido a Score Your Food", "Score Your Food", usuario.getEmail());
    }

    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {

        validar(id,nombre, apellido, mail, clave, clave2);

        Optional<Usuario> respuesta = usuarioRepository.findById(id);
        if (respuesta.isPresent()) {

            Usuario usuario = respuesta.get();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setEmail(mail);

            String encriptida = new BCryptPasswordEncoder().encode(clave);
            usuario.setClave(encriptida);

            String idFoto = null;
            if (usuario.getFoto() != null) {
                idFoto = usuario.getFoto().getId();
            }

            Foto foto = fotoServicio.actualizar(idFoto, archivo);
            usuario.setFoto(foto);

            usuarioRepository.save(usuario);

        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    public void codigoSeguridad(String email) throws ErrorServicio {
        Usuario usuario = buscarPorEmail(email);
        if (usuario != null) {
            Integer numero = (int) (Math.random() * (999999 - 100000 + 1) + 100000);

            usuario.setVerificacion(numero);
            usuarioRepository.save(usuario);

            notificacionServicio.enviar("Su codigo de verificacion es: " + usuario.getVerificacion(), "Score Your Food", usuario.getEmail());
        }else{
            throw new ErrorServicio("El mail no se encuentra registrado");
        }

    }

    @Transactional
    public void recuperarClave(String email, String clave, String clave2, Integer codigo) throws ErrorServicio {

        Usuario usuario = usuarioRepository.buscarPorMail(email);
        if (usuario == null) {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }

        if (email == null || email.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }

        if (!Objects.equals(codigo, usuario.getVerificacion())) {
            throw new ErrorServicio("No es el codigo de seguridad");
        }

        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("El contraseña no puede ser nula y tiene que tener mas de 6 digitos");
        }
        if (clave2 == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("El contraseña no puede ser nula y tiene que tener mas de 6 digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las contraseñas no coinciden.");
        }

        String encriptida = new BCryptPasswordEncoder().encode(clave);
        usuario.setClave(encriptida);

        usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario buscarUsuarioPorId(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepository.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            return usuario;
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    @Transactional
    public Usuario buscarPorEmail(String mail) throws ErrorServicio {
        Usuario usuario = null;
        usuario = usuarioRepository.buscarPorMail(mail);

        if (usuario != null) {
            return usuario;
        } else {
            throw new ErrorServicio("Email incorrecto");
        }
    }

    @Transactional
    public void eliminar(String id) throws ErrorServicio {
        Optional<Usuario> respuesta = usuarioRepository.findById(id);
        if (respuesta.isPresent()) {
            Usuario usuario = respuesta.get();
            usuario.setEstado(false);
            usuarioRepository.delete(usuario);
        } else {
            throw new ErrorServicio("No se encontro el usuario solicitado");
        }
    }

    private void validar(String id,String nombre, String apellido, String mail, String clave, String clave2) throws ErrorServicio {

        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("El nombre del usuario no puede ser nulo");
        }
        if (apellido == null || apellido.isEmpty()) {
            throw new ErrorServicio("El apellido del usuario no puede ser nulo");
        }
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("El mail del usuario no puede ser nulo");
        }
        if (clave == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("El contraseña no puede ser nula y tiene que tener mas de 6 digitos");
        }
        if (clave2 == null || clave.isEmpty() || clave.length() <= 6) {
            throw new ErrorServicio("El contraseña no puede ser nula y tiene que tener mas de 6 digitos");
        }
        if (!clave.equals(clave2)) {
            throw new ErrorServicio("Las contraseñas no coinciden.");
        }
        Usuario usuario = null;
        usuario = usuarioRepository.buscarPorMail(mail);
        if (usuario != null && !usuario.getId().equals(id)) {
            throw new ErrorServicio("El mail ya se encuentra registrado.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.buscarPorMail(email);
        if (usuario != null && usuario.getEstado() == true) {
            List<GrantedAuthority> permisos = new ArrayList<>();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_USUARIO_REGISTRADO");
            permisos.add(p1);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);

            User user = new User(usuario.getEmail(), usuario.getClave(), permisos);
            return user;
        } else {
            return null;
        }
    }
}
