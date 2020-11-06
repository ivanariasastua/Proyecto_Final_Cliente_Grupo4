/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.util;

import java.util.Date;
import org.una.aeropuerto.service.TransaccionesService;
import org.una.aeropuerto.dto.TransaccionesDTO;
/**
 *
 * @author Ivan Josué Arias Astua
 */
public class TransactionRecorder {
    
    private final static TransaccionesService service = new TransaccionesService();
    
    public static void registrarTransaccion(String accion){
        TransaccionesDTO dto = new TransaccionesDTO(0L, UserAuthenticated.getInstance().getUsuario(), accion, new Date());
        service.guardarTransaccion(dto);
    }
}
