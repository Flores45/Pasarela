package com.springboot.bootstrap.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_OpcionesPago")
public class OpcionesPago {
    @Id
    @Column(name="opciones_id")
    private String empresa;
    private String metodo;


}
