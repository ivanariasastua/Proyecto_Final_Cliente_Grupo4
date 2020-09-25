/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class EmpleadosController extends Controller implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TableView tablaEmpleados;

    private EmpleadosService empleadoService;
    private List<EmpleadosDTO> listEmpleados;
    @FXML
    private JFXTextField txtBuscar;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXComboBox<EmpleadosDTO> cbxEmpleados;
    @FXML
    private JFXComboBox<RolesDTO> cbxRoles;
    @FXML
    private JFXTextField txtCedula;
    @FXML
    private JFXTextField txtContrasena;

    private List<EmpleadosDTO> listJefes = new ArrayList<>();
    private List<RolesDTO> listRoles = new ArrayList<>();
    private RolesService rolesService = new RolesService();
    private EmpleadosDTO empleadoDTO = new EmpleadosDTO();
    boolean empSeleccionado;
    private EmpleadosDTO emplSeleccionado = new EmpleadosDTO();
    @FXML
    private Tab tabEmpleados;
    @FXML
    private Tab tabCrear;
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        empSeleccionado = false;
        listEmpleados = new ArrayList<>();
        empleadoService = new EmpleadosService();
        cargarTablaEmpleados();
        llenarComboBoxs();
        clickTabla();
    }

    @Override
    public void initialize() {
    }

    public void llenarComboBoxs() {
        Respuesta res = empleadoService.getAll();
        listJefes = (List<EmpleadosDTO>) res.getResultado("Empleados");
        if (listJefes != null) {
            ObservableList items = FXCollections.observableArrayList(listJefes);
            cbxEmpleados.setItems(items);
        }
        Respuesta resp = rolesService.getAll();
        listRoles = (List<RolesDTO>) resp.getResultado("Roles");
        if (listRoles != null) {
            ObservableList items = FXCollections.observableArrayList(listRoles);
            cbxRoles.setItems(items);
        }
    }

    public void clickTabla() {
        tablaEmpleados.setRowFactory(tv -> {
            TableRow<EmpleadosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    empSeleccionado = true;
                    emplSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    public void cargarTablaEmpleados() {
        tablaEmpleados.getColumns().clear();
        TableColumn<EmpleadosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<EmpleadosDTO, String> colCedula = new TableColumn<>("Cedula");
        colCedula.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCedula())));
        TableColumn<EmpleadosDTO, String> colJefe = new TableColumn<>("Jefe");
        colJefe.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getJefe())));
        TableColumn<EmpleadosDTO, String> colrol = new TableColumn<>("Rol");
        colrol.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getRol())));
        tablaEmpleados.getColumns().addAll(colNombre, colCedula, colJefe, colrol);
        Respuesta res = empleadoService.getAll();
        listEmpleados = (List<EmpleadosDTO>) res.getResultado("Empleados");

        if (listEmpleados != null) {
            ObservableList items = FXCollections.observableArrayList(listEmpleados);
            tablaEmpleados.setItems(items);
        } else {
            tablaEmpleados.getItems().clear();
        }
    }

    @FXML
    private void actBuscar(ActionEvent event) {  //no esta funcionando el filtro
//        if (txtBuscar.getText() != null && !txtBuscar.getText().isEmpty()) {
//            tablaEmpleados.getColumns().clear();
//            TableColumn<EmpleadosDTO, String> colNombre = new TableColumn<>("Nombre");
//            colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
//            TableColumn<EmpleadosDTO, String> colCedula = new TableColumn<>("Cedula");
//            colCedula.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCedula())));
//            TableColumn<EmpleadosDTO, String> colJefe = new TableColumn<>("Jefe");
//            colJefe.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getJefe())));
//            TableColumn<EmpleadosDTO, String> colrol = new TableColumn<>("Rol");
//            colrol.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getRol())));
//            tablaEmpleados.getColumns().addAll(colNombre, colCedula, colJefe, colrol);
//            Respuesta res = empleadoService.getFiltro(txtBuscar.getText(),txtBuscar.getText(),true,txtBuscar.getText());
//            listEmpleados = (List<EmpleadosDTO>) res.getResultado("Empleados");
//
//            if (listEmpleados != null) {
//                ObservableList items = FXCollections.observableArrayList(listEmpleados);
//                tablaEmpleados.setItems(items);
//            } else {
//                tablaEmpleados.getItems().clear();
//            }
//        }
    }

    @FXML
    private void actInactivar(ActionEvent event) {
    }

    public boolean validarCampos() {
        if (txtCedula.getText() == null || txtNombre.getText() == null || txtContrasena.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos Nombre, Cedula y Contraseña son obligatorios");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (empSeleccionado == true) {
            System.out.println("editar");
            if (validarCampos()) {
                empleadoDTO.setId(emplSeleccionado.getId());
                empleadoDTO.setCedula(txtCedula.getText());
                empleadoDTO.setContrasenaEncriptada(txtContrasena.getText());
                empleadoDTO.setJefe(cbxEmpleados.getValue());
                empleadoDTO.setNombre(txtNombre.getText());
                empleadoDTO.setRol(cbxRoles.getValue());

                Respuesta res = empleadoService.guardarEmpleado(empleadoDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Empleado editado correctamente");
                    empSeleccionado = false;
                    emplSeleccionado = null;
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error al editar ", res.getMensaje());
                }
            }
        } else {
            System.out.println("guardar");
            if (validarCampos()) {
                empleadoDTO.setCedula(txtCedula.getText());
                empleadoDTO.setContrasenaEncriptada(txtContrasena.getText());
                empleadoDTO.setJefe(cbxEmpleados.getValue());
                empleadoDTO.setNombre(txtNombre.getText());
                empleadoDTO.setRol(cbxRoles.getValue());

                Respuesta res = empleadoService.guardarEmpleado(empleadoDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Empleado guardado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error al guardar ", res.getMensaje());
                }
            }
        }

    }

    void limpiarCampos() {
        txtCedula.setText(null);
        txtContrasena.setText(null);
        txtNombre.setText(null);
        cbxEmpleados.setValue(null);
        cbxRoles.setValue(null);
    }

    @FXML
    private void actTabPane(MouseEvent event) {
        if (tabEmpleados.isSelected()) {
            cargarTablaEmpleados();
        } else if (tabCrear.isSelected() && empSeleccionado == false) {
            limpiarCampos();
        }
    }

    public void cargarDatos() {
        txtCedula.setText(emplSeleccionado.getCedula());
        // txtContrasena.setText(emplSeleccionado.);
        txtNombre.setText(emplSeleccionado.getNombre());
        cbxEmpleados.setValue(emplSeleccionado.getJefe());
        cbxRoles.setValue(emplSeleccionado.getRol());
    }

    @FXML
    private void actIrAModificar(ActionEvent event) {
        if (empSeleccionado == true) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tabCrear);
            cargarDatos();
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar empleado", "Debe seleccionar un empleado");
        }
    }
}
