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
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.dto.EmpleadosAreasTrabajosDTO;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
import org.una.aeropuerto.service.EmpleadosAreasTrabajosService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class AreasTrabajosController extends Controller implements Initializable {

    @FXML
    private Tab tabAreas;
    @FXML
    private TableView tablaAreasTrabajo;
    @FXML
    private JFXTextField txtNombreArea;
    @FXML
    private JFXTextArea txtDescripcionArea;
    @FXML
    private Tab tabAsignarAreas;
    @FXML
    private JFXComboBox<AreasTrabajosDTO> cbxAreaTrabajo;
    @FXML
    private JFXComboBox<EmpleadosDTO> cbxEmpleado;
    @FXML
    private TableView tablaAsignarAreas;

    private AreasTrabajosService areasService = new AreasTrabajosService();
    private List<AreasTrabajosDTO> listAreasT;
    private AreasTrabajosDTO areaDto = new AreasTrabajosDTO();
    private AreasTrabajosDTO areaSeleccionada = new AreasTrabajosDTO();
    /**
     * Initializes the controller class.
     */
    boolean areaSelec = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        listAreasT = new ArrayList<>();
        cargarTablaAreas();
        clickTablas();
    }

    public void llenarComboboxs() {

    }

    @FXML
    private void actBuscarAreasTrabajos(ActionEvent event) {
    }

    public void clickTablas() {
        tablaAreasTrabajo.setRowFactory(tv -> {
            TableRow<AreasTrabajosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    areaSelec = true;
                    areaSeleccionada = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actEditarAreasT(ActionEvent event) {
        if (areaSelec == true) {
            txtDescripcionArea.setText(areaSeleccionada.getDescripcion());
            txtNombreArea.setText(areaSeleccionada.getNombre());
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Area", "Debe seleccionar el area de trabajo");
        }
    }

    @FXML
    private void actGuardarAreasTrabajo(ActionEvent event) {
        if (areaSelec == true) {
            areaDto.setId(areaSeleccionada.getId());
            areaDto.setDescripcion(txtDescripcionArea.getText());
            areaDto.setNombre(txtNombreArea.getText());
            Respuesta res = areasService.modificarAreaTrabajo(areaSeleccionada.getId(), areaDto);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Area de trabajo editada correctamente");
                cargarTablaAreas();
            }
            areaSelec = false;
        } else {
            if (txtNombreArea.getText() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Noombre es obligatorio");
            } else {
                areaDto = new AreasTrabajosDTO();
                areaDto.setDescripcion(txtDescripcionArea.getText());
                areaDto.setNombre(txtNombreArea.getText());
                Respuesta res = areasService.guardarAreaTrabajo(areaDto);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Area de trabajo guardada correctamente");
                    cargarTablaAreas();
                }
            }
        }
    }

    @FXML
    private void actLimpiarCamposAreasTrabajo(ActionEvent event) {
        txtDescripcionArea.setText(null);
        txtNombreArea.setText(null);
        areaSelec = false;
        areaDto = new AreasTrabajosDTO();
    }

    public void cargarTablaAreas() {
        tablaAreasTrabajo.getColumns().clear();
        TableColumn<AreasTrabajosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<AreasTrabajosDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        tablaAreasTrabajo.getColumns().addAll(colNombre, colDesc);
        Respuesta res = areasService.getAll();
        listAreasT = (List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos");
        if (listAreasT != null) {
            ObservableList items = FXCollections.observableArrayList(listAreasT);
            tablaAreasTrabajo.setItems(items);
        } else {
            tablaAreasTrabajo.getItems().clear();
        }
    }

    //tab de asignar areas de trabajo
    @FXML
    private void actGuardarAsignacionArea(ActionEvent event) {
    }

    @FXML
    private void actLimpiarCamposAsignarArea(ActionEvent event) {
    }

    @Override
    public void initialize() {
    }

}
