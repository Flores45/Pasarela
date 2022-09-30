package com.springboot.bootstrap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;



import com.springboot.bootstrap.model.*;

@Controller
public class OpcionesPagoController {




    private static final String INDEX ="Pago/OpcionesPago"; 
    private static String MODEL_CONTACT="opciones";
  

    @GetMapping("/Pago/OpcionesPago")
    public String index(Model model) {
        model.addAttribute(MODEL_CONTACT, new OpcionesPago());
        return INDEX;
    }  


}
