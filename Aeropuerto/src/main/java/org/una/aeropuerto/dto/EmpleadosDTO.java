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
 * @author cordo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor 
public class EmpleadosDTO {
    
    private Long id;
    private String nombre;
    private String cedula;
    private boolean estado;
    private EmpleadosDTO jefe;
    private String contrasenaEncriptada;
    private RolesDTO rol;
    
    @Override
    public String toString(){
        return nombre;
    }
    
}
