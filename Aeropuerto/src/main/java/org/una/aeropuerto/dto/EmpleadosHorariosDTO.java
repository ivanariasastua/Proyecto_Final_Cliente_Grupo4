/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
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
public class EmpleadosHorariosDTO {
    
    private Long id;
    @JsonBackReference
    private EmpleadosDTO empleado;
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date horaEntrada;
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date horaSalida;
    private String diaEntrada;
    private String diaSalida;
    private boolean estado;
    
    @Override
    public String toString(){
        return String.valueOf(horaEntrada+"/"+horaSalida);
    }
}
