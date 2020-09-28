/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import java.util.Date;
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
public class EmpleadosHorariosDTO {
    
    private Long id;
    private EmpleadosDTO empleado;
  //  @JsonbDateFormat(value = "HH:mm:ss.SSSXXX")
    private Date horaEntrada;
    private Date horaSalida;
    private String diaEntrada;
    private String diaSalida;
    private boolean estado;
    
    @Override
    public String toString(){
        return String.valueOf(horaEntrada+"/"+horaSalida);
    }
}
