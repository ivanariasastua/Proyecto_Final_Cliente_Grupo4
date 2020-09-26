/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class ServiciosController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtNombreServicio;
    @FXML
    private JFXTextArea txtDescripcionServicio;
    @FXML
    private JFXTextField txtBuscarServicio;

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
    private void actGuardarServicio(ActionEvent event) {
    }

    @FXML
    private void acteditarServicio(ActionEvent event) {
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
    }

    @FXML
    private void actLimpiarCamposServicio(ActionEvent event) {
    }
    
}
