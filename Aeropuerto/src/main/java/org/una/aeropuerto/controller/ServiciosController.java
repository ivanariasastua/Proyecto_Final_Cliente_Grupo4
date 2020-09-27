/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void initialize() {
        servService = new ServiciosService();
        servicioDTO = new ServiciosDTO();
        listServic = new ArrayList<>();
        cargarTablaServicios();
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

    public void cargarTablaServicios() {
        tablaServicios.getColumns().clear();
        TableColumn<ServiciosDTO, String> colNomb = new TableColumn<>("Nombre");
        colNomb.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<ServiciosDTO, String> colDescrip = new TableColumn<>("Descripcion");
        colDescrip.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        tablaServicios.getColumns().addAll(colNomb, colDescrip);
        Respuesta res = servService.getAll();
        System.out.println(res.getMensaje() + res.getMensajeInterno());
        if (res.getEstado()) {
            listServic = (List<ServiciosDTO>) res.getResultado("Servicios");
            if (listServic != null) {
                ObservableList items = FXCollections.observableArrayList(listServic);
                tablaServicios.setItems(items);
            } else {
                tablaServicios.getItems().clear();
            }
        }
    }

    @FXML
    private void actGuardarServicio(ActionEvent event) {
        if (servSelec == true) {
            servicioDTO.setId(servicSeleccionado.getId());
            servicioDTO.setDescripcion(txtDescripcionServicio.getText());
            servicioDTO.setNombre(txtNombreServicio.getText());
            Respuesta res = servService.modificarServicio(servicSeleccionado.getId(), servicioDTO);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Servicio editado correctamente");
                cargarTablaServicios();
            }
        } else {
            if (txtNombreServicio.getText() != null) {
                servicioDTO = new ServiciosDTO();
                servicioDTO.setDescripcion(txtDescripcionServicio.getText());
                servicioDTO.setNombre(txtNombreServicio.getText());
                Respuesta res = servService.guardarServicio(servicioDTO);
                System.out.println(res.getMensaje());
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Servicio guardado correctamente");
                    cargarTablaServicios();
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Nombre es obligatorio");
            }
        }
    }

    @FXML
    private void acteditarServicio(ActionEvent event) {
        if (servSelec == true) {
            txtDescripcionServicio.setText(servicSeleccionado.getDescripcion());
            txtNombreServicio.setText(servicSeleccionado.getNombre());
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        }
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
    }

    @FXML
    private void actLimpiarCamposServicio(ActionEvent event) {
        txtNombreServicio.setText(null);
        txtDescripcionServicio.setText(null);
        servSelec = false;
        servicioDTO = new ServiciosDTO();
    }

}
