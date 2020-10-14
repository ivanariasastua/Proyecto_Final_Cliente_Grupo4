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
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class IncidentesCategoriasService {

    public Respuesta guardarIncidentesCategorias(IncidentesCategoriasDTO incidente) {
        try {
            Request request = new Request("incidentes_categorias/save");
            request.post(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar la categoria de incidente");
            }
            IncidentesCategoriasDTO result = (IncidentesCategoriasDTO) request.readEntity(IncidentesCategoriasDTO.class);
            return new Respuesta(true, "Incidentes_Categorias", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarIncidentesCategorias(Long id, IncidentesCategoriasDTO incidente) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("incidentes_categorias/editar", "/{id}", parametros);
            request.put(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar la categoria de incidente");
            }
            IncidentesCategoriasDTO result = (IncidentesCategoriasDTO) request.readEntity(IncidentesCategoriasDTO.class);
            return new Respuesta(true, "Incidentes_Categorias", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByNombre(String nombre) {
       try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", nombre);
            Request request = new Request("incidentes_categorias/nombre", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener las categorias");
            }
            List<IncidentesCategoriasDTO> result = (List<IncidentesCategoriasDTO>) request.readEntity(new GenericType<List<IncidentesCategoriasDTO>>(){});
            return new Respuesta(true, "Incidentes_Categorias",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByEstado(boolean estado) {
       try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", estado);
            Request request = new Request("incidentes_categorias/estado", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener las categorias");
            }
            List<IncidentesCategoriasDTO> result = (List<IncidentesCategoriasDTO>) request.readEntity(new GenericType<List<IncidentesCategoriasDTO>>(){});
            return new Respuesta(true, "Incidentes_Categorias",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
