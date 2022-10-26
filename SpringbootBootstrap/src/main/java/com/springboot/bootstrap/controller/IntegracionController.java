package com.springboot.bootstrap.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.bootstrap.model.Token;
import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.repository.TokenRepository;
import com.springboot.bootstrap.service.IUsuarioService;
import com.springboot.bootstrap.util.Methods;

@Controller
@RequestMapping("/integracion")
public class IntegracionController {
    
    @Autowired
    private IUsuarioService _dataUsuarios;

    @Autowired
    private TokenRepository _dataTokens;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(Model model, Authentication auth){

        Usuario u = _dataUsuarios.findByUsername(auth.getName());
        Token apikey = _dataTokens.findTokenVigenteUsuario(u.getId());

        if(apikey != null){
            apikey.setToken(Methods.decodeBase64(apikey.getToken()));
        }

        model.addAttribute("apikey", apikey);
        return "integracion/index";

    }

    @RequestMapping(value = "/crearAPIKey", method = RequestMethod.POST)
    public String crearAPIKey(Model model, Authentication auth, RedirectAttributes redirectAttributes){

        Usuario u = _dataUsuarios.findByUsername(auth.getName());
        Token apikey = _dataTokens.findTokenVigenteUsuario(u.getId());

        if(u != null){

            if(apikey != null){
                redirectAttributes.addFlashAttribute("status", "error");
                redirectAttributes.addFlashAttribute("mensaje", "YA TIENE SU API KEY");
            }else{

                Token t = new Token();
                t.setToken(Methods.generarCadenaAleatoria(32));
                t.setToken(Methods.encodeBase64(t.getToken()));
                t.setCreateDate(new Date());
                t.setValidSince(new Date());

                long validez = Methods.generarAleatorio(15L*24*60*60*1000,90L*24*60*60*1000); //Dando validez entre 15 y 90 días provisionalmente.

                t.setDueDate(new Date(System.currentTimeMillis() + validez));
                t.setUsuario(u);
                _dataTokens.save(t);

                redirectAttributes.addFlashAttribute("status", "success");
                redirectAttributes.addFlashAttribute("mensaje", "TOKEN GENERADO");

            }
        }
        return "redirect:/";

    }

}
