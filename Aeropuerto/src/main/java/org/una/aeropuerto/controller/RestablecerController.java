/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.StageStyle;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.service.CambioContrasenaService;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Formato;
/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class RestablecerController extends Controller implements Initializable {

    @FXML private JFXTextField txtNewPass;
    @FXML private JFXTextField txtConfPass;
    private final CambioContrasenaService service = new CambioContrasenaService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtNewPass.setTextFormatter(Formato.getInstance().maxLengthFormat(30));
        txtConfPass.setTextFormatter(Formato.getInstance().maxLengthFormat(30));
    }    

    @FXML
    private void accionRestaurar(ActionEvent event) {
        if(validar()){
            if(txtNewPass.getText().equals(txtConfPass.getText())){
                EmpleadosDTO emp = UserAuthenticated.getInstance().getUsuario();
                emp.setContrasenaEncriptada(txtNewPass.getText());
                emp.setPasswordTemporal(Boolean.FALSE);
                Respuesta res = service.cambioContrasena(emp);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Cambiar Contraseña", "La contraseña se ha sido modificada");
                    FlowController.getInstance().goViewInNoResizableWindow("LogIn", Boolean.TRUE, StageStyle.UNDECORATED);
                    this.closeWindow();
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Cambiar Contraseña", res.getMensaje());
                }
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Cambiar Contraseña", "Las contraseñas no so iguales");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Cambiar Contraseña", "Debe rellenar ambos campos de texto");
        }
    }

    @Override
    public void initialize() {
        txtConfPass.setText("");
        txtNewPass.setText("");
    }
    
    private Boolean validar(){
        return txtNewPass.getText() != null && !txtNewPass.getText().isEmpty() && txtConfPass.getText() != null && !txtConfPass.getText().isEmpty();
    }
    
}
