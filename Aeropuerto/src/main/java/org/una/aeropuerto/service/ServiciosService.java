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
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class ServiciosService {
    
    public Respuesta getAll() {
        try {
            Request request = new Request("servicios/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los servicios");
            }
            List<ServiciosDTO> result = (List<ServiciosDTO>) request.readEntity(new GenericType<List<ServiciosDTO>>() {
            });
            return new Respuesta(true, "Servicios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarServicio(ServiciosDTO servicio) {
        try {
            Request request = new Request("servicios/save");
            request.post(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el servicio");
            }
            ServiciosDTO result = (ServiciosDTO) request.readEntity(ServiciosDTO.class);
            return new Respuesta(true, "Servicios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarServicio(Long id, ServiciosDTO servicio) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("servicios/editar", "/{id}", parametros);
            request.put(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el servicio");
            }
            ServiciosDTO result = (ServiciosDTO) request.readEntity(ServiciosDTO.class);
            return new Respuesta(true, "Servicios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByNombre(String nombre){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", nombre);
            Request request = new Request("servicios/nombre", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los servicios");
            }
            List<ServiciosDTO> result = (List<ServiciosDTO>) request.readEntity(new GenericType<List<ServiciosDTO>>(){});
            return new Respuesta(true, "Servicios",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByEstado(boolean estado){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", estado);
            Request request = new Request("servicios/estado", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los servicios");
            }
            List<ServiciosDTO> result = (List<ServiciosDTO>) request.readEntity(new GenericType<List<ServiciosDTO>>(){});
            return new Respuesta(true, "Servicios",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
