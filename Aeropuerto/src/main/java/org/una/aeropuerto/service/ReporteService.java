/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author Ivan Josué Arias Astua
 */
public class ReporteService {
    
    public Respuesta reporteGastosFechaAntesDe(Date fecha, String empresa, String servicio, boolean estPago, boolean estGasto, String responsable){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha", fecha);
            parametros.put("empresa", empresa);
            parametros.put("servicio", servicio);
            parametros.put("estPago", estPago);
            parametros.put("estGasto", estGasto);
            parametros.put("responsable", responsable);
            Request request = new Request("reportes/reporteGastos1", "/{fecha}/{empresa}/{servicio}/{estPago}/{estGasto}/{responsable}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        }catch(Exception ex){
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
}
