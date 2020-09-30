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
import org.una.aeropuerto.service.EmpleadosService;
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
    private List<AreasTrabajosDTO> listAreasT = new ArrayList<>();
    private List<EmpleadosAreasTrabajosDTO> listEmpAreaT = new ArrayList<>();
    private AreasTrabajosDTO areaDto = new AreasTrabajosDTO();
    private AreasTrabajosDTO areaSeleccionada = new AreasTrabajosDTO();
    private EmpleadosAreasTrabajosDTO empAreaSeleccionado = new EmpleadosAreasTrabajosDTO();
    private EmpleadosAreasTrabajosDTO empTrabDTO = new EmpleadosAreasTrabajosDTO();
    private EmpleadosAreasTrabajosService empTrabService = new EmpleadosAreasTrabajosService();
    private List<EmpleadosDTO> listEmp = new ArrayList<>();
    private EmpleadosService empleadoService = new EmpleadosService();
    boolean areaSelec = false;
    boolean empAreaSelec = false;
    @FXML
    private JFXTextField txtBuscarAreasT;
    @FXML
    private JFXComboBox<String> cbxFiltroAreas;
    @FXML
    private JFXComboBox<String> cbxFiltroAsignarA;
    @FXML
    private JFXTextField txtBuscarEmpleadAreaT;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroAreas.setItems(filtro);
        ObservableList filtro2 = FXCollections.observableArrayList("Empleado", "Area de trabajo");
        cbxFiltroAsignarA.setItems(filtro2);
    }

    @Override
    public void initialize() {
        listAreasT = new ArrayList<>();
        areaSelec = false;
        clickTablas();
        llenarComboboxs();
        llenarColumnas();
    }

    public void llenarComboboxs() {
        Respuesta res = empleadoService.getAll();
        listEmp = (List<EmpleadosDTO>) res.getResultado("Empleados");
        if (listEmp != null) {
            ObservableList items = FXCollections.observableArrayList(listEmp);
            cbxEmpleado.setItems(items);
        }
        Respuesta resp = areasService.getAll();
        listAreasT = (List<AreasTrabajosDTO>) resp.getResultado("Areas_Trabajos");
        if (listAreasT != null) {
            ObservableList items = FXCollections.observableArrayList(listAreasT);
            cbxAreaTrabajo.setItems(items);
        }
    }

    public void llenarColumnas() {
        tablaAreasTrabajo.getColumns().clear();
        TableColumn<AreasTrabajosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<AreasTrabajosDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<AreasTrabajosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaAreasTrabajo.getColumns().addAll(colNombre, colDesc, colEstado);
    }

    @FXML
    private void actBuscarAreasTrabajos(ActionEvent event) {
        llenarColumnas();
        if (cbxFiltroAreas.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
        } else {
            if (cbxFiltroAreas.getValue().equals("Nombre")) {
                Respuesta res = areasService.getByNombre(txtBuscarAreasT.getText());
                listAreasT = (List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos");
                if (listAreasT != null) {
                    ObservableList items = FXCollections.observableArrayList(listAreasT);
                    tablaAreasTrabajo.setItems(items);
                } else {
                    tablaAreasTrabajo.getItems().clear();
                }
            } else {
                if (txtBuscarAreasT.getText().equals("activo") || txtBuscarAreasT.getText().equals("Activo")) {
                    Respuesta res = areasService.getByEstado(true);
                    listAreasT = (List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos");
                    if (listAreasT != null) {
                        ObservableList items = FXCollections.observableArrayList(listAreasT);
                        tablaAreasTrabajo.setItems(items);
                    } else {
                        tablaAreasTrabajo.getItems().clear();
                    }
                } else if (txtBuscarAreasT.getText().equals("inactivo") || txtBuscarAreasT.getText().equals("Inactivo")) {
                    Respuesta res = areasService.getByEstado(false);
                    listAreasT = (List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos");
                    if (listAreasT != null) {
                        ObservableList items = FXCollections.observableArrayList(listAreasT);
                        tablaAreasTrabajo.setItems(items);
                    } else {
                        tablaAreasTrabajo.getItems().clear();
                    }
                }else{
                    tablaAreasTrabajo.getItems().clear();
                }
            }
        }
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
        tablaAsignarAreas.setRowFactory(tv -> {
            TableRow<EmpleadosAreasTrabajosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    empAreaSelec = true;
                    empAreaSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actEditarAreasT(ActionEvent event) {
        if (areaSelec == true) {
            if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                txtDescripcionArea.setText(areaSeleccionada.getDescripcion());
                txtNombreArea.setText(areaSeleccionada.getNombre());
            } else {
                areaSelec = false;
            }
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
                llenarComboboxs();
            }
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
                    llenarComboboxs();
                }
            }
        }
    }

    @FXML
    private void actLimpiarCamposAreasTrabajo(ActionEvent event) {
        txtDescripcionArea.setText(null);
        txtNombreArea.setText(null);
        areaSelec = false;
        areaSeleccionada = new AreasTrabajosDTO();
        areaDto = new AreasTrabajosDTO();
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }

    @FXML
    private void actInactivarAreaT(ActionEvent event) {
        if (areaSelec == true) {
            if (Mensaje.showConfirmation("Inactivar ", null, "Seguro que desea inactivar la información?")) {
                areaSeleccionada.setEstado(false);
                Respuesta res = areasService.modificarAreaTrabajo(areaSeleccionada.getId(), areaSeleccionada);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Area de trabajo inactivada correctamente");
                    areaSelec = false;
                }
            } else {
                areaSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Area", "Debe seleccionar una area de trabajo");
        }
    }

    //tab de asignar areas de trabajo
    public boolean validarCamposEmpleadosAreasT() {
        if (cbxAreaTrabajo.getValue() == null || cbxEmpleado.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos son obligatorios");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarAsignacionArea(ActionEvent event) {
        if (empAreaSelec == true) {
            empTrabDTO.setId(empAreaSeleccionado.getId());
            empTrabDTO.setAreaTrabajo(cbxAreaTrabajo.getValue());
            empTrabDTO.setEmpleado(cbxEmpleado.getValue());
            Respuesta res = empTrabService.modificarEmpleadoAreaTrabajo(empAreaSeleccionado.getId(), empTrabDTO);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Editado ", "Asignacion de area de trabajo editada correctamente");
            }
        } else {
            if (validarCamposEmpleadosAreasT()) {
                empTrabDTO = new EmpleadosAreasTrabajosDTO();
                empTrabDTO.setAreaTrabajo(cbxAreaTrabajo.getValue());
                empTrabDTO.setEmpleado(cbxEmpleado.getValue());
                Respuesta res = empTrabService.guardarEmpleadoAreaTrabajo(empTrabDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado ", "Asignacion de area de trabajo guardada correctamente");
                }
            }
        }
    }

    @FXML
    private void actLimpiarCamposAsignarArea(ActionEvent event) {
        cbxAreaTrabajo.setValue(null);
        cbxEmpleado.setValue(null);
        empAreaSelec = false;
        empAreaSeleccionado = new EmpleadosAreasTrabajosDTO();
    }

    @FXML
    private void actEditarEmplAreasTrab(ActionEvent event) {
        if (empAreaSelec == true) {
            if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                cbxAreaTrabajo.setValue(empAreaSeleccionado.getAreaTrabajo());
                cbxEmpleado.setValue(empAreaSeleccionado.getEmpleado());
            } else {
                empAreaSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Dato", "Debe seleccionar un dato de la tabla");
        }
    }

    @FXML
    private void actInactivarEmplAreasTrab(ActionEvent event) {
        if (empAreaSelec == true) {
            if (Mensaje.showConfirmation("Inactivar ", null, "Seguro que desea inactivar la información?")) {
                empAreaSeleccionado.setEstado(false);
                Respuesta res = empTrabService.modificarEmpleadoAreaTrabajo(empAreaSeleccionado.getId(), empAreaSeleccionado);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Información inactivada correctamente");
                    empAreaSelec = false;
                }
            } else {
                empAreaSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar información", "Debe seleccionar información de la tabla");
        }
    }

    @FXML
    private void actBuscarEmpleadAreasT(ActionEvent event) {
    }

}
