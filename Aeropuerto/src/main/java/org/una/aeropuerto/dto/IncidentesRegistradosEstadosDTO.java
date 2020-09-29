/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import java.util.Date;
import javax.json.bind.annotation.JsonbDateFormat;
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
public class IncidentesRegistradosEstadosDTO {
    
    private Long id;
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaRegistro;
    private boolean estado;
    private IncidentesRegistradosDTO incidenteRegistrado;
}
