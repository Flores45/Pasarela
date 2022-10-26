package com.springboot.bootstrap.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.springboot.bootstrap.model.Pago;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer>{
    @Query("SELECT sum(p.monto) FROM Pago p WHERE TO_CHAR(p.fechaHora, 'yyyy-MM-dd') = TO_CHAR(NOW(), 'yyyy-MM-dd') AND p.tarjeta.id = :tarjetaId")
    Double getSumMontoTarjetaHoy(@Param("tarjetaId") Integer tarjetaId);

    @Query("SELECT p FROM Pago p WHERE p.tarjeta.id = :tarjetaId AND p.fechaHora BETWEEN TO_TIMESTAMP(:fechaInicio, 'yyyy-MM-dd HH24:mi:ss') AND TO_TIMESTAMP(:fechaFin, 'yyyy-MM-dd HH24:mi:ss') ORDER BY p.fechaHora DESC")
    List<Pago> getPagosTarjeta(@Param("tarjetaId") Integer tarjetaId, @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin);

   
    @Query("SELECT tipo as tipo, SUM(p.id) as id FROM Tarjeta p GROUP BY p.tipo")
    List<Map<String, Object>> querySumaTotal();


}
