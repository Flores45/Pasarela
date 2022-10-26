package com.springboot.bootstrap.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelActivarTarjeta {
    
    private String nroTarjeta;
    private String dueMonth;
    private String dueYear;
    private String cvv;
    private String nombre;
    private boolean active;

}
