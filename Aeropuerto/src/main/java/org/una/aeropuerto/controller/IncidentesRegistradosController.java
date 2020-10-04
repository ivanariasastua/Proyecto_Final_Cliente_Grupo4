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
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.dto.IncidentesRegistradosDTO;
import org.una.aeropuerto.service.IncidentesRegistradosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class IncidentesRegistradosController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtCategoriaSelec;
    @FXML
    private JFXTextField txtEmisorSelec;
    @FXML
    private JFXTextField txtResponsableSelec;
    @FXML
    private JFXTextField txtAreaSelec;
    @FXML
    private JFXTextArea txtDescripcionIncident;

    private IncidentesRegistradosService incidentService;
    private IncidentesRegistradosDTO incidentDTO;
    private AreasTrabajosDTO areaSelec = new AreasTrabajosDTO();
    private EmpleadosDTO emisorSelec = new EmpleadosDTO();
    private EmpleadosDTO responsableSelec = new EmpleadosDTO();
    private IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();
    @FXML
    private TableView tablaIncident;
    @FXML
    private JFXTextField txtBuscarIncident;
    @FXML
    private JFXComboBox<String> cbxFiltro;
    boolean incidentSelec;
    IncidentesRegistradosDTO incidentSeleccionado = new IncidentesRegistradosDTO();
    List<EmpleadosDTO> listEmpl = new ArrayList<>();
    List<AreasTrabajosDTO> listAreas = new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> item = FXCollections.observableArrayList("Responsable", "Emisor", "Categoria", "Area");
        cbxFiltro.setItems(item);
    }

    @Override
    public void initialize() {
        incidentService = new IncidentesRegistradosService();
        incidentDTO = new IncidentesRegistradosDTO();
        incidentSelec = false;
        clickTabla();
    }

    public void clickTabla() {
        tablaIncident.setRowFactory(tv -> {
            TableRow<IncidentesRegistradosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    incidentSelec = true;
                    incidentSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actBuscarCategoria(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.UTILITY);
        categoriaSelec = (IncidentesCategoriasDTO) AppContext.getInstance().get("CategoriaSup");
        if (categoriaSelec != null) {
            txtCategoriaSelec.setText(categoriaSelec.getNombre());
        }
    }

    @FXML
    private void actBuscarEmisor(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.UTILITY);
        emisorSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if (emisorSelec != null) {
            txtEmisorSelec.setText(emisorSelec.getNombre());
        }
    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.UTILITY);
        responsableSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if (responsableSelec != null) {
            txtResponsableSelec.setText(responsableSelec.getNombre());
        }
    }

    @FXML
    private void actBuscarArea(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarArea", false, StageStyle.UTILITY);
        areaSelec = (AreasTrabajosDTO) AppContext.getInstance().get("Area");
        if (areaSelec != null) {
            txtAreaSelec.setText(areaSelec.getNombre());
        }
    }

    public boolean validarCampos() {
        if (txtAreaSelec.getText() == null || txtCategoriaSelec.getText() == null || txtEmisorSelec.getText() == null || txtResponsableSelec.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos son obligatorios");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarIncidenteRegistrado(ActionEvent event) {
        if (validarCampos()) {
            incidentDTO = new IncidentesRegistradosDTO();
            incidentDTO.setAreaTrabajo(areaSelec);
            incidentDTO.setCategoria(categoriaSelec);
            incidentDTO.setDescripcion(txtDescripcionIncident.getText());
            incidentDTO.setEmisor(emisorSelec);
            incidentDTO.setResponsable(responsableSelec);
            Respuesta res = incidentService.guardarIncidenteRegistrado(incidentDTO);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Incidente guardado correctamente");
            }
        }
    }

    @FXML
    private void actLimpiarCamposIncidentes(ActionEvent event) {
        txtAreaSelec.setText(null);
        txtCategoriaSelec.setText(null);
        txtDescripcionIncident.setText(null);
        txtEmisorSelec.setText(null);
        txtResponsableSelec.setText(null);
        areaSelec = new AreasTrabajosDTO();
    }

    @FXML
    private void actInactivarIncidente(ActionEvent event) {
        if (incidentSelec == true) {
            if (Mensaje.showConfirmation("Inactivar incidente", null, "Seguro que desea inactivar el incidente?")) {
                incidentSeleccionado.setEstado(false);
                Respuesta res = incidentService.modificarIncidenteRegistrado(incidentSeleccionado.getId(), incidentSeleccionado);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Incidente inactivado correctamente");
                }
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar incidente", "Debe seleccionar un incidente");
        }
        incidentSelec = false;
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }
    
    public void llenarColumnas() {
        tablaIncident.getColumns().clear();
        TableColumn<IncidentesRegistradosDTO, String> colCatag = new TableColumn<>("Categoria");
        colCatag.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCategoria())));
        TableColumn<IncidentesRegistradosDTO, String> colEmisor = new TableColumn<>("Emisor");
        colEmisor.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getEmisor())));
        TableColumn<IncidentesRegistradosDTO, String> colRespons = new TableColumn<>("Responable");
        colRespons.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getResponsable())));
        TableColumn<IncidentesRegistradosDTO, String> colArea = new TableColumn<>("Area");
        colArea.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getAreaTrabajo())));
        TableColumn<IncidentesRegistradosDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<IncidentesRegistradosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaIncident.getColumns().addAll(colCatag, colEmisor,colRespons,colArea,colEstado,colDesc);
    }

    @FXML
    private void actBuscarIncidente(ActionEvent event) {

    }

}