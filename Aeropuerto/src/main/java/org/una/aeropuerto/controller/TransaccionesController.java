/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.una.aeropuerto.util.UserAuthenticated;
/**
 * FXML Controller class
 *
 * @author cordo
 */
public class TransaccionesController extends Controller implements Initializable {

    @FXML
    private TableView  tablaTransac;
    @FXML
    private JFXTextField txtBuscarTransacciones;
    private Map<String,String> modoDesarrollo;
    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
    }    

    @Override
    public void initialize() {
    }

    @FXML
    private void actBuscar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            
        }
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista Transacciones");
        modoDesarrollo.put("Buscar", "Responde al método actBuscar");
        modoDesarrollo.put("Generar", "Responde al método actGenerarReporte");
        modoDesarrollo.put("Empleado", "Responde al método atBuscarEmpleado");
    }
    
    @Override
    public void cargarTema() {
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            
        }
    }

    @FXML
    private void actBuscarEmpleado(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            
        }
    }
    
}
