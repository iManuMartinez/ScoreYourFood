
package com.ScoreYourFood.demo.Repository;

import com.ScoreYourFood.demo.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {
    @Query("SELECT c FROM Usuario c WHERE c.email = :email AND c.estado = true")
    public Usuario buscarPorMail(@Param("email") String mail);
}
