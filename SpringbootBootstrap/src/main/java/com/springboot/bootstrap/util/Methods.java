package com.springboot.bootstrap.util;

import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Methods {
    
    public static long generarAleatorio(long min, long max){
        return (long)(Math.random() * (max - min + 1) + min);
    }

    public static String encodeBase64(String text){
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String decodeBase64(String base64String){
        return new String(Base64.getDecoder().decode(base64String));
    }

    public static Date obtenerUltimoDiaMes(String mmyyyy){

        int month = Integer.parseInt(mmyyyy.substring(0,2));
        int year = Integer.parseInt(mmyyyy.substring(3));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();

    }

    public static String generarCadenaAleatoria(int longitud){
        
        String letrasUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String letrasLower = letrasUpper.toLowerCase();
        String digitos = "0123456789";

        String alfanumerico = letrasUpper + letrasLower + digitos;

        String cadenaAleatoria = "";

        for(int i = 0; i < longitud; i++){

            int random = (int)(Math.random() * alfanumerico.length());
            cadenaAleatoria += alfanumerico.charAt(random);

        }

        return cadenaAleatoria;

    }

}
