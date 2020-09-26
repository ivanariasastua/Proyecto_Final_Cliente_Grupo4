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
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class EmpleadosService {

    public Respuesta getAll() {
        try {
            Request request = new Request("empleados/get");
            request.get();
            if (request.isError()) {
                System.out.println("ERROR "+request.getError() +" "+request.getMensajeRespuesta());
                return new Respuesta(false, request.getError(), "Error al obtener todos los empleados");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>() {
            });
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarEmpleado(EmpleadosDTO empleado) {
        try {
            Request request = new Request("empleados/save");
            request.post(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarEmpleado(Long id, EmpleadosDTO empleado) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados/editar", "/{id}", parametros);
            request.put(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta inactivarEmpleado(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados/inactivar", "/{id}", parametros);
            request.put(id);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getFiltro(String nombre, String cedula, boolean estado, String area) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nombre", nombre);
            parametros.put("cedula", cedula);
            parametros.put("estado", estado);
            parametros.put("area", area);
            Request request = new Request("empleados/filter", "/{nombre}/{cedula}/{estado}/{area}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los empleados");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>() {
            });
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
