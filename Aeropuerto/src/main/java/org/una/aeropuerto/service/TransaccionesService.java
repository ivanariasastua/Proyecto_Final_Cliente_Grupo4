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
import org.una.aeropuerto.dto.TransaccionesDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class TransaccionesService {

    public Respuesta guardarTransaccion(TransaccionesDTO transaccion) {
        try {
            Request request = new Request("transacciones/save");
            request.post(transaccion);
            if (request.isError()) {
                System.out.println(request.getError()+" "+request.getMensajeRespuesta());
                return new Respuesta(false, request.getError(), "No se pudo guardar la transaccion");
            }
            TransaccionesDTO result = (TransaccionesDTO) request.readEntity(TransaccionesDTO.class);
            return new Respuesta(true, "Transacciones", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByAccion(String accion) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", accion);
            Request request = new Request("transacciones/accion", "/{term}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener las transacciones");
            }
            List<TransaccionesDTO> result = (List<TransaccionesDTO>) request.readEntity(new GenericType<List<TransaccionesDTO>>() {
            });
            return new Respuesta(true, "Transacciones", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByFechas(Date fechaInicio, Date fechaFinal){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fechaInicio", fechaInicio);
            parametros.put("fechaFinal", fechaFinal);
            Request request = new Request("transacciones/fechas", "/{fechaInicio}/{fechaFinal}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(),"Error al obtener las transacciones");
            }
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Transacciones", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
    
    public Respuesta getByFiltro(Date fechaInicio, Date fechaFinal, String empleado){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha1", fechaInicio);
            parametros.put("fecha2", fechaFinal);
            parametros.put("empleado", empleado);
            Request request = new Request("transacciones/filtro", "/{fecha1}/{fecha2}/{empleado}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(),"Error al obtener las transacciones");
            }
            Object result = (String) request.readEntity(String.class);
            return new Respuesta(true, "Transacciones", result);
        } catch (Exception ex) {
            return new Respuesta(false, "No se pudo establecer comunicación con el servidor");
        }
    }
}
