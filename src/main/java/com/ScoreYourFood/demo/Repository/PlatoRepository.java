package com.ScoreYourFood.demo.Repository;

import com.ScoreYourFood.demo.Entidades.Plato;
import com.ScoreYourFood.demo.Enum.TipoComida;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatoRepository extends JpaRepository<Plato, String> {

    @Query("SELECT c FROM Plato c WHERE c.tipo = :tipo AND c.baja IS NULL")
    public List<Plato> buscarPorPlato(@Param("tipo") TipoComida tipo);

    @Query("SELECT c FROM Plato c WHERE c.usuario.id = :id AND c.baja IS NULL")
    public List<Plato> buscarPorUsuario(@Param("id") String id);

    @Query(value = "SELECT * FROM scoreyourfood.plato WHERE baja IS NULL LIMIT 15;",nativeQuery = true)
    public List<Plato> buscarPlatosX();
}
