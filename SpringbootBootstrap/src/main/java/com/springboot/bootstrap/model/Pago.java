package com.springboot.bootstrap.model;

import javax.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_pago")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Integer id;
    private String nombreTarjeta;
    private Integer numeroTarjeta;
    private Integer fechaVencimiento;
    private String tipoTarjeta;
    private Integer codigoCcv;
    private Integer dni;
    private Integer monto;
    
    
    

}
