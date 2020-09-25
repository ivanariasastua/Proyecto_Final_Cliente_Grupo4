/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.una.aeropuerto.util.FlowController;

/**
 * FXML Controller class
 *
 * @author ivana
 */
public class LogInController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtUserName;
    @FXML
    private JFXPasswordField txtPassword;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @Override
    public void initialize() {
    }

    @FXML
    private void accionLogIn(ActionEvent event) {
        
        FlowController.getInstance().goViewInResizableWindow("Principal", 0, 1100, 0, 700, Boolean.TRUE, StageStyle.UNDECORATED);
        this.closeWindow();
    }

    @FXML
    private void accionCerrar(ActionEvent event) {
        this.getStage().close();
    }

    @FXML
    private void accionMinimizar(MouseEvent event) {
        this.minimizeWindow();
    }
    
}
