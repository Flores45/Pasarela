package com.springboot.bootstrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.bootstrap.model.Tarjeta;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer>{

    @Query(value = "SELECT t FROM Tarjeta t WHERE t.credenciales = :credenciales")
    Tarjeta findByCredenciales(@Param("credenciales") String credenciales);

    @Query(value = "SELECT t FROM Tarjeta t WHERE t.usuario.id = :usuarioId AND NOW() <= t.dueDate")
    List<Tarjeta> findByUsuario(@Param("usuarioId") Integer usuarioId);

    @Query(value = "SELECT t FROM Tarjeta t WHERE t.dni = :dni AND t.usuario IS NULL")
    List<Tarjeta> findByDniAndUserNull(@Param("dni") String dni);

}
