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
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import org.una.aeropuerto.service.AuthenticationService;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

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

    private AuthenticationService service = new AuthenticationService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initialize() {
        txtUserName.setText("");
        txtUserName.setText("");
    }

    @FXML
    private void accionLogIn(ActionEvent event) {
        if (camposValidos()) {
            Respuesta respuesta = service.LogIn(txtUserName.getText(), txtPassword.getText());
            if (respuesta.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Inicio de sesion", "Inicio de sesion: Sesion iniciada correctamente");
                FlowController.getInstance().goViewInResizableWindow("Principal", 0, 1100, 0, 700, Boolean.TRUE, StageStyle.UNDECORATED);
                this.closeWindow();
            } else {
                Mensaje.show(Alert.AlertType.ERROR, "Inicio de sesion", respuesta.getMensaje());
            }
        }
    }

    @FXML
    private void accionCerrar(ActionEvent event) {
        this.getStage().close();
    }

    @FXML
    private void accionMinimizar(MouseEvent event) {
        this.minimizeWindow();
    }

    private Boolean camposValidos() {
        String mensaje = "";
        if (txtUserName.getText() == null || txtPassword.getText().isEmpty()) {
            mensaje = "Campo de texto cedula, esta vacío\n";
        }
        if (txtPassword.getText() == null || txtPassword.getText().isEmpty()) {
            mensaje += "El campo de contraseña esta vacío";
        }
        if (mensaje.isEmpty()) {
            return true;
        }
        Mensaje.show(Alert.AlertType.WARNING, "Inicio de sesion", mensaje);
        return false;
    }

}
