package com.springboot.bootstrap.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ModelPagoAbono {
    
    private String nroTarjeta;
    private String dueMonth;
    private String dueYear;
    private String cvv;
    private String nombre;
    private String moneda;
    private Double monto;
    private Double tcCompra;
    private Double tcVenta;

}
