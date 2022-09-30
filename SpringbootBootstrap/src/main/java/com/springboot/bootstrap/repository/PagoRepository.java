package com.springboot.bootstrap.repository;



import org.springframework.stereotype.*;
import org.springframework.data.jpa.repository.JpaRepository;
import com.springboot.bootstrap.model.*;

import java.util.*;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>{

    @Query("SELECT o FROM Pago o WHERE o.numeroTarjeta=?1")
    Optional<Pago> findByNumTarjeta(Integer numeroTarjeta);
    
    
    @Query("SELECT tipoTarjeta as tipoTarjeta, SUM(p.monto) as monto FROM Pago p GROUP BY p.tipoTarjeta")
    List<Map<String, Object>> querySumaTotal();



}
