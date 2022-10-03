package com.springboot.bootstrap.controller;

import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.repository.UsuarioRepository;

import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import javax.validation.Valid;

@Controller
public class UsuarioController {
    

    private static final String INDEX = "login";
    private static String MODEL_CONTACT="user";
    private static String MODEL_MESSAGE="mensaje";
    private final UsuarioRepository usuariosData;

    public UsuarioController(UsuarioRepository usuariosData){
        this.usuariosData = usuariosData;
    }   

    @GetMapping("login")
    public String login(Model model) {
        model.addAttribute("user", new Usuario());
        return INDEX;
    }
    @PostMapping("login")
    public String LoginPost(Model model, @Valid Usuario objUser, HttpServletRequest request, 
    BindingResult result ){
        String page=INDEX;
            model.addAttribute(MODEL_CONTACT, new Usuario());
            if(result.hasFieldErrors()) {
                model.addAttribute(MODEL_MESSAGE, "No se ha podido Ingresar Correctamente");
            }else{
                Optional<Usuario> userDB = this.usuariosData.findById(objUser.getUserID());
                if(userDB.isPresent()){
                    if(userDB.get().getPassword().equals(objUser.getPassword())){
                        model.addAttribute(MODEL_CONTACT,userDB.get());
                        model.addAttribute(MODEL_MESSAGE, "Usuario existe");
                        request.getSession().setAttribute("user", objUser);{}
                        page="inicio";  
                    }else{
                        model.addAttribute(MODEL_MESSAGE, "Password no coincide");  
                    }
                }else{
                    model.addAttribute(MODEL_MESSAGE, "Usuario no existe");
                }
            }
            return page;             
       
    }
    @GetMapping("/Usuario/Logout")
	public String logout(HttpServletRequest request) {
		request.getSession().invalidate();
		return "redirect:/login";
	}
}
