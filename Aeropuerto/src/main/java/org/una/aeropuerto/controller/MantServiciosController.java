/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.Formato;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class MantServiciosController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXTextField txtNombre;

    boolean servSeleccionado = false;
    private ServiciosDTO servSelect;
    private ServiciosDTO servicioDTO = new ServiciosDTO();
    private ServiciosService servService = new ServiciosService();
    private Map<String,String> modoDesarrollo;
    @FXML
    private Label lblDesarrollo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
        txtDescripcion.setTextFormatter(Formato.getInstance().maxLengthFormat(100));
        txtNombre.setTextFormatter(Formato.getInstance().maxLengthFormat(25));
    }

    @Override
    public void initialize() {
        servSelect = new ServiciosDTO();
        lblDesarrollo.setText("");
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lblDesarrollo.setText(modoDesarrollo.get("Vista")+"\n"+modoDesarrollo.get("Guardar"));
        } else {
            if (servSeleccionado == true) {
                if (validarActivos()) {
                    servSelect.setId(servSelect.getId());
                    servSelect.setDescripcion(txtDescripcion.getText());
                    servSelect.setNombre(txtNombre.getText());
                    Respuesta res = servService.modificarServicio(servSelect.getId(), servSelect);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Servicio editado correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            } else {
                if (txtNombre.getText() != null) {
                    servicioDTO = new ServiciosDTO();
                    servicioDTO.setDescripcion(txtDescripcion.getText());
                    servicioDTO.setNombre(txtNombre.getText());
                    Respuesta res = servService.guardarServicio(servicioDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Servicio guardado correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Nombre es obligatorio");
                }
            }
        }
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista MantServicios");
        modoDesarrollo.put("Limpiar", "Limpiar responde al método actLimpiar");
        modoDesarrollo.put("Guardar", "Guardar responde al método actGuardar");
    }
    
    public boolean validarActivos() {
        if (servSelect.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    public void limpiarServicios() {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR"))
            lblDesarrollo.setText(modoDesarrollo.get("Vista")+"\n"+modoDesarrollo.get("Guardar"));
        servSeleccionado = false;
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtId.setText(null);
    }

    @FXML
    private void actLimpiar(ActionEvent event) {
        limpiarServicios();
    }

    public void cargarDatos(ServiciosDTO servicio) {
        servSelect = servicio;
        servSeleccionado = true;
        txtDescripcion.setText(servicio.getDescripcion());
        txtId.setText(String.valueOf(servicio.getId()));
        txtNombre.setText(servicio.getNombre());
    }

}
