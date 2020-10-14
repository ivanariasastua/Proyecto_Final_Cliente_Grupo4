/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import java.util.HashMap;
import java.util.Map;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.dto.EmpleadosDTO;

/**
 *
 * @author Ivan Josué Arias Astua
 */
public class CambioContrasenaService {
    
    public Respuesta enviarCorreo(String cedula){
        try{
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("cedula", cedula);
            Request request = new Request("changePassword/sendEmail", "/{cedula}", parametros);
            request.get();
            if(request.isError()){
                return new Respuesta(Boolean.FALSE, "Hubo un error al enviar el correo");
            }
            return new Respuesta(Boolean.TRUE, "El correo ha sido enviado");
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "Enviar Correo: Error al comunicarse con el servidor");
        }
    }
    
    public Respuesta cambioContrasena(EmpleadosDTO empleado){
        try{
            Request request = new Request("changePassword/cambiarContrasena");
            request.post(empleado);
            if(request.isError()){
                return new Respuesta(Boolean.FALSE, "Hubo un error al cambiar la contraseña");
            }
            empleado = (EmpleadosDTO) request.readEntity(EmpleadosDTO.class);
            return new Respuesta(Boolean.TRUE, "Empleado", empleado);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "Cambiar Contraseña: Error al comunicarse con el servidor");
        }
    }
}
