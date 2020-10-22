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
import org.una.aeropuerto.dto.ParametrosSistemaDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class ParametrosSistemaService {

    public Respuesta guardarParametro(ParametrosSistemaDTO parametros) {
        try {
            Request request = new Request("parametros_sistema/save");
            request.post(parametros);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el parametro del sistema");
            }
            ParametrosSistemaDTO result = (ParametrosSistemaDTO) request.readEntity(ParametrosSistemaDTO.class);
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarParametro(Long id, ParametrosSistemaDTO parametrosS) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("parametros_sistema/editar", "/{id}", parametros);
            request.put(parametrosS);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el parametro del sistema");
            }
            ParametrosSistemaDTO result = (ParametrosSistemaDTO) request.readEntity(ParametrosSistemaDTO.class);
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByValor(String valor) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("valor", valor);
            Request request = new Request("parametros_sistema/valor", "/{valor}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los parametros del sistema");
            }
            ParametrosSistemaDTO result = (ParametrosSistemaDTO) request.readEntity(ParametrosSistemaDTO.class);
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByCodigoIdentificador(String codigo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("codigo", codigo);
            Request request = new Request("parametros_sistema/codigoIdentiicador", "/{codigo}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los parametros del sistema");
            }
            ParametrosSistemaDTO result = (ParametrosSistemaDTO) request.readEntity(ParametrosSistemaDTO.class);
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByFechaRegistro(String fecha1, String fecha2){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha1", fecha1);
            parametros.put("fecha2", fecha2);
            Request request = new Request("parametros_sistema/fechaRegisto", "/{fecha1}/{fecha2}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los parametros del sistema");
            }
            List<ParametrosSistemaDTO> result = (List<ParametrosSistemaDTO>) request.readEntity(new GenericType<List<ParametrosSistemaDTO>>(){});
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByModificacion(Date fecha1, Date fecha2){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("fecha1", fecha1);
            parametros.put("fecha2", fecha2);
            Request request = new Request("parametros_sistema/fechaModificacion", "/{fecha1}/{fecha2}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los parametros del sistema");
            }
            List<ParametrosSistemaDTO> result = (List<ParametrosSistemaDTO>) request.readEntity(new GenericType<List<ParametrosSistemaDTO>>(){});
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta update(ParametrosSistemaDTO paramSistema, Long id){
        try{
            Map<String,Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("parametros_sistema/editar","/{id}", parametros);
            request.put(paramSistema);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al actualizar el parametro del sistema");
            }
            ParametrosSistemaDTO result = (ParametrosSistemaDTO) request.readEntity(ParametrosSistemaDTO.class);
            return new Respuesta(true, "Parametros_Sistema", result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
