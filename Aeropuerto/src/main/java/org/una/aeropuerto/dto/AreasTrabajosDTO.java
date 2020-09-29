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
public class AreasTrabajosDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estado;
    
    @Override
    public String toString(){
        return nombre;
    }
}
