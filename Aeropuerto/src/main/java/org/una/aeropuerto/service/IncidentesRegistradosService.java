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

    public Respuesta getAll() {
        try {
            Request request = new Request("incidentes_registrados/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los  incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>() {
            });
            return new Respuesta(true, "Incidentes_Registrados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

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

    public Respuesta inactivarIncidenteRegistrado(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("incidentes_registrados/inactivar", "/{id}", parametros);
            request.put(id);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el incidente registrado");
            }
            IncidentesRegistradosDTO result = (IncidentesRegistradosDTO) request.readEntity(IncidentesRegistradosDTO.class);
            return new Respuesta(true, "Incidentes_Registrados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getFiltro(String nomEmisor, String cedEmisor, String nomResponsable, String cedResponsable, String nomCategoria, String nomArea) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nomEmisor", nomEmisor);
            parametros.put("cedEmisor", cedEmisor);
            parametros.put("nomResponsable", nomResponsable);
            parametros.put("cedResponsable", cedResponsable);
            parametros.put("nomCategoria", nomCategoria);
            parametros.put("nomArea", nomArea);
            Request request = new Request("incidentes_registrados/filtro", "/{nomEmisor}/{cedEmisor}/{nomResponsable}/{cedResponsable}/{nomCategoria}/{nomArea}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los incidentes registrados");
            }
            List<IncidentesRegistradosDTO> result = (List<IncidentesRegistradosDTO>) request.readEntity(new GenericType<List<IncidentesRegistradosDTO>>() {
            });
            return new Respuesta(true, "Incidentes_Registrados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

}
