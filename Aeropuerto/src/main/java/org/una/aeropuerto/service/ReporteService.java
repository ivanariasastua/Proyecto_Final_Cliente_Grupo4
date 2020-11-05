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
    
    public Respuesta reporteGastos(Date fecha, Date fecha2, String empresa, String servicio, boolean estPago, boolean estGasto, String responsable){
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha", fecha);
            parametros.put("fecha2", fecha2);
            parametros.put("empresa", empresa);
            parametros.put("servicio", servicio);
            parametros.put("estPago", estPago);
            parametros.put("estGasto", estGasto);
            parametros.put("responsable", responsable);
            Request request = new Request("reportes/reporteGastos1", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estPago}/{estGasto}/{responsable}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        }catch(Exception ex){
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta reporteGastos(Date fecha, Date fecha2, String empresa, String servicio, boolean estPago, String responsable){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha", fecha);
            parametros.put("fecha2", fecha2);
            parametros.put("empresa", empresa);
            parametros.put("servicio", servicio);
            parametros.put("estPago", estPago);
            parametros.put("responsable", responsable);
            Request request = new Request("reportes/reporteGastos3", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estPago}/{responsable}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        }catch(Exception ex){
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta reporteGastos(Date fecha, Date fecha2, String empresa, String servicio, String responsable, boolean estGasto){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha", fecha);
            parametros.put("fecha2", fecha2);
            parametros.put("empresa", empresa);
            parametros.put("servicio", servicio);
            parametros.put("estGasto", estGasto);
            parametros.put("responsable", responsable);
            Request request = new Request("reportes/reporteGastos1", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estGasto}/{responsable}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        }catch(Exception ex){
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta reporteGastos(Date fecha, Date fecha2, String empresa, String servicio, String responsable){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha", fecha);
            parametros.put("fecha2", fecha2);
            parametros.put("empresa", empresa);
            parametros.put("servicio", servicio);
            parametros.put("responsable", responsable);
            Request request = new Request("reportes/reporteGastos2", "/{fecha}/{fecha2}/{empresa}/{servicio}/{responsable}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }

    public Respuesta reporteIncident(Date fechaIni, boolean estado, String responsable, String emisor) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaIni", fechaIni);
          //  parametros.put("fechaFin", fechaFin);
            parametros.put("estado", estado);
            parametros.put("responsable", responsable);
            parametros.put("emisor", emisor);
            Request request = new Request("reportes/reporteIncidente", "/{fechaIni}/{estado}/{responsable}/{emisor}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
}