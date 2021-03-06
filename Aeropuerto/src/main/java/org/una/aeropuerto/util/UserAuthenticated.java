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
        this.token = "bearer "+token;
    }
    
    public Boolean isValid(){
        return usuario.isEstado() && usuario.getAprobado() && !usuario.getPasswordTemporal();
    }
    
    public Boolean isEstado(){
        return usuario.isEstado();
    }
    
    public Boolean isAprobado(){
        return usuario.getAprobado();
    }
    
    public Boolean isTemporal(){
        return usuario.getPasswordTemporal();
    }
    
    public Boolean isRol(String rolName){
        if(rol == null)
            return false;
        return rol.getNombre().equals(rolName.toUpperCase());
    }
    
    public Boolean cambioDatosCriticos(EmpleadosDTO emp){
        if(emp.getId().equals(this.usuario.getId())){
            if(emp.getRol() != null){
                if(!emp.getRol().getNombre().equals(this.usuario.getRol().getNombre())){
                    return true;
                }else{
                    this.usuario = emp;
                    return false;
                }
            }else{
                return true;
            }
        }
        return false;
    }
}
