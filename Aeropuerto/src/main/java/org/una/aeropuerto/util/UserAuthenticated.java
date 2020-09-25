/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.util;

import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.RolesDTO;

/**
 *
 * @author Ivan Josué Arias Astua
 */
public class UserAuthenticated {
    
    private static UserAuthenticated INSTANCE = null;
    private EmpleadosDTO usuario;
    private RolesDTO rol;
    private String token;
    
    public UserAuthenticated(){}
    
    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (AppContext.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserAuthenticated();
                }
            }
        }
    }
    
    public static UserAuthenticated getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }
    
    public EmpleadosDTO getUsuario(){
        return this.usuario;
    }
    
    public RolesDTO getRol(){
        return this.rol;
    }
    
    public String getToken(){
        return this.token;
    }
    
    public void setData(EmpleadosDTO usuario, RolesDTO rol, String token){
        this.usuario = usuario;
        this.rol = rol;
        this.token = token;
    }
}
