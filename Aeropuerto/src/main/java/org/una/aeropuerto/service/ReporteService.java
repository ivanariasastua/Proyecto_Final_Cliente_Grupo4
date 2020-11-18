/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.GenericType;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.util.TransactionRecorder;

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
            Request request = new Request("reportes/reporteGastos1", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estPago}/{estGasto}/{responsable}/{creador}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de gastos");
            }catch(Exception ex){}
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
            parametros.put("creador", UserAuthenticated.getInstance().getUsuario().getNombre()+" "+UserAuthenticated.getInstance().getUsuario().getCedula());
            Request request = new Request("reportes/reporteGastos3", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estPago}/{responsable}/{creador}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de gastos");
            }catch(Exception ex){}
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
            parametros.put("creador", UserAuthenticated.getInstance().getUsuario().getNombre()+" "+UserAuthenticated.getInstance().getUsuario().getCedula());
            Request request = new Request("reportes/reporteGastos4", "/{fecha}/{fecha2}/{empresa}/{servicio}/{estGasto}/{responsable}/{creador}", parametros);
            request.get();
            if(request.isError())
                return new Respuesta(false, request.getError());
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de gastos");
            }catch(Exception ex){}
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
            parametros.put("creador", UserAuthenticated.getInstance().getUsuario().getNombre()+" "+UserAuthenticated.getInstance().getUsuario().getCedula());
            Request request = new Request("reportes/reporteGastos2", "/{fecha}/{fecha2}/{empresa}/{servicio}/{responsable}/{creador}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de gastos");
            }catch(Exception ex){}
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }

    public Respuesta reporteIncident(Date fechaIni, Date fechaFin, boolean estado, String responsable, String emisor,boolean est) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaIni", fechaIni);
            parametros.put("fechaFin", fechaFin);
            if(est){
                parametros.put("estado", estado);
            }
            parametros.put("responsable", responsable);
            parametros.put("emisor", emisor);
            parametros.put("creador", UserAuthenticated.getInstance().getUsuario().getNombre()+" "+UserAuthenticated.getInstance().getUsuario().getCedula());
            Request request;
            if(est){
                request = new Request("reportes/reporteIncidente", "/{fechaIni}/{fechaFin}/{estado}/{responsable}/{emisor}/{creador}", parametros);
            }else{
                request = new Request("reportes/reporteIncidente2", "/{fechaIni}/{fechaFin}/{responsable}/{emisor}/{creador}", parametros);
            }
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de incidentes");
            }catch(Exception ex){}
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta reporteHorasLaboradas(String cedula, Date fechaInicial, Date fechaFinal) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cedula", cedula);
            parametros.put("fecha1", fechaInicial);
            parametros.put("fecha2", fechaFinal);
            Request request = new Request("reportes/reporteHoras", "/{cedula}/{fecha1}/{fecha2}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = (String) request.readEntity(String.class);
            try{
                TransactionRecorder.registrarTransaccion("Generar reporte de horas laboradas");
            }catch(Exception ex){}
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta PruebaHorasLaboradas(String cedula, Date fechaInicial, Date fechaFinal){
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cedula", cedula);
            parametros.put("fecha1", fechaInicial);
            parametros.put("fecha2", fechaFinal);
            Request request = new Request("empleados_marcajes/reporteHoras", "/{cedula}/{fecha1}/{fecha2}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError());
            }
            Object result = request.readEntity(new GenericType<List<EmpleadosMarcajesDTO>>(){});
            return new Respuesta(true, "Reporte", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
}
