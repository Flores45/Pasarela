package com.springboot.bootstrap.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;

import java.io.Serializable;

import javax.persistence.Column;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_usuario")
public class Usuario implements Serializable{
    @Id
    @Column(name="user_id")
    private String userID;
    private String password;
    private String tipoUsuario;
}
