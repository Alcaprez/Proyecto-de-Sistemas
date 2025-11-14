/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.UPAO.proyecto.Util;

public class TextUtils {
    
    public static String capitalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        
        texto = texto.trim().toLowerCase();
        String[] palabras = texto.split("\\s+");
        StringBuilder resultado = new StringBuilder();
        
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                if (resultado.length() > 0) {
                    resultado.append(" ");
                }
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                         .append(palabra.substring(1));
            }
        }
        
        return resultado.toString();
    }
    
    // Método adicional para formatear nombres compuestos
    public static String capitalizarNombresPropios(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }
        
        texto = capitalizarTexto(texto);
        
        // Manejar casos especiales como "Maria Del Carmen"
        texto = texto.replace(" Del ", " del ")
                    .replace(" De La ", " de la ")
                    .replace(" De Los ", " de los ")
                    .replace(" De ", " de ");
        
        return texto;
    }
}
