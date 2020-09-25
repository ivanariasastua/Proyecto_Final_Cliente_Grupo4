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

    public Respuesta getAll() {
        try {
            Request request = new Request("gastos_mantenimientos/get");
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener todos los gastos de servicios");
            }
            List<ServiciosGastosDTO> result = (List<ServiciosGastosDTO>) request.readEntity(new GenericType<List<ServiciosGastosDTO>>() {
            });
            return new Respuesta(true, "Servicios_Gastos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

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

    public Respuesta inactivarGastoServicio(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("gastos_mantenimientos/inactivar", "/{id}", parametros);
            request.put(id);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo inactivar el gasto de servicio");
            }
            ServiciosGastosDTO result = (ServiciosGastosDTO) request.readEntity(ServiciosGastosDTO.class);
            return new Respuesta(true, "Servicios_Gastos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta getByServicios(Long id) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("gastos_mantenimientos/gastos_servicios", "/{id}", parametros);
            request.get();
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "Error al obtener los datos");
            }
            List<ServiciosGastosDTO> result = (List<ServiciosGastosDTO>) request.readEntity(new GenericType<List<ServiciosGastosDTO>>() {
            });
            return new Respuesta(true, "Servicios_Gastos", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
