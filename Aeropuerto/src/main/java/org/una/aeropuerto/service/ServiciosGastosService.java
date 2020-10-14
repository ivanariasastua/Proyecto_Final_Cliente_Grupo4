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
import org.una.aeropuerto.dto.ServiciosGastosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class ServiciosGastosService {

    public Respuesta guardarGastoServicio(ServiciosGastosDTO servicio) {
        try {
            Request request = new Request("gastos_mantenimientos/save");
            request.post(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el gasto de servicio");
            }
            ServiciosGastosDTO result = (ServiciosGastosDTO) request.readEntity(ServiciosGastosDTO.class);
            return new Respuesta(true, "Servicios_Gastos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarGastoServicio(Long id, ServiciosGastosDTO servicio) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("gastos_mantenimientos/editar", "/{id}", parametros);
            request.put(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el gasto de servicio");
            }
            ServiciosGastosDTO result = (ServiciosGastosDTO) request.readEntity(ServiciosGastosDTO.class);
            return new Respuesta(true, "Servicios_Gastos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByEmpresa(String empresa){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", empresa);
            Request request = new Request("gastos_mantenimientos/empresa", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los gastos de servios");
            }
            List<ServiciosGastosDTO> result = (List<ServiciosGastosDTO>) request.readEntity(new GenericType<List<ServiciosGastosDTO>>(){});
            return new Respuesta(true, "Servicios_Gastos",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta getByContrato(String contrato){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", contrato);
            Request request = new Request("gastos_mantenimientos/numeroContrato", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los gastos de servios");
            }
            List<ServiciosGastosDTO> result = (List<ServiciosGastosDTO>) request.readEntity(new GenericType<List<ServiciosGastosDTO>>(){});
            return new Respuesta(true, "Servicios_Gastos",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
    
    public Respuesta findByServicio(String servicio){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("term", servicio);
            Request request = new Request("gastos_mantenimientos/servicio", "/{term}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Error al obtener los gastos de servios");
            }
            List<ServiciosGastosDTO> result = (List<ServiciosGastosDTO>) request.readEntity(new GenericType<List<ServiciosGastosDTO>>(){});
            return new Respuesta(true, "Servicios_Gastos",result);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
