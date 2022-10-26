package com.springboot.bootstrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.bootstrap.model.Token;

public interface TokenRepository extends JpaRepository<Token, Integer>{
    
    @Query("SELECT t FROM Token t WHERE t.usuario.id = :usuarioId AND NOW() BETWEEN t.validSince AND t.dueDate")
    Token findTokenVigenteUsuario(@Param("usuarioId") Integer usuarioId);

    @Query("SELECT t FROM Token t WHERE t.token = :token AND NOW() BETWEEN t.validSince AND t.dueDate")
    Token findTokenVigenteByToken(@Param("token") String token);
}
