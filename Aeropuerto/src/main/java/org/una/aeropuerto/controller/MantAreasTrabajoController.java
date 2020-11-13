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
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
import org.una.aeropuerto.util.Formato;
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
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXTextField txtEstado;

    private AreasTrabajosDTO areaDto = new AreasTrabajosDTO();
    private AreasTrabajosDTO areaSelec = new AreasTrabajosDTO();
    private final AreasTrabajosService areasService = new AreasTrabajosService();
    private Map<String,String> modoDesarrollo;
    boolean areaSelect = false;
    @FXML
    private Label lblDesarollo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
        txtDescripcion.setTextFormatter(Formato.getInstance().maxLengthFormat(100));
        txtNombre.setTextFormatter(Formato.getInstance().maxLengthFormat(35));
    }

    @Override
    public void cargarTema() {
    }

    @Override
    public void initialize() {
        limpiarAreas();
        lblDesarollo.setText("");
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lblDesarollo.setText(modoDesarrollo.get("Vista")+"\n"+modoDesarrollo.get("Guardar"));
        } else {
            if (areaSelect == true) {
                if (validarActivos()) {
                    areaSelec.setId(areaSelec.getId());
                    areaSelec.setDescripcion(txtDescripcion.getText());
                    areaSelec.setNombre(txtNombre.getText());
                    Respuesta res = areasService.modificarAreaTrabajo(areaSelec.getId(), areaSelec);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Área de trabajo editada correctamente");
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
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Área de trabajo guardada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista MantAreasTrabajo");
        modoDesarrollo.put("Limpiar", "Limpiar responde al método actLimpiar");
        modoDesarrollo.put("Guardar", "Guardar responde al método actGuardar");
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
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR"))
            lblDesarollo.setText(modoDesarrollo.get("Vista")+"\n"+modoDesarrollo.get("Guardar"));
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
