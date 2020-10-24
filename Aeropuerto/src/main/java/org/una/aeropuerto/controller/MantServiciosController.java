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
import javafx.scene.control.Alert;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.service.ServiciosService;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void cargarTema() {
    }

    @Override
    public void initialize() {
        servSelect = new ServiciosDTO();
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

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

    public boolean validarActivos() {
        if (servSelect.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    public void limpiarServicios() {
        servSeleccionado = false;
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtId.setText(null);
    }

    @FXML
    private void actLimpiar(ActionEvent event) {
    }

    public void cargarDatos(ServiciosDTO servicio) {
        servSelect = servicio;
        servSeleccionado = true;
        txtDescripcion.setText(servicio.getDescripcion());
        txtId.setText(String.valueOf(servicio.getId()));
        txtNombre.setText(servicio.getNombre());
    }

}
