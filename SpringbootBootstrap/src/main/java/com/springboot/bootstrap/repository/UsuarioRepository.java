package com.springboot.bootstrap.repository;

import org.springframework.stereotype.Repository;
import com.springboot.bootstrap.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UsuarioRepository  extends JpaRepository<Usuario,String> {

    
    
}
