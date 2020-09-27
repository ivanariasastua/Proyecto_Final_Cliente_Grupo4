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
import lombok.ToString;

/**
 *
 * @author cordo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiciosGastosDTO {
    
    private Long id;
    private ServiciosDTO servicio;
    private String empresa;
    @JsonbDateFormat(value = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaRegistro;
    private boolean estadoPago;
    private boolean estadoGasto;
    private String numeroContrato;
    private Integer perioricidad;
    private Long duracion;
    private EmpleadosDTO responsable;
    private boolean estado;
    private List<NotasDTO> notas = new ArrayList<>();
}
