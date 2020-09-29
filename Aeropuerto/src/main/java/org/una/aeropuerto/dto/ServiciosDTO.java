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
public class ServiciosDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estado;
    //@ToString.Exclude
    private List<ServiciosGastosDTO> gastosMantenimientos = new ArrayList<>();
    private List<ServiciosPreciosDTO> serviciosPrecios = new ArrayList<>();
    
     @Override
    public String toString(){
        return nombre;
    }
}
