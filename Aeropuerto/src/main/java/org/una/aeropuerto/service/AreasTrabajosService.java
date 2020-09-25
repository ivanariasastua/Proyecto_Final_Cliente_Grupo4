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
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class AreasTrabajosService {
    
    public Respuesta getAll() {
        try {
            Request request = new Request("areas_trabajos/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos las areas de trabajos");
            }
            List<AreasTrabajosDTO> result = (List<AreasTrabajosDTO>) request.readEntity(new GenericType<List<AreasTrabajosDTO>>(){});
            return new Respuesta(true, "Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }


    public Respuesta guardarAreaTrabajo(AreasTrabajosDTO areas) {
        try {
            Request request = new Request("areas_trabajos/save");
            request.post(areas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el area de trabajo");
            }
            AreasTrabajosDTO result = (AreasTrabajosDTO) request.readEntity(AreasTrabajosDTO.class);
            return new Respuesta(true, "Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarAreaTrabajo(Long id, AreasTrabajosDTO areas) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("areas_trabajos/editar", "/{id}", parametros);
            request.put(areas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el area de trabajo");
            }
            AreasTrabajosDTO result = (AreasTrabajosDTO) request.readEntity(AreasTrabajosDTO.class);
            return new Respuesta(true, "Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta inactivarAreaTrabajo(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("areas_trabajos/inactivar", "/{id}", parametros);
            request.put(id);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el area de trabajo");
            }
            AreasTrabajosDTO result = (AreasTrabajosDTO) request.readEntity(AreasTrabajosDTO.class);
            return new Respuesta(true, "Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByNombre(String nombre){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", nombre);
            Request request = new Request("areas_trabajos/nombre", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener las areas de trabjo");
            }
            List<AreasTrabajosDTO> result = (List<AreasTrabajosDTO>) request.readEntity(new GenericType<List<AreasTrabajosDTO>>(){});
            return new Respuesta(true, "Areas_Trabajos",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
