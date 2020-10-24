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
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class MantCategoriasController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXTextField txtCategoriaSuperior;

    private IncidentesCategoriasDTO categSuperiorSelec;
    private IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    private IncidentesCategoriasDTO categoriaDTO = new IncidentesCategoriasDTO();
    boolean catSelec = false;

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
        limpiarCampos();
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (catSelec == true) {
                if (validarActivos()) {
                    categoriaSelec.setId(categoriaSelec.getId());
                    categoriaSelec.setDescripcion(txtDescripcion.getText());
                    categoriaSelec.setNombre(txtNombre.getText());
                    if (categSuperiorSelec.getNombre() != null && txtCategoriaSuperior.getText()!="(No tiene categoria superior)") {
                        categoriaSelec.setCategoriaSuperior(categSuperiorSelec);
                    }
                    Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaSelec);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Categoria editada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            } else {
                if (txtNombre.getText() == null) {
                    Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo de nombre es obligatorio");
                } else {
                    categoriaDTO = new IncidentesCategoriasDTO();
                    categoriaDTO.setDescripcion(txtDescripcion.getText());
                    categoriaDTO.setNombre(txtNombre.getText());
                    if (categSuperiorSelec.getNombre() != null && txtCategoriaSuperior.getText() != null) {
                        categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
                    }
                    Respuesta res = categoriaService.guardarIncidentesCategorias(categoriaDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoria guardada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

    public void limpiarCampos() {
        categSuperiorSelec = new IncidentesCategoriasDTO();
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtCategoriaSuperior.setText(null);
        catSelec = false;
        categoriaSelec = new IncidentesCategoriasDTO();
    }

    public void cargarDatos(IncidentesCategoriasDTO categoria) {
        catSelec = true;
        categoriaSelec = categoria;
        if (categoria.getCategoriaSuperior()!= null) {
            txtCategoriaSuperior.setText(categoria.getCategoriaSuperior().getNombre());
        } else {
            txtCategoriaSuperior.setText("(No tiene categoria superior)");
        }
        txtDescripcion.setText(categoria.getDescripcion());
        txtNombre.setText(categoria.getNombre());
    }

    @FXML
    private void actLimpiar(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void actBuscarCategoria(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.UTILITY);
            categSuperiorSelec = (IncidentesCategoriasDTO) AppContext.getInstance().get("CategoriaSup");
            if (categSuperiorSelec != null) {
                txtCategoriaSuperior.setText(categSuperiorSelec.getNombre());
            }
        }
    }

    public boolean validarActivos() {
        if (categoriaSelec.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

}
