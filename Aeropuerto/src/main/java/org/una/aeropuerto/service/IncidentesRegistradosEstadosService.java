/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import org.una.aeropuerto.dto.IncidentesRegistradosEstadosDTO;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.TransactionRecorder;

/**
 *
 * @author cordo
 */
public class IncidentesRegistradosEstadosService {

    public Respuesta guardarIncidentesRegistradosEstados(IncidentesRegistradosEstadosDTO incidente) {
        try {
            Request request = new Request("incidentes_registrados_estados/save");
            request.post(incidente);
            if (request.isError()) {
                return new Respuesta(false, request.getError(), "No se pudo guardar el estado de incidente registrado");
            }
            IncidentesRegistradosEstadosDTO result = (IncidentesRegistradosEstadosDTO) request.readEntity(IncidentesRegistradosEstadosDTO.class);
            try{
                TransactionRecorder.registrarTransaccion("Guardar Estado del incidente");
            }catch(Exception ex){}
            return new Respuesta(true, "Incidentes_Registrados_E", result);
        } catch (Exception ex) {
            return new Respuesta(false, ex.toString(), "No puedo establecerce conexion con el servidor");
        }
    }

}
