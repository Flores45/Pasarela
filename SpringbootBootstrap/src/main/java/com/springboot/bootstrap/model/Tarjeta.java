package com.springboot.bootstrap.model;

import lombok.*;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_tarjeta")
public class Tarjeta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(updatable = false, unique = true)
    @NotNull
    private String credenciales;

    @Column(updatable = false)
    @NotNull
    private Date dueDate;

    @Column(columnDefinition = "char(1)", updatable = false)
    @NotNull
    private String tipo;

    @NotNull
    private boolean active;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn
    private Usuario usuario;

    @Column(columnDefinition = "char(3)", updatable = false)
    @NotNull
    private String moneda;

    @Column(columnDefinition = "numeric(18,2)")
    @NotNull
    private Double saldo;

    @Column(columnDefinition = "numeric(18,2)")
    @NotNull
    private Double limDiario;

    @Column(columnDefinition = "char(8)", updatable = false)
    @NotNull
    private String dni;

}
