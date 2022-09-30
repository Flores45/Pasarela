package com.springboot.bootstrap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

import com.springboot.bootstrap.model.Register;
import com.springboot.bootstrap.repository.RegisterRepository;
import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.repository.UsuarioRepository;

@Controller
public class RegisterController {
    
    private static final String INDEX ="Register/Register"; 
    private static String MODEL_CONTACT="registrar";
    private final RegisterRepository registerData;
    private final UsuarioRepository usuariosData;

    public RegisterController(RegisterRepository registerData,
        UsuarioRepository usuariosData    
        ){
        this.registerData = registerData;
        this.usuariosData = usuariosData;
    }      

    @GetMapping("/Register/Register")
    public String index(Model model) {
        model.addAttribute(MODEL_CONTACT, new Register());
        return INDEX;
    }    

    @PostMapping("/Register/Register")
    public String create(Model model, 
        @Valid Register objRegister, BindingResult result ){
        if(result.hasFieldErrors()) {
            model.addAttribute("mensaje", "No se registro con exito");
        }else{
            Usuario user = objRegister.getUser();
            user.setTipoUsuario("R");
            this.usuariosData.save(user);
            this.usuariosData.flush();
            this.registerData.save(objRegister);
            model.addAttribute(MODEL_CONTACT, objRegister);
            model.addAttribute("mensaje", "Se registro con exito");
        }
        return INDEX;
    }

}