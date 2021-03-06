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
import org.una.aeropuerto.dto.EmpleadosAreasTrabajosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.TransactionRecorder;

/**
 *
 * @author cordo
 */
public class EmpleadosAreasTrabajosService {

    public Respuesta getAll() {
        try {
            Request request = new Request("empleados_areas_trabajos/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los empleados areas de trabajos");
            }
            List<EmpleadosAreasTrabajosDTO> result = (List<EmpleadosAreasTrabajosDTO>) request.readEntity(new GenericType<List<EmpleadosAreasTrabajosDTO>>() {
            });
            return new Respuesta(true, "Empleados_Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta guardarEmpleadoAreaTrabajo(EmpleadosAreasTrabajosDTO empleadosAreas) {
        try {
            Request request = new Request("empleados_areas_trabajos/save");
            request.post(empleadosAreas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el empleados area de trabajo");
            }
            EmpleadosAreasTrabajosDTO result = (EmpleadosAreasTrabajosDTO) request.readEntity(EmpleadosAreasTrabajosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Asignar Area de Trabajo");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados_Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarEmpleadoAreaTrabajo(Long id, EmpleadosAreasTrabajosDTO empleadosAreas) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados_areas_trabajos/editar", "/{id}", parametros);
            request.put(empleadosAreas);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el empleados area de trabajo");
            }
            EmpleadosAreasTrabajosDTO result = (EmpleadosAreasTrabajosDTO) request.readEntity(EmpleadosAreasTrabajosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Modificar asignacion del Area de Trabajo");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados_Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta findByArea(String area){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", area);
            Request request = new Request("empleados_areas_trabajos/area", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener las areas de trabjo");
            }
            List<EmpleadosAreasTrabajosDTO> result = (List<EmpleadosAreasTrabajosDTO>) request.readEntity(new GenericType<List<EmpleadosAreasTrabajosDTO>>(){});
            return new Respuesta(true, "Empleados_Areas_Trabajos",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta inactivar(EmpleadosAreasTrabajosDTO area, Long id, String cedula, String codigo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            parametros.put("cedula", cedula);
            parametros.put("codigo", codigo);
            Request request = new Request("empleados_areas_trabajos/inactivar", "/{id}/{cedula}/{codigo}", parametros);
            request.put(area);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar area de trabajo del empleado");
            }
            EmpleadosAreasTrabajosDTO result = (EmpleadosAreasTrabajosDTO) request.readEntity(EmpleadosAreasTrabajosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Inactivar asignacion de Area de Trabajo");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados_Areas_Trabajos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
