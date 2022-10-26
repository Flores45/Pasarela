package com.springboot.bootstrap.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelRespuestaSaldo {
    
    private String status;
    private String moneda;
    private Double saldo;
    private boolean active;
    private String mensaje;

}
