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
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.TransactionRecorder;

/**
 *
 * @author cordo
 */
public class EmpleadosMarcajesService {

    public Respuesta guardarEmpleadoMarcaje(EmpleadosMarcajesDTO empleado) {
        try {
            Request request = new Request("empleados_marcajes/save");
            request.post(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el marcaje del empleado");
            }
            EmpleadosMarcajesDTO result = (EmpleadosMarcajesDTO) request.readEntity(EmpleadosMarcajesDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Guardar marcaje");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados_Marcajes", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarEmpleadoMarcaje(Long id, EmpleadosMarcajesDTO empleado) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados_marcajes/editar", "/{id}", parametros);
            request.put(empleado);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el marcaje del empleado");
            }
            EmpleadosMarcajesDTO result = (EmpleadosMarcajesDTO) request.readEntity(EmpleadosMarcajesDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Modificar marcaje");
            }catch(Exception ex){}
            return new Respuesta(true, "Empleados_Marcajes", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getLastByHorarioId(Long id){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("empleados_marcajes/Ultimo", "/{id}", parametros);
            request.get();
            if(request.isError()){
               return new Respuesta(false, request.getError(), "No se pudo modificar el marcaje del empleado");
            }
            EmpleadosMarcajesDTO result = (EmpleadosMarcajesDTO) request.readEntity(EmpleadosMarcajesDTO.class);
            return new Respuesta(true, "Empleados_Marcajes", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
