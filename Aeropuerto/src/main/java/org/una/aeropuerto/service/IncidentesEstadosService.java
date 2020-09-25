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
import org.una.aeropuerto.dto.IncidentesEstadosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class IncidentesEstadosService {

    public Respuesta getAll() {
        try {
            Request request = new Request("incidentes_estados/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los estados de incidentes");
            }
            List<IncidentesEstadosDTO> result = (List<IncidentesEstadosDTO>) request.readEntity(new GenericType<List<IncidentesEstadosDTO>>() {
            });
            return new Respuesta(true, "Incidentes_Estados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarIncidentesEstados(IncidentesEstadosDTO incidente) {
        try {
            Request request = new Request("incidentes_estados/save");
            request.post(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el estado de incidente");
            }
            IncidentesEstadosDTO result = (IncidentesEstadosDTO) request.readEntity(IncidentesEstadosDTO.class);
            return new Respuesta(true, "Incidentes_Estados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarIncidentesEstados(Long id, IncidentesEstadosDTO incidente) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("incidentes_estados/editar", "/{id}", parametros);
            request.put(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el estado de incidente");
            }
            IncidentesEstadosDTO result = (IncidentesEstadosDTO) request.readEntity(IncidentesEstadosDTO.class);
            return new Respuesta(true, "Incidentes_Estados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByEstado(String nombre) {
        try {
            Request request = new Request("incidentes_estados/estado");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los estados de incidentes");
            }
            List<IncidentesEstadosDTO> result = (List<IncidentesEstadosDTO>) request.readEntity(new GenericType<List<IncidentesEstadosDTO>>() {
            });
            return new Respuesta(true, "Incidentes_Estados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
