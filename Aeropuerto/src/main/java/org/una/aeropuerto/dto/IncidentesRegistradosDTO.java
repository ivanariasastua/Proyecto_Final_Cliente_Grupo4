/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
public class IncidentesRegistradosDTO {

    private Long id;
    private String descripcion;
    private IncidentesCategoriasDTO categoria;
    private boolean estado;
    private EmpleadosDTO emisor;
    private EmpleadosDTO responsable;
    private AreasTrabajosDTO areaTrabajo;
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaRegistro;
    private List<IncidentesRegistradosEstadosDTO> incidentesRegistradosEstados = new ArrayList<>();
}