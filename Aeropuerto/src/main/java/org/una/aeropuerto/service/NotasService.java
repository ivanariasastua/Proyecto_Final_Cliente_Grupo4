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
import org.una.aeropuerto.dto.NotasDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class NotasService {

    public Respuesta getAll() {
        try {
            Request request = new Request("notas/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos las notas");
            }
            List<NotasDTO> result = (List<NotasDTO>) request.readEntity(new GenericType<List<NotasDTO>>() {
            });
            return new Respuesta(true, "Notas", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarNota(NotasDTO notas) {
        try {
            Request request = new Request("notas/save");
            request.post(notas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar la nota");
            }
            NotasDTO result = (NotasDTO) request.readEntity(NotasDTO.class);
            return new Respuesta(true, "Notas", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarNota(Long id, NotasDTO notas) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("notas/editar", "/{id}", parametros);
            request.put(notas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar la nota");
            }
            NotasDTO result = (NotasDTO) request.readEntity(NotasDTO.class);
            return new Respuesta(true, "Notas", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta inactivarNota(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("notas/inactivar", "/{id}", parametros);
            request.put(id);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar la nota");
            }
            NotasDTO result = (NotasDTO) request.readEntity(NotasDTO.class);
            return new Respuesta(true, "Notas", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByServiciosGastos(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("notas/notas_gastos_mantenimientos", "/{id}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los datos");
            }
            List<NotasDTO> result = (List<NotasDTO>) request.readEntity(new GenericType<List<NotasDTO>>() {
            });
            return new Respuesta(true, "Notas", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
