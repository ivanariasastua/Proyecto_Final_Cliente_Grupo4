/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

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
    private ServiciosDTO servicioDTO;
    private ServiciosService servService;
    @FXML
    private TableView tablaServicios;

    private List<ServiciosDTO> listServic;
    ServiciosDTO servicSeleccionado = new ServiciosDTO();
    boolean servSelec;
    @FXML
    private JFXComboBox<String> cbxFiltroServicios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroServicios.setItems(filtro);
    }

    @Override
    public void initialize() {
        servService = new ServiciosService();
        servicioDTO = new ServiciosDTO();
        listServic = new ArrayList<>();
        clickTabla();
        servSelec = false;
    }

    public void clickTabla() {
        tablaServicios.setRowFactory(tv -> {
            TableRow<ServiciosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    servSelec = true;
                    servicSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }

    public void cargarColumnas() {
        tablaServicios.getColumns().clear();
        TableColumn<ServiciosDTO, String> colNomb = new TableColumn<>("Nombre");
        colNomb.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<ServiciosDTO, String> colDescrip = new TableColumn<>("Descripcion");
        colDescrip.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<ServiciosDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaServicios.getColumns().addAll(colNomb, colDescrip, colEst);
    }

    public boolean validarActivos() {
        if (servicSeleccionado.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }
    
    @FXML
    private void actGuardarServicio(ActionEvent event) {
        if (servSelec == true) {
            if(validarActivos()){
                servicSeleccionado.setId(servicSeleccionado.getId());
                servicSeleccionado.setDescripcion(txtDescripcionServicio.getText());
                servicSeleccionado.setNombre(txtNombreServicio.getText());
                Respuesta res = servService.modificarServicio(servicSeleccionado.getId(), servicSeleccionado);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Servicio editado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                }
            }
        } else {
            if (txtNombreServicio.getText() != null) {
                servicioDTO = new ServiciosDTO();
                servicioDTO.setDescripcion(txtDescripcionServicio.getText());
                servicioDTO.setNombre(txtNombreServicio.getText());
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

    @FXML
    private void acteditarServicio(ActionEvent event) {
        if (servSelec == true) {
            if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                txtDescripcionServicio.setText(servicSeleccionado.getDescripcion());
                txtNombreServicio.setText(servicSeleccionado.getNombre());
            } else {
                servSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        }
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
        cargarColumnas();
        tablaServicios.getItems().clear();
        if (cbxFiltroServicios.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
        } else {
            Respuesta res;
            if (cbxFiltroServicios.getValue().equals("Nombre")) {
                res = servService.getByNombre(txtBuscarServicio.getText());
            } else {
                if (txtBuscarServicio.getText().equals("activo") || txtBuscarServicio.getText().equals("Activo")) {
                    res = servService.getByEstado(true);
                } else if (txtBuscarServicio.getText().equals("inactivo") || txtBuscarServicio.getText().equals("Inactivo")) {
                    res = servService.getByEstado(false);
                } else {
                    res = servService.getByNombre("");
                }
            }
            if (res.getEstado()) {
                tablaServicios.getItems().addAll((List<ServiciosDTO>) res.getResultado("Servicios"));
            } else {
                Mensaje.show(Alert.AlertType.ERROR, "Buscar Servicios", res.getMensaje());
            }
        }
    }

    @FXML
    private void actLimpiarCamposServicio(ActionEvent event) {
        txtNombreServicio.setText(null);
        txtDescripcionServicio.setText(null);
        servSelec = false;
        servicioDTO = new ServiciosDTO();
        servicSeleccionado = new ServiciosDTO();
    }

    @FXML
    private void actInactivarServicio(ActionEvent event) {
        if (servSelec == true) {
            if (Mensaje.showConfirmation("Inactivar", null, "Seguro que desea inactivar la información?")) {
                if(validarActivos()){
                    servicSeleccionado.setEstado(false);
                    Respuesta res = servService.modificarServicio(servicSeleccionado.getId(), servicSeleccionado);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Se ha inactivado correctamente el servicio");
                        servSelec = false;
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            } else {
                servSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        }
    }

}
