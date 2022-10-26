package com.springboot.bootstrap.controller.rest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.bootstrap.integration.reniec.ReniecApi;
import com.springboot.bootstrap.integration.reniec.UserReniec;
import com.springboot.bootstrap.model.Pago;
import com.springboot.bootstrap.model.Request;
import com.springboot.bootstrap.model.Tarjeta;
import com.springboot.bootstrap.model.Token;
import com.springboot.bootstrap.model.dto.ModelActivarTarjeta;
import com.springboot.bootstrap.model.dto.ModelBuscarTarjeta;
import com.springboot.bootstrap.model.dto.ModelPagoAbono;
import com.springboot.bootstrap.model.dto.ModelRespuestaSaldo;
import com.springboot.bootstrap.model.dto.ModelTarjetaCreate;
import com.springboot.bootstrap.repository.PagoRepository;
import com.springboot.bootstrap.repository.RequestRepository;
import com.springboot.bootstrap.repository.TarjetaRepository;
import com.springboot.bootstrap.repository.TokenRepository;
import com.springboot.bootstrap.service.IUsuarioService;
import com.springboot.bootstrap.util.Constants;
import com.springboot.bootstrap.util.Methods;

@RestController
@RequestMapping(value = "api/tarjeta", produces = "application/json")
public class TarjetaRestController {
    
    @Autowired
    private TarjetaRepository _dataTarjetas;

    @Autowired
    private PagoRepository _dataPagos;

    @Autowired
    private TokenRepository _dataTokens;

    @Autowired
    private RequestRepository _dataRequests;

    @Autowired
    private ReniecApi _reniecApi;

    @Autowired
    private IUsuarioService _dataUsuarios;

    @PostMapping(value = "/crearTarjeta", produces = "application/json")
    public ResponseEntity<Map<String,Object>> crearTarjeta(@RequestHeader(required = true) String apikey, @RequestBody ModelTarjetaCreate form){

        Token t = validarToken(apikey);

        Map<String, Object> respuesta = new HashMap<>();

        String status = "";
        String mensaje = "";

        if(t == null){

            status = "error";
            mensaje = "No está autorizado a utilizar este servicio, revise sus credenciales";

            respuesta.put("status", status);
            respuesta.put("tarjeta", null);
            respuesta.put("mensaje", mensaje);

            return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.FORBIDDEN);
        }

        UserReniec user = _reniecApi.findExitsUserByDni(form.getDni());

        if(user != null){

            Tarjeta tarjeta = new Tarjeta();

            tarjeta.setDni(form.getDni());
            tarjeta.setTipo(form.getTipo());
            tarjeta.setMoneda(form.getMoneda());

            String nroTarjetaJunto = "";

            if(form.getTipo().equals("V")){
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

            String dueDate = Methods.generarAleatorio(1, 12) + "/" + Methods.generarAleatorio(2023, 2027); //Generamos una fecha entre el 2026 y el 2029

            while(dueDate.length() < 7){
                dueDate = "0" + dueDate;
            }

            tarjeta.setDueDate(Methods.obtenerUltimoDiaMes(dueDate));

            String cvv = Methods.generarAleatorio(10, 999) + "";
            
            while(cvv.length() < 3){
                cvv = "0" + cvv;
            }

            String credenciales = nroTarjetaFormateado + "," + dueDate + "," + cvv + "," + form.getNombre().toUpperCase();
            
            tarjeta.setCredenciales(Methods.encodeBase64(credenciales));
            tarjeta.setActive(false);
            tarjeta.setUsuario(_dataUsuarios.findByUsername(form.getDni()));

            Double maxDiario = 0.0;

            if(form.getMoneda().equals("PEN")){
                maxDiario = Constants.maxPENDefault;
            }else{
                maxDiario = Constants.maxUSDDefault;
            }

            tarjeta.setSaldo(0.0);            
            tarjeta.setLimDiario(maxDiario);
            Tarjeta tarjetaCreated = _dataTarjetas.save(tarjeta);

            status = "success";
            mensaje = "Tarjeta creada con éxito.";

            respuesta.put("status", status);
            respuesta.put("tarjeta", tarjetaCreated);
            respuesta.put("mensaje", mensaje);

            Request r = new Request();

            r.setToken(t);
            r.setFechaHora(new Date());
            r.setHttpMethod("POST");
            r.setAction("crearTarjeta");
            r.setStatus(status);
            _dataRequests.save(r);

            return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.CREATED);

        }else{

            status = "error";
            mensaje = "EL DNI NO EXISTE EN EL SISTEMA DE RENIEC";

            respuesta.put("status", status);
            respuesta.put("tarjeta", null);
            respuesta.put("mensaje", mensaje);

            Request r = new Request();

            r.setToken(t);
            r.setFechaHora(new Date());
            r.setHttpMethod("POST");
            r.setAction("crearTarjeta");
            r.setStatus(status);
            _dataRequests.save(r);

            return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.NOT_FOUND);

        }

    }

    @PutMapping(value = "/activar", produces = "application/json")
    public ResponseEntity<String> activar(@RequestHeader(required = true) String apikey, @RequestBody ModelActivarTarjeta form){

        Token t = validarToken(apikey);

        String mensaje = "";

        if(t == null){

            mensaje = "NO TIENE AUTORIZACION, REVISE SUS CREDENCIALES";

            return new ResponseEntity<String>(mensaje, HttpStatus.FORBIDDEN);
        }

        String credenciales = form.getNroTarjeta() + "," + form.getDueMonth() + "/" + form.getDueYear() + "," + form.getCvv() + "," + form.getNombre().toUpperCase();
        String credencialesEncode = Methods.encodeBase64(credenciales);
        Tarjeta tarjeta = _dataTarjetas.findByCredenciales(credencialesEncode);

        Request r = new Request();

        r.setToken(t);
        r.setFechaHora(new Date());
        r.setHttpMethod("PUT");
        r.setAction("activar");

        if(existeTarjeta(tarjeta)){

            tarjeta.setActive(form.isActive());
            _dataTarjetas.save(tarjeta);
            
            if(form.isActive()){
                mensaje = "ACTIVADA";
            }else{
                mensaje = "DESACTIVADA";
            }

            r.setStatus("success");
            _dataRequests.save(r);

            return new ResponseEntity<String>(mensaje, HttpStatus.OK);

        }else{
            mensaje = "LA TARJETA NO EXISTE";
            r.setStatus("error");
            _dataRequests.save(r);
            return new ResponseEntity<String>(mensaje, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/saldo", produces = "application/json")
    public ResponseEntity<ModelRespuestaSaldo> saldo(@RequestHeader(required = true) String apikey, @RequestBody ModelBuscarTarjeta form){

        Token t = validarToken(apikey);

        ModelRespuestaSaldo respuesta = new ModelRespuestaSaldo();

        String status = "";
        String mensaje = "";

        if(t == null){

            status = "error";
            mensaje = "NO ESTA AUTORIZADO";

            respuesta.setStatus(status);
            respuesta.setMensaje(mensaje);

            return new ResponseEntity<ModelRespuestaSaldo>(respuesta, HttpStatus.FORBIDDEN);
        }

        String credenciales = form.getNroTarjeta() + "," + form.getDueMonth() + "/" + form.getDueYear() + "," + form.getCvv() + "," + form.getNombre().toUpperCase();
        String credencialesEncode = Methods.encodeBase64(credenciales);
        Tarjeta tarjeta = _dataTarjetas.findByCredenciales(credencialesEncode);

        Request r = new Request();

        r.setToken(t);
        r.setFechaHora(new Date());
        r.setHttpMethod("POST");
        r.setAction("saldo");

        if(existeTarjeta(tarjeta)){

            respuesta.setMoneda(tarjeta.getMoneda());
            respuesta.setSaldo(tarjeta.getSaldo());
            respuesta.setActive(tarjeta.isActive());
            status = "success";
            mensaje = "Se obtuvo el saldo de la tarjeta.";
            r.setStatus(status);
            _dataRequests.save(r);

            respuesta.setStatus(status);
            respuesta.setMensaje(mensaje);

            return new ResponseEntity<ModelRespuestaSaldo>(respuesta, HttpStatus.OK);

        }else{
            status = "error";
            mensaje = "LA TARJETA NO EXISTE";
            r.setStatus(status);
            _dataRequests.save(r);

            respuesta.setStatus(status);
            respuesta.setMensaje(mensaje);
            return new ResponseEntity<ModelRespuestaSaldo>(respuesta, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/pagar", produces = "application/json")
    public ResponseEntity<Map<String, Object>> pagar(@RequestHeader(required = true) String apikey, @RequestBody ModelPagoAbono form){

        Token t = validarToken(apikey);

        Map<String, Object> respuesta = new HashMap<>();

        String status = "";
        String mensaje = "";

        if(t == null){

            status = "error";
            mensaje = "NO ESTA AUTORIZADO, REVISE LAS CREDENCIALES";

            respuesta.put("status", status);
            respuesta.put("tarjeta", null);
            respuesta.put("mensaje", mensaje);

            return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.FORBIDDEN);
        }

        String credenciales = form.getNroTarjeta() + "," + form.getDueMonth() + "/" + form.getDueYear() + "," + form.getCvv() + "," + form.getNombre().toUpperCase();
        String credencialesEncode = Methods.encodeBase64(credenciales);
        Tarjeta tarjeta = _dataTarjetas.findByCredenciales(credencialesEncode);

        if(validarTarjeta(tarjeta)){

            Double montoGastadoHoy = _dataPagos.getSumMontoTarjetaHoy(tarjeta.getId());
            if(montoGastadoHoy == null){ montoGastadoHoy = 0.0;}

            if(form.getMoneda().equals(tarjeta.getMoneda())){
            
                if(tarjeta.getSaldo() >= form.getMonto()){

                    if(montoGastadoHoy + form.getMonto() <= tarjeta.getLimDiario()){

                        Pago p = new Pago();
                        p.setTarjeta(tarjeta);
                        p.setMonto(form.getMonto());
                        p.setFechaHora(new Date());
                        _dataPagos.save(p);

                        tarjeta.setSaldo(tarjeta.getSaldo() - form.getMonto());
                        _dataTarjetas.save(tarjeta);
                        status = "success";
                        mensaje = "PAGO REALIZADO";

                    }else{
                        status = "error";
                        mensaje = "Ha superado su límite diario, no se puede procesar el pago!!!";
                    }

                }else{
                    status = "error";
                    mensaje = "SALDO INSUFICENTE";
                }

            }else{

                status = "reload";
                String moneda = tarjeta.getMoneda();
                form.setMoneda(moneda);

                if(moneda.equals("USD")){ //La moneda de la tarjeta es dólares, por lo que hay que convertir el monto del formulario a dólares.
                    Double monto = form.getMonto() / form.getTcCompra(); //El banco está comprando dólares, por lo que se aplica el TC de compra.
                    monto = Math.rint(monto * 100) / 100;
                    form.setMonto(monto);
                }else{ //Y viceversa
                    Double monto = form.getMonto() * form.getTcVenta(); //El banco está vendiendo dólares, por lo que se aplica el TC de venta.
                    monto = Math.rint(monto * 100) / 100;
                    form.setMonto(monto);
                } //Siempre el usuario paga un poco más al banco, este nunca pierde.

                mensaje = "Se recalculó el monto total en " + moneda + ". Vuelva a hacer clic para procesar el pago.";

            }
            
            

        }else{

            status = "error";
            mensaje = "NO SE ENCUENTRA LA TARJETA, REVISE SUS DATOS";

        }

        if(status.equals("success")){
            respuesta.put("tarjeta", null);
        }else{
            respuesta.put("tarjeta", form);
        }

        respuesta.put("status", status);
        respuesta.put("mensaje", mensaje);

        Request r = new Request();

        r.setToken(t);
        r.setFechaHora(new Date());
        r.setHttpMethod("POST");
        r.setAction("pagar");
        r.setStatus(status);
        _dataRequests.save(r);

        return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.OK);

    }

    @PostMapping(value = "/abonar", produces = "application/json")
    public ResponseEntity<Map<String, Object>> abonar(@RequestHeader(required = true) String apikey, @RequestBody ModelPagoAbono form){

        Token t = validarToken(apikey);

        Map<String, Object> respuesta = new HashMap<>();

        String status = "";
        String mensaje = "";

        if(t == null){

            status = "error";
            mensaje = "NO ESTA AUTORIZADO REVISE SUS CREDENCIALES";

            respuesta.put("status", status);
            respuesta.put("tarjeta", null);
            respuesta.put("mensaje", mensaje);
            return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.FORBIDDEN);
        }

        String credenciales = form.getNroTarjeta() + "," + form.getDueMonth() + "/" + form.getDueYear() + "," + form.getCvv() + "," + form.getNombre().toUpperCase();
        String credencialesEncode = Methods.encodeBase64(credenciales);
        Tarjeta tarjeta = _dataTarjetas.findByCredenciales(credencialesEncode);

        if(validarTarjeta(tarjeta)){

            if(form.getMoneda().equals(tarjeta.getMoneda())){

                tarjeta.setSaldo(tarjeta.getSaldo() + form.getMonto());
                _dataTarjetas.save(tarjeta);
                status = "success";
                mensaje = "ABONO CORRECTO";

            }else{

                status = "reload";
                String moneda = tarjeta.getMoneda();
                form.setMoneda(moneda);

                if(moneda.equals("USD")){ //La moneda de la tarjeta es dólares, por lo que hay que convertir el monto del formulario a dólares.
                    Double monto = form.getMonto() / form.getTcVenta(); //El banco está vendiendo dólares, por lo que se aplica el TC de venta.
                    monto = Math.rint(monto * 100) / 100;
                    form.setMonto(monto);
                }else{ //Y viceversa
                    Double monto = form.getMonto() * form.getTcCompra(); //El banco está comprando dólares, por lo que se aplica el TC de compra.
                    monto = Math.rint(monto * 100) / 100;
                    form.setMonto(monto);
                } //Siempre el banco da un poco menos al usuario, nunca pierde.

                mensaje = "Se recalculó el monto total en " + moneda + ". Vuelva a hacer clic para procesar el abono.";

            }

        }else{

            status = "error";
            mensaje = "NO SE ENCUENTRA LA TARJETA, REVISE SUS DATOS";

        }

        if(status.equals("success")){
            respuesta.put("tarjeta", null);
        }else{
            respuesta.put("tarjeta", form);
        }

        respuesta.put("status", status);
        respuesta.put("mensaje", mensaje);

        Request r = new Request();

        r.setToken(t);
        r.setFechaHora(new Date());
        r.setHttpMethod("POST");
        r.setAction("abonar");
        r.setStatus(status);
        _dataRequests.save(r);

        return new ResponseEntity<Map<String,Object>>(respuesta, HttpStatus.OK);

    }

    public boolean validarTarjeta(Tarjeta t){

        if(t == null || t.isActive() == false || new Date().after(t.getDueDate())){
            return false;
        }else{
            return true;
        }

    }

    public boolean existeTarjeta(Tarjeta t){

        if(t == null || new Date().after(t.getDueDate())){
            return false;
        }else{
            return true;
        }

    }

    public Token validarToken(String token){

        String tokenEncrypt = Methods.encodeBase64(token);

        Token t = _dataTokens.findTokenVigenteByToken(tokenEncrypt);

        if(t == null){
            return null;
        }

        return t;

    }

}
