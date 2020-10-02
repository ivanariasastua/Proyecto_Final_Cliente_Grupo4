/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author cordo
 */

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class RolesDTO {
    
    private Long id;
    private String nombre;
//    @ToString.Exclude
//    private List<EmpleadosDTO> usuarios=new ArrayList<>();
    
    
    @Override
    public String toString(){
        return nombre;
    }
    
}
