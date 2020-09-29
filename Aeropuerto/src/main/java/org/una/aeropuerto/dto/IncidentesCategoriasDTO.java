/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

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
public class IncidentesCategoriasDTO {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private IncidentesCategoriasDTO categoriaSuperior;
    private boolean estado;
//    //@ToString.Exclude
    private List<IncidentesRegistradosDTO> incidentesRegistrados;
    
    @Override
    public String toString(){
        return nombre;
    }
}
