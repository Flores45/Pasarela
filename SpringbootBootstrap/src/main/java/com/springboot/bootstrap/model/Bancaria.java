package com.springboot.bootstrap.model;
import javax.persistence.*;
import lombok.*;
@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "t_banco")
public class Bancaria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombreBancaria;
    private Integer numeroCuenta;
    private String nombreBanco; 
    private Integer dni;         
    private Integer monto;
}
