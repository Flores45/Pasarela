package com.springboot.bootstrap.model;

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

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_request")
public class Request {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(optional = false, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(updatable = false)
    @NotNull
    private Token token;

    @Column(updatable = false)
    @NotNull
    private Date fechaHora;

    @Column(updatable = false)
    @NotNull
    private String httpMethod;

    @Column(updatable = false)
    @NotNull
    private String action;

    @Column(updatable = false)
    @NotNull
    private String status;

}
