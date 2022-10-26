package com.springboot.bootstrap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.bootstrap.model.Request;

public interface RequestRepository extends JpaRepository<Request, Integer>{
    
}
