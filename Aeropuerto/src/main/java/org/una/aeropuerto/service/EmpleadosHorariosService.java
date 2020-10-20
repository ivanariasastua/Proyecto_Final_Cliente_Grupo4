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
import org.una.aeropuerto.dto.EmpleadosHorariosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class EmpleadosHorariosService {
    
    public Respuesta getAll() {
        try {
            Request request = new Request("empleados_horarios/get");
            request.get();
            if (request.isError()) {
                System.out.println("error "+request.getError()+request.getMensajeRespuesta());
                return new Respuesta(false, request.getError(), "Error al obtener todos los horarios de los empleados");
            }
            List<EmpleadosHorariosDTO> result = (List<EmpleadosHorariosDTO>) request.readEntity(new GenericType<List<EmpleadosHorariosDTO>>(){});
            return new Respuesta(true, "Empleados_Horarios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }


    public Respuesta guardarEmpleadoHorario(EmpleadosHorariosDTO empleado) {
        try {
            Request request = new Request("empleados_horarios/save");
            request.post(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el horario del empleado");
            }
            EmpleadosHorariosDTO result = (EmpleadosHorariosDTO) request.readEntity(EmpleadosHorariosDTO.class);
            return new Respuesta(true, "Empleados_Horarios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarEmpleadoHorario(Long id, EmpleadosHorariosDTO empleado) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados_horarios/editar", "/{id}", parametros);
            request.put(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el horario del empleado");
            }
            EmpleadosHorariosDTO result = (EmpleadosHorariosDTO) request.readEntity(EmpleadosHorariosDTO.class);
            return new Respuesta(true, "Empleados_Horarios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta inactivar(EmpleadosHorariosDTO horario, Long id, String cedula, String codigo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            parametros.put("cedula", cedula);
            parametros.put("codigo", codigo);
            Request request = new Request("empleados_horarios/inactivar", "/{id}/{cedula}/{codigo}", parametros);
            request.put(horario);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el horario de trabajo del empleado");
            }
            EmpleadosHorariosDTO result = (EmpleadosHorariosDTO) request.readEntity(EmpleadosHorariosDTO.class);
            return new Respuesta(true, "Empleados_Horarios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
