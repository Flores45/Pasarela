package com.springboot.bootstrap.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelTarjetaCreate {

    private String dni;
    private String nombre;
    private String moneda;
    private String tipo;

}
