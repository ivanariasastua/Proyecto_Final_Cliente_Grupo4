/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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

    @FXML private JFXPasswordField txtNewPass;
    @FXML private JFXPasswordField txtConfPass;
    private final CambioContrasenaService service = new CambioContrasenaService();
    @FXML private JFXCheckBox cbNew;
    @FXML private JFXCheckBox cbConf;
    @FXML private JFXTextField txtViewNew;
    @FXML private JFXTextField txtViewConf;
    private Map<String,String> modoDesarrollo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtNewPass.setTextFormatter(Formato.getInstance().maxLengthFormat(30));
        txtConfPass.setTextFormatter(Formato.getInstance().maxLengthFormat(30));
        txtNewPass.textProperty().addListener( t -> {
            if(cbNew.isSelected()){
                txtViewNew.setText(txtNewPass.getText());
            }
        });
        cbNew.selectedProperty().addListener( s -> {
            txtViewNew.setText(cbNew.isSelected() ? txtNewPass.getText() : "");
        });
        txtConfPass.textProperty().addListener( t -> {
            if(cbConf.isSelected()){
                txtViewConf.setText(txtConfPass.getText());
            }
        });
        cbConf.selectedProperty().addListener( s -> {
            txtViewConf.setText(cbConf.isSelected() ? txtConfPass.getText() : "");
        });
        datosModoDesarrollo();
    }    

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista Restablecer");
        modoDesarrollo.put("Restaurar", "Restaurar responde al método accionRestaurar");
    }
    
    @FXML
    private void accionRestaurar(ActionEvent event) {
        if(validar()){
            if(txtNewPass.getText().equals(txtConfPass.getText())){
                EmpleadosDTO emp = UserAuthenticated.getInstance().getUsuario();
                emp.setContrasenaEncriptada(txtNewPass.getText());
                Respuesta res = service.cambioContrasena(emp);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Cambiar Contraseña", "La contraseña se ha sido modificada");
                    this.closeWindow();
                    FlowController.getInstance().goViewInNoResizableWindow("LogIn", Boolean.TRUE, StageStyle.UNDECORATED);
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Cambiar Contraseña", res.getMensaje());
                }
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Cambiar Contraseña", "Las contraseñas no son iguales");
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

    @Override
    public void cargarTema() {
    }
    
}
