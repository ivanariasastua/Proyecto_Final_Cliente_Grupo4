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
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class RolesService {

    public Respuesta getAll() {
        try {
            Request request = new Request("roles/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los roles");
            }
            List<RolesDTO> result = (List<RolesDTO>) request.readEntity(new GenericType<List<RolesDTO>>() {
            });
            return new Respuesta(true, "Roles", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarRol(RolesDTO roles) {
        try {
            Request request = new Request("roles/save");
            request.post(roles);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el rol");
            }
            RolesDTO result = (RolesDTO) request.readEntity(RolesDTO.class);
            return new Respuesta(true, "Roles", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarRol(Long id, RolesDTO roles) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("roles/editar", "/{id}", parametros);
            request.put(roles);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el rol");
            }
            RolesDTO result = (RolesDTO) request.readEntity(RolesDTO.class);
            return new Respuesta(true, "Roles", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByNombre(String nombre) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", nombre);
            Request request = new Request("roles/nombre", "/{term}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los roles");
            }
            List<RolesDTO> result = (List<RolesDTO>) request.readEntity(new GenericType<List<RolesDTO>>() {
            });
            return new Respuesta(true, "Roles", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
