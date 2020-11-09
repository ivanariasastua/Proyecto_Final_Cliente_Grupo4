/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.util;

import java.util.Date;
import org.una.aeropuerto.service.TransaccionesService;
import org.una.aeropuerto.dto.TransaccionesDTO;
import org.una.aeropuerto.dto.EmpleadosDTO;
/**
 *
 * @author Ivan Josué Arias Astua
 */
public class TransactionRecorder {
    
    private final static TransaccionesService service = new TransaccionesService();
    
    public static void registrarTransaccion(String accion){
        EmpleadosDTO usu =UserAuthenticated.getInstance().getUsuario();
        TransaccionesDTO dto = new TransaccionesDTO(0L,usu,accion,new Date());
        Respuesta res =service.guardarTransaccion(dto);
        System.out.println(res.getMensaje());
    }
}
