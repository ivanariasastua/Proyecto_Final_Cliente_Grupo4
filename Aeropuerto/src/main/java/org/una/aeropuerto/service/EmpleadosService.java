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
import org.una.aeropuerto.util.TransactionRecorder;

/**
 *
 * @author cordo
 */
public class EmpleadosService {
    
    public Respuesta guardarEmpleado(EmpleadosDTO empleado) {
        try {
            Request request = new Request("empleados/save");
            request.post(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Guardar Empleado");
            }catch(Exception ex){}
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
            try{
                TransactionRecorder.registrarTransaccion("Modificar Empleado");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByCedula(String cedula){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cedula", cedula);
            Request request = new Request("empleados/cedula", "/{cedula}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los empleados");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>(){});
            return new Respuesta(true, "Empleados", result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByNombre(String nombre){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("nombre", nombre);
            Request request = new Request("empleados/nombre", "/{nombre}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los empleados");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>(){});
            return new Respuesta(true, "Empleados", result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByArea(String area){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("area", area);
            Request request = new Request("empleados/area", "/{area}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los empleados");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>(){});
            return new Respuesta(true, "Empleados", result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
     public Respuesta getNoAprobadosbyRol(Long rol) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("rol", rol);
            Request request = new Request("empleados/getNoAprobados","/{rol}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los empleados\ncuyo rol no ha sido aprobado");
            }
            List<EmpleadosDTO> result = (List<EmpleadosDTO>) request.readEntity(new GenericType<List<EmpleadosDTO>>(){});
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta inactivar(EmpleadosDTO emp, Long id, String cedula, String codigo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            parametros.put("cedula", cedula);
            parametros.put("codigo", codigo);
            Request request = new Request("empleados/inactivar", "/{id}/{cedula}/{codigo}", parametros);
            request.put(emp);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Inactivar Empleado");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta Aprobar(Long id){
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados/aprobar","/{id}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo aprobar el empleado");
            }
            EmpleadosDTO result = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Aprobar Empleado");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        } 
    }
}
