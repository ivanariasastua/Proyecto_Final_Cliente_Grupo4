/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author Ivan Josué Arias Astua
 */
@Data
@AllArgsConstructor
@NoArgsConstructor 
@ToString
public class EmpleadoDTO {
    
    private Long id;
    private String nombre;
    private String cedula;
    private boolean estado;
    private Boolean esJefe;
    private String contrasenaEncriptada;
    private RolesDTO rol;
    private Boolean passwordTemporal;
    private Boolean solicitud;
    private Boolean aprobado;
    private String correo;
    
    public EmpleadoDTO(EmpleadosDTO emp){
        id = emp.getId();
        nombre = emp.getNombre();
        cedula = emp.getCedula();
        estado = emp.isEstado();
        esJefe = emp.getEsJefe();
        contrasenaEncriptada = emp.getContrasenaEncriptada();
        rol = emp.getRol() ;
        passwordTemporal = emp.getPasswordTemporal();
        solicitud = emp.getSolicitud();
        aprobado = emp.getAprobado();
        correo = emp.getCorreo();
    }
}