package com.springboot.bootstrap.model;


import javax.persistence.*;

import javax.validation.constraints.Size;

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
    /*@Length(min=16,  message="El Numero de Digitos son 16 ")*/
    @Size(min = 16, message="EL NUMERO DE LA TARJETA DEBE TENER 16 DIGITOS ANIMAL!!!")
    private String numeroTarjeta;

    private String fechaVencimiento;

    private String tipoTarjeta;

    @Size(min = 3,  message="EL CODIGO CVV TIENE 3 DIGITOS BABOSO")
    private String codigoCcv;

    @Size(min = 8, max = 8, message="SU DNI  CONTIENE 8 DIGITOS BURRO DE MRD")
    private String dni;
    private Integer monto;
    
    
    
    

}
