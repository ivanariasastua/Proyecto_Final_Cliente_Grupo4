/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.ServiciosDTO;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class GastosServiciosController extends Controller implements Initializable {

    @FXML
    private TableView tablaGastosS;
    @FXML
    private JFXComboBox<String> cbxEstadoPago;
    @FXML
    private JFXTextField txtNumContrato;
    @FXML
    private JFXTextField txtEmpresa;
    @FXML
    private JFXComboBox<String> cbxEstadoGasto;
    @FXML
    private JFXComboBox<?> cbxPerioricidad;
    @FXML
    private JFXComboBox<EmpleadosDTO> cbxResponsable;
    @FXML
    private JFXComboBox<ServiciosDTO> cbxServicio;
    @FXML
    private JFXComboBox<?> cbxDuracion;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void initialize() {
    }

    @FXML
    private void actEditarGastoS(ActionEvent event) {
    }

    
    
    //tab de crear gastos de servicios
    @FXML
    private void actGuardarGastoS(ActionEvent event) {
    }

    @FXML
    private void actLimpiarGastoS(ActionEvent event) {
    }
    
}
