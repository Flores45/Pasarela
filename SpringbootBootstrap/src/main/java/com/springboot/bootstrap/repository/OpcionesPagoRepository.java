package com.springboot.bootstrap.repository;

import com.springboot.bootstrap.model.OpcionesPago;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OpcionesPagoRepository extends JpaRepository<OpcionesPago,String> {
    
}
