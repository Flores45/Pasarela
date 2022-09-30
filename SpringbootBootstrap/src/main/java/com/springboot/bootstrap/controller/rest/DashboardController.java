package com.springboot.bootstrap.controller.rest;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.*;

import com.springboot.bootstrap.repository.*;

@RestController
@RequestMapping(value = "api/dashboard", produces = "application/json")
public class DashboardController {
    

    private final PagoRepository pagosData;

    public DashboardController(PagoRepository pagosData){
        this.pagosData = pagosData;
    } 

    @GetMapping(value = "/tarjetastotales", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> tarjetas(){
        return new ResponseEntity<List<Map<String, Object>>>(
            pagosData.querySumaTotal(), HttpStatus.OK);
    }
}
