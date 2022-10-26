package com.springboot.bootstrap.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.bootstrap.integration.reniec.ReniecApi;
import com.springboot.bootstrap.integration.reniec.UserReniec;
import com.springboot.bootstrap.model.Pago;
import com.springboot.bootstrap.model.Tarjeta;
import com.springboot.bootstrap.model.Usuario;
import com.springboot.bootstrap.model.dto.ModelPagoAbono;
import com.springboot.bootstrap.model.dto.ModelRespuestaPagoAbono;
import com.springboot.bootstrap.model.dto.ModelTarjetaCreate;
import com.springboot.bootstrap.model.dto.ModelTarjetaEdit;
import com.springboot.bootstrap.repository.PagoRepository;
import com.springboot.bootstrap.repository.TarjetaRepository;
import com.springboot.bootstrap.service.IUsuarioService;
import com.springboot.bootstrap.util.Constants;
import com.springboot.bootstrap.util.Methods;

@Controller
@RequestMapping("/tarjeta")
public class TarjetaController {
    
    @Autowired
    private ReniecApi _reniecApi;

    @Autowired
    private IUsuarioService _dataUsuarios;

    @Autowired
    private RestTemplate _restTemplate;

    @Autowired
    private TarjetaRepository _dataTarjetas;

    @Autowired
    private PagoRepository _dataPagos;

    @Secured("ROLE_ADMIN")
    @GetMapping("/validarDNI")
    public String validarDNI(Model model){
        return "tarjeta/formDNI";
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/validarDNI")
    public String validarDNI(Model model, @Valid String dni, RedirectAttributes redirectAttributes){
        
        UserReniec usuario = null; 
        usuario = _reniecApi.findExitsUserByDni(dni);
        
        if(usuario != null){
        
            ModelTarjetaCreate tarjeta = new ModelTarjetaCreate();
            tarjeta.setDni(dni);
            
            String nombre = "";

            if(usuario.getNombres().split(" ")[0].length() < 7){
                nombre = usuario.getNombres().split(" ")[0];
            }else{
                nombre = usuario.getNombres().charAt(0) + ".";
            }

            nombre += " " + usuario.getApePat() + " " + usuario.getApeMat().charAt(0) + ".";
            nombre.toUpperCase();
            tarjeta.setNombre(nombre);

            redirectAttributes.addFlashAttribute("tarjeta", tarjeta);
            redirectAttributes.addFlashAttribute("mensaje", "DNI CORRECTO");
            return "redirect:/tarjeta/create";

        }else{

            model.addAttribute("mensaje", "El DNI NO EXISTE EN RENIEC");
            return "tarjeta/formDNI";
        }

    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(Model model){
        return "tarjeta/create";
    }

    @Secured("ROLE_ADMIN")
    @RequestMapping(value = "/submitCreate", method = RequestMethod.POST)
    public String submitCreate(Model model, @Valid ModelTarjetaCreate tarjetaCreate, BindingResult result, RedirectAttributes redirectAttributes){

        if(!result.hasErrors()){

            Tarjeta tarjeta = new Tarjeta();

            String nroTarjetaJunto = "";

            if(tarjetaCreate.getTipo().equals("V")){
                nroTarjetaJunto = Methods.generarAleatorio(4000000000000000L, 4999999999999999L) + "";   
            }else{
                nroTarjetaJunto = Methods.generarAleatorio(5000000000000000L, 5999999999999999L) + "";
            }
            
            String nroTarjetaFormateado = "";

            for(int i = 0; i < nroTarjetaJunto.length(); i++){
                
                nroTarjetaFormateado += nroTarjetaJunto.charAt(i);

                if((i+1) % 4 == 0){
                    nroTarjetaFormateado += " ";
                }
            }

            nroTarjetaFormateado = nroTarjetaFormateado.trim(); //Quitamos el espacio al final
            
            tarjeta.setTipo(tarjetaCreate.getTipo());

            String dueDate = Methods.generarAleatorio(1, 12) + "/" + Methods.generarAleatorio(2023, 2027); //Generamos una fecha entre el 2026 y el 2029

            while(dueDate.length() < 7){
                dueDate = "0" + dueDate;
            }

            tarjeta.setDueDate(Methods.obtenerUltimoDiaMes(dueDate));

            String cvv = Methods.generarAleatorio(10, 999) + "";
            
            while(cvv.length() < 3){
                cvv = "0" + cvv;
            }

            String credenciales = nroTarjetaFormateado + "," + dueDate + "," + cvv + "," + tarjetaCreate.getNombre().toUpperCase();
            
            tarjeta.setCredenciales(Methods.encodeBase64(credenciales));
            tarjeta.setActive(false);
            tarjeta.setDni(tarjetaCreate.getDni());
            tarjeta.setUsuario(_dataUsuarios.findByUsername(tarjetaCreate.getDni()));
            tarjeta.setMoneda(tarjetaCreate.getMoneda());

            Double maxDiario = 0.0;

            if(tarjetaCreate.getMoneda().equals("PEN")){
                maxDiario = Constants.maxPENDefault;
            }else{
                maxDiario = Constants.maxUSDDefault;
            }

            tarjeta.setSaldo(0.0);            
            tarjeta.setLimDiario(maxDiario);
            _dataTarjetas.save(tarjeta);

            redirectAttributes.addFlashAttribute("mensaje", "TARJETA CREADA");

            return "redirect:/";

        }else{
            model.addAttribute("mensaje", "Hay errores");
            System.out.println(result.getAllErrors());
            return "tarjeta/create";
        }
        
    }

    @RequestMapping(value = "/pagar", method = RequestMethod.GET)
    public String pagar(Model model){

        ModelPagoAbono form = new ModelPagoAbono();

        form.setTcCompra(Constants.tcCompra);
        form.setTcVenta(Constants.tcVenta);

        model.addAttribute("form", form);
        return "tarjeta/pago";
    }

    @RequestMapping(value = "/pagar", method = RequestMethod.POST)
    public String pagar(Model model, @Valid ModelPagoAbono form, BindingResult result, RedirectAttributes redirectAttributes){

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", System.getenv("API_KEY_TARJETAS"));

        HttpEntity<Object> entity = new HttpEntity<Object>(form, headers);
        ResponseEntity<ModelRespuestaPagoAbono> responseEntity;

        try{
            
            responseEntity = _restTemplate.exchange(Constants.URLFuxiaPass + Constants.APITarjeta + Constants.pagar, HttpMethod.POST, entity, ModelRespuestaPagoAbono.class);

            ModelRespuestaPagoAbono respuesta = responseEntity.getBody();

            if(respuesta.getStatus().equals("reload")){
                
                model.addAttribute("mensajeRecarga", respuesta.getMensaje());
                model.addAttribute("form", respuesta.getTarjeta());
                return "tarjeta/pago";
            }

            if(respuesta.getStatus().equals("error")){
                model.addAttribute("mensajeError", respuesta.getMensaje());
                model.addAttribute("form", respuesta.getTarjeta());
                return "tarjeta/pago";
            }

            if(respuesta.getStatus().equals("success")){
                redirectAttributes.addFlashAttribute("status", respuesta.getStatus());
                redirectAttributes.addFlashAttribute("mensaje", respuesta.getMensaje());
                return "redirect:/";
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return "redirect:/";
        

    }

    @RequestMapping(value = "/abonar", method = RequestMethod.GET)
    public String abonar(Model model){

        ModelPagoAbono form = new ModelPagoAbono();

        form.setTcCompra(Constants.tcCompra);
        form.setTcVenta(Constants.tcVenta);

        model.addAttribute("form", form);
        return "tarjeta/abono";
    }

    @RequestMapping(value = "/abonar", method = RequestMethod.POST)
    public String abonar(Model model, @Valid ModelPagoAbono form, BindingResult result, RedirectAttributes redirectAttributes){

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", System.getenv("API_KEY_TARJETAS"));

        HttpEntity<Object> entity = new HttpEntity<Object>(form, headers);
        ResponseEntity<ModelRespuestaPagoAbono> responseEntity;

        try{
            
            responseEntity = _restTemplate.exchange(Constants.URLFuxiaPass + Constants.APITarjeta + Constants.abonar, HttpMethod.POST, entity, ModelRespuestaPagoAbono.class);

            ModelRespuestaPagoAbono respuesta = responseEntity.getBody();

            if(respuesta.getStatus().equals("reload")){
                
                model.addAttribute("mensajeRecarga", respuesta.getMensaje());
                model.addAttribute("form", respuesta.getTarjeta());
                return "tarjeta/abono";
            }

            if(respuesta.getStatus().equals("error")){
                model.addAttribute("mensajeError", respuesta.getMensaje());
                model.addAttribute("form", respuesta.getTarjeta());
                return "tarjeta/abono";
            }

            if(respuesta.getStatus().equals("success")){
                redirectAttributes.addFlashAttribute("status", respuesta.getStatus());
                redirectAttributes.addFlashAttribute("mensaje", respuesta.getMensaje());
                return "redirect:/";
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        return "redirect:/";
        
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/listar", method = RequestMethod.GET)
    public String tarjetas(Model model, HttpSession session){

        Usuario u = (Usuario)session.getAttribute("usuario");

        List<Tarjeta> listaTarjetas = _dataTarjetas.findByUsuario(u.getId());

        for(Tarjeta t : listaTarjetas){

            String credenciales = Methods.decodeBase64(t.getCredenciales());
            t.setCredenciales(credenciales.substring(0,4) + " ---- ---- " + credenciales.substring(15,19));

        }

        model.addAttribute("listaTarjetas", listaTarjetas);
        System.out.println(listaTarjetas.size());
        return "tarjeta/lista";

    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/ver/{id}", method = RequestMethod.GET)
    public String verTarjeta(@PathVariable("id") Integer id, @RequestParam(defaultValue="") String fechaInicio, @RequestParam(defaultValue="") String fechaFin, Authentication auth, Model model, HttpSession session){

        Tarjeta t = _dataTarjetas.findById(id).get();

        if(!_dataUsuarios.findByUsername(auth.getName()).equals(t.getUsuario())){
            return "redirect:/";
        }

        session.setAttribute("tarjetaId", id);
        ModelTarjetaEdit tarjetaEdit = new ModelTarjetaEdit();
        tarjetaEdit.setId(id);
        tarjetaEdit.setActive(t.isActive());
        tarjetaEdit.setLimDiario(t.getLimDiario());

        model.addAttribute("monedaTarjeta", t.getMoneda());
        model.addAttribute("tarjeta", tarjetaEdit);

        Calendar calInicio = Calendar.getInstance();
        calInicio.set(Calendar.DAY_OF_MONTH, 1);
        
        Calendar calFin = Calendar.getInstance();
        calFin.set(Calendar.DATE, calFin.getActualMaximum(Calendar.DATE));

        model.addAttribute("idTarjeta", id);

        if(fechaInicio.isBlank()){
            fechaInicio = new SimpleDateFormat("yyyy-MM-dd").format(calInicio.getTime());
        }

        if(fechaFin.isBlank()){
            fechaFin = new SimpleDateFormat("yyyy-MM-dd").format(calFin.getTime());
        }

        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);

        List<Pago> listaPagos = _dataPagos.getPagosTarjeta(id, fechaInicio + " 00:00:00", fechaFin + " 23:59:59");
        
        model.addAttribute("listaPagos", listaPagos);
        
        return "tarjeta/ver";

    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editarTarjeta(@Valid ModelTarjetaEdit tarjeta, BindingResult result, Model model, HttpSession session, RedirectAttributes redirectAttributes){

        tarjeta.setId((Integer)session.getAttribute("tarjetaId"));

        if(result.hasErrors()){
            redirectAttributes.addAttribute("id", tarjeta.getId());
            return "redirect:/tarjeta/ver/{id}";
        }else{

            Tarjeta t = _dataTarjetas.findById(tarjeta.getId()).get();
            t.setActive(tarjeta.isActive());
            t.setLimDiario(tarjeta.getLimDiario());
            _dataTarjetas.save(t);
            redirectAttributes.addAttribute("id", tarjeta.getId());
            return "redirect:/tarjeta/ver/{id}";

        }     


    }

}
