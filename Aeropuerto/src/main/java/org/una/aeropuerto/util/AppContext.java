/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.una.aeropuerto.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import org.una.aeropuerto.App;

/**
 * 
 * @author Ivan Josué Arias Astúa
 */
public class AppContext {
    
    private static AppContext INSTANCE = null;
    private static HashMap<String, Object> context = new HashMap<>();
    
    private AppContext() {
        cargarTema();
    }
    
    private void cargarTema(){
        try{
            File archivo = new File (App.class.getResource("resources/config.txt").getFile());
            FileReader fr = new FileReader (archivo);
            BufferedReader br = new BufferedReader(fr);
            String linea = br.readLine();
            set("Tema", linea);
        }catch(IOException ex){
            System.out.println("Error cargando el tema ["+ex+"]");
            set("Tema", "Tema_Oscuro.css");
        }
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (AppContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AppContext();
                }
            }
        }
    }
    
    public static AppContext getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }
    
    public Object get(String parameter){    
        return context.get(parameter);
    }

    public void set(String nombre, Object valor) {
        context.put(nombre, valor);
    }

    public void delete(String parameter) {
        context.put(parameter, null);
    }

    public void clear(){
        context.clear();
    }
}
