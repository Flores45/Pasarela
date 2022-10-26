package com.springboot.bootstrap.service;

import org.springframework.stereotype.Service;

import com.springboot.bootstrap.model.Usuario;

@Service
public interface IUsuarioService {
    public Usuario findByUsername(String username);
    public Usuario registrar(Usuario u);
}
