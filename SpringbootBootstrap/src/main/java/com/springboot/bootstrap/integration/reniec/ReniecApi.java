package com.springboot.bootstrap.integration.reniec;



import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ReniecApi {
    
    @Autowired
    private RestTemplate restTemplate;


    public UserReniec findExitsUserByDni(String dni){

        UserReniec reniec = null;
        String apiKey = System.getenv("API_KEY_RENIEC");

        HashMap<String, String> uriVariables = new HashMap<>();

        uriVariables.put("dni", dni);
        uriVariables.put("apiKey", apiKey);


        try {

            reniec = restTemplate.getForObject("https://app-reniec.herokuapp.com/api/persona/dni={dni}&apiKey={apiKey}", UserReniec.class, uriVariables);

        } catch (Exception e) {
            
            System.out.println("No existe el usuario");
        }
      

        return reniec;

    }
}
