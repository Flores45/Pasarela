package com.springboot.bootstrap.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.springboot.bootstrap.model.Contacto;
import com.springboot.bootstrap.repository.ContactoRepository;
@Controller
public class ContactoController {
     private static final String INDEX = "Contacto/Contacto";
     private static String MODEL_CONTACT="contact";
     private final ContactoRepository contactsData;
     public ContactoController(ContactoRepository contactsData){
        this.contactsData = contactsData;
    }    

     @GetMapping("/Contacto/Contacto")
     public String index(Model model) {
         model.addAttribute(MODEL_CONTACT, new Contacto());
         return INDEX;
     }
     @PostMapping("/Contacto/Contacto")
    public String createSubmitForm(Model model, 
        @Valid Contacto objContact, BindingResult result ){
        if(result.hasFieldErrors()) {
            model.addAttribute("mensaje", "NO SE REGISTRO SU CONSULTA");
        }else{
            this.contactsData.save(objContact);
            model.addAttribute(MODEL_CONTACT, objContact);
            model.addAttribute("mensaje", "SE REGISTRO SU CONSULTA");
        }
        return INDEX;
    }
     
}
