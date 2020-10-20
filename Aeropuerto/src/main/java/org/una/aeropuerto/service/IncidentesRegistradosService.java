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
import org.una.aeropuerto.dto.IncidentesRegistradosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class IncidentesRegistradosService {

    public Respuesta guardarIncidenteRegistrado(IncidentesRegistradosDTO incidente) {
        try {
            Request request = new Request("incidentes_registrados/save");
            request.post(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el incidente registrado");
            }
            IncidentesRegistradosDTO result = (IncidentesRegistradosDTO) request.readEntity(IncidentesRegistradosDTO.class);
            return new Respuesta(true, "Incidentes_Registrados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarIncidenteRegistrado(Long id, IncidentesRegistradosDTO incidente) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("incidentes_registrados/editar", "/{id}", parametros);
            request.put(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el incidente registrado");
            }
            IncidentesRegistradosDTO result = (IncidentesRegistradosDTO) request.readEntity(IncidentesRegistradosDTO.class);
            return new Respuesta(true, "Incidentes_Registrados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta findByArea(String area){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", area);
            Request request = new Request("incidentes_registrados/area", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>(){});
            return new Respuesta(true, "Incidentes_Registrados",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta findByEmisor(String emisor){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", emisor);
            Request request = new Request("incidentes_registrados/emisor", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>(){});
            return new Respuesta(true, "Incidentes_Registrados",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta findByCategoria(String categoria){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", categoria);
            Request request = new Request("incidentes_registrados/categoria", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>(){});
            return new Respuesta(true, "Incidentes_Registrados",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta findByResponsable(String responsable){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", responsable);
            Request request = new Request("incidentes_registrados/responsable", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>(){});
            return new Respuesta(true, "Incidentes_Registrados",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta inactivar(IncidentesRegistradosDTO incidente, Long id, String cedula, String codigo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            parametros.put("cedula", cedula);
            parametros.put("codigo", codigo);
            Request request = new Request("incidentes_registrados/inactivar", "/{id}/{cedula}/{codigo}", parametros);
            request.put(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el incidente");
            }
            IncidentesRegistradosDTO result = (IncidentesRegistradosDTO) request.readEntity(IncidentesRegistradosDTO.class);
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
