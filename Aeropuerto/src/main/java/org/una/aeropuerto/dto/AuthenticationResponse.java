/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author ivana
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AuthenticationResponse {

    private String jwt;
    private EmpleadosDTO empleado;
    private RolesDTO rol;

}