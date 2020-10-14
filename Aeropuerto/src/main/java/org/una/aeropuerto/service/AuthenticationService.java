/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.service;

import org.una.aeropuerto.dto.AuthenticationRequest;
import org.una.aeropuerto.dto.AuthenticationResponse;
import org.una.aeropuerto.util.Request;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 *
 * @author cordo
 */
public class AuthenticationService {
    
    public Respuesta LogIn(String userName, String userPassword){
        try{
            AuthenticationRequest authetication = new AuthenticationRequest(userName, userPassword);
            Request request = new Request("authentication/login");
            request.post(authetication);
            if(request.isError()){
                return new Respuesta(false, request.getError(), "Iniciar Sesion: "+request.getMensajeRespuesta());
            }
            AuthenticationResponse usuario = (AuthenticationResponse) request.readEntity(AuthenticationResponse.class);
            UserAuthenticated.getInstance().setData(usuario.getEmpleado(), usuario.getRol(), usuario.getJwt());
            return new Respuesta(true, "Usuario", usuario);
        }catch(Exception ex){
            return new Respuesta(false, ex.toString(), "Iniciar Sesion: Error al comunicarse con el servidor");
        }
    }
}
