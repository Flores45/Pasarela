package com.springboot.bootstrap.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.bootstrap.integration.reniec.ReniecApi;
import com.springboot.bootstrap.integration.reniec.UserReniec;
import com.springboot.bootstrap.model.Tarjeta;
import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.repository.TarjetaRepository;
import com.springboot.bootstrap.service.IUsuarioService;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private IUsuarioService _dataUsuarios;  

    @Autowired
    private ReniecApi _reniecApi;

    @Autowired
    private TarjetaRepository _dataTarjetas;

    @RequestMapping(value = "/validar", method = RequestMethod.GET)
    public String validarDNI(Model model){
        return "usuario/validar";
    }

    @RequestMapping(value = "/validar", method = RequestMethod.POST)
    public String validarDNI(Model model, @Valid String dni, RedirectAttributes redirectAttributes){
        
        UserReniec usuarioReniec = null; 
        usuarioReniec = _reniecApi.findExitsUserByDni(dni);
        
        if(usuarioReniec != null){
            
            if(_dataUsuarios.findByUsername(dni) == null){

                Usuario usuario = new Usuario();
                usuario.setUsername(dni);
                usuario.setNombres(usuarioReniec.getNombres());
                usuario.setApellidos(usuarioReniec.getApePat() + " " + usuarioReniec.getApeMat());

                redirectAttributes.addFlashAttribute("usuario", usuario);
                redirectAttributes.addFlashAttribute("mensaje", "DNI CORRECTO");
                return "redirect:/usuario/Register";

            }else{

                model.addAttribute("mensaje", "YA EXISTE UN USUARIO CON ESE DNI");
                return "usuario/validar";

            }

        }else{

            model.addAttribute("mensaje", "EL DNI NO EXISTE");
            return "usuario/validar";
        }

    }

    @RequestMapping(value = "/Register", method = RequestMethod.GET)
    public String create(Model model) {
        model.addAttribute("title", "Registro de usuario");
        
        /*Usuario usuario = new Usuario();
        usuario.setUsername("Admin123");
        usuario.setTipoUsuario("A");
        model.addAttribute("usuario", usuario);
        */ //Descomentar para registrar un administrador 
        return "usuario/Register";
    } 
    
    @RequestMapping(value = "/Register", method = RequestMethod.POST)
    public String createPost(Model model, @Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirectAttributes ){
        model.addAttribute("title", "Registro de usuario");
        if(result.hasFieldErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "ERROR, NO SE REGISTRO");
            return "redirect:/usuario/Register";
        }else{
            _dataUsuarios.registrar(usuario);
            model.addAttribute("usuario", usuario);

            List<Tarjeta> listaTarjetasGuardadas = _dataTarjetas.findByDniAndUserNull(usuario.getUsername());

            if(listaTarjetasGuardadas.size() > 0){

                for(Tarjeta t : listaTarjetasGuardadas){
                    t.setUsuario(usuario);
                    _dataTarjetas.save(t);
                }

            }

        }
        return "redirect:/usuario/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model) {
        model.addAttribute("title", "Inicio de sesión");
        model.addAttribute("usuario", new Usuario());
        return "usuario/login";
    }  



}
