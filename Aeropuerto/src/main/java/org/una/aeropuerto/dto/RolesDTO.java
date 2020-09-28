/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import java.util.ArrayList;
import java.util.List;
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
@ToString
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
