/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

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
    
     public Respuesta getAll() {
        try {
            Request request = new Request("parametros_sistema/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los parametros del sistema");
            }
            List<ParametrosSistemaDTO> result = (List<ParametrosSistemaDTO>) request.readEntity(new GenericType<List<ParametrosSistemaDTO>>() {
            });
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

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
            Request request = new Request("parametros_sistema/valor");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los parametros del sistema");
            }
            List<ParametrosSistemaDTO> result = (List<ParametrosSistemaDTO>) request.readEntity(new GenericType<List<ParametrosSistemaDTO>>() {
            });
            return new Respuesta(true, "Parametros_Sistema", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
