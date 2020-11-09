/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.util;

import java.text.SimpleDateFormat;
import org.una.aeropuerto.dto.TransaccionesDTO;
/**
 *
 * @author Ivan Josué Arias Astua
 */
public class ReporteTransacciones {
    
    private String id;
    private String empleado;
    private String accion;
    private String fechaRegistro;
    
    private final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    
    public ReporteTransacciones(TransaccionesDTO transaction){
        this.id = transaction.getId().toString();
        this.empleado = transaction.getEmpleado().getNombre()+" "+transaction.getEmpleado().getCedula();
        this.accion = transaction.getAccion();
        this.fechaRegistro = formato.format(transaction.getFechaRegistro());
    }
    
    public String getId() {
        return id;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getAccion() {
        return accion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }
    
    
}
