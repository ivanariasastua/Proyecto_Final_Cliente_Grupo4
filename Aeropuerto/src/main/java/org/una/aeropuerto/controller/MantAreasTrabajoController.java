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
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class MantAreasTrabajoController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextField txtDescripcion;
    @FXML
    private JFXTextField txtEstado;

    private AreasTrabajosDTO areaDto = new AreasTrabajosDTO();
    private AreasTrabajosDTO areaSelec = new AreasTrabajosDTO();
    private AreasTrabajosService areasService = new AreasTrabajosService();
    boolean areaSelect = false;

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
        limpiarAreas();
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (areaSelect == true) {
                if (validarActivos()) {
                    areaSelec.setId(areaSelec.getId());
                    areaSelec.setDescripcion(txtDescripcion.getText());
                    areaSelec.setNombre(txtNombre.getText());
                    Respuesta res = areasService.modificarAreaTrabajo(areaSelec.getId(), areaSelec);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Area de trabajo editada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            } else {
                if (txtNombre.getText() == null) {
                    Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Nombre es obligatorio");
                } else {
                    areaDto = new AreasTrabajosDTO();
                    areaDto.setDescripcion(txtDescripcion.getText());
                    areaDto.setNombre(txtNombre.getText());
                    Respuesta res = areasService.guardarAreaTrabajo(areaDto);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Area de trabajo guardada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

    public void limpiarAreas() {
        txtId.setText(null);
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtEstado.setText(null);
        areaSelect = false;
        areaSelec = new AreasTrabajosDTO();
    }

    @FXML
    private void actLimpiar(ActionEvent event) {
        limpiarAreas();
    }

    public boolean validarActivos() {
        if (areaSelec.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    public void cargarDatos(AreasTrabajosDTO area) {
        areaSelec = area;
        areaSelect = true;
        txtDescripcion.setText(area.getDescripcion());
        txtEstado.setText(estado(area.isEstado()));
        txtId.setText(String.valueOf(area.getId()));
        txtNombre.setText(area.getNombre());
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }

}
