package com.springboot.bootstrap.controller;

import com.springboot.bootstrap.model.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import com.springboot.bootstrap.repository.*;


import java.util.*;




@RestController
@RequestMapping(value ="api/pasarela", produces ="application/json")
public class PasarelaController {


    private final PagoRepository pagoData;
    

public PasarelaController(PagoRepository pagoData){

  
  this.pagoData = pagoData;


}
//Tarjeta
@PostMapping(value = "/{createTarjeta}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity <String> createTarjeta(@RequestBody Pago pa){

  pagoData.save(pa);
  pagoData.flush();
    
    return new ResponseEntity<String>(pa.getNumeroTarjeta(), HttpStatus.CREATED);

}
@GetMapping(value = "/{numeroTarjeta}", produces = MediaType.APPLICATION_JSON_VALUE)
public ResponseEntity<Pago> NumTarjeta(@PathVariable String numeroTarjeta){
    Optional<Pago> optPago = pagoData.findByNumTarjeta(numeroTarjeta);
    if(optPago.isPresent()){
        Pago pa = optPago.get();
        return new ResponseEntity<Pago>(pa, HttpStatus.OK);
    }else{
        return new ResponseEntity<Pago>(HttpStatus.NOT_FOUND);
    }
} 



}