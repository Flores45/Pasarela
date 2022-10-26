package com.springboot.bootstrap.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.service.IUsuarioService;
import com.springboot.bootstrap.util.Methods;
import org.springframework.ui.Model;
@Controller
public class HomeController {

    private static final String VIEW_OPTION ="Dash/opcionDash"; 
    private static final String VIEW_DASHBOARD ="dashboard"; 

    @GetMapping("/Dash/opcionDash")
    public String opcionDash(Model model) {
        return VIEW_OPTION;
    }
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return VIEW_DASHBOARD;
    }
    
    @Autowired
    private IUsuarioService _dataUsuarios;

 
    @GetMapping("/")
    public String index(Authentication auth, HttpSession session){

        if(auth != null){
            String username = auth.getName();
            Usuario usuario = _dataUsuarios.findByUsername(username);
            usuario.setPassword(null);
            session.setAttribute("usuario", usuario);
            return "inicio";
        }

        return "redirect:/usuario/login";
    }


}
