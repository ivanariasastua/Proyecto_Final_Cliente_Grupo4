/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import java.util.HashMap;
import java.util.Map;
import org.una.aeropuerto.dto.ServiciosPreciosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;

/**
 *
 * @author cordo
 */
public class ServiciosPreciosService {

    public Respuesta guardarPrecioServicio(ServiciosPreciosDTO servicio) {
        try {
            Request request = new Request("servicios_precios/save");
            request.post(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el precio de servicio");
            }
            ServiciosPreciosDTO result = (ServiciosPreciosDTO) request.readEntity(ServiciosPreciosDTO.class);
            return new Respuesta(true, "Servicios_Precios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

    public Respuesta modificarPrecioServicio(Long id, ServiciosPreciosDTO servicio) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("id", id);
            Request request = new Request("servicios_precios/editar", "/{id}", parametros);
            request.put(servicio);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo modificar el precio de servicio");
            }
            ServiciosPreciosDTO result = (ServiciosPreciosDTO) request.readEntity(ServiciosPreciosDTO.class);
            return new Respuesta(true, "Servicios_Precios", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }
}
