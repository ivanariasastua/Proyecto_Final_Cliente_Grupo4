/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTimePicker;
import java.net.URL;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.EmpleadosHorariosDTO;
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.EmpleadosHorariosService;
import org.una.aeropuerto.service.EmpleadosMarcajesService;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.util.AppContext;
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
    private EmpleadosService empleadoService = new EmpleadosService();
    private List<EmpleadosDTO> listEmpleados;
    private List<EmpleadosHorariosDTO> listHorariosEmp;
    private List<EmpleadosMarcajesDTO> listMarcajes = new ArrayList<>();
    private List<EmpleadosDTO> listJefes = new ArrayList<>();
    private List<EmpleadosDTO> listEmp = new ArrayList<>();
    private List<RolesDTO> listRoles = new ArrayList<>();
    private RolesService rolesService = new RolesService();
    private EmpleadosDTO empleadoDTO = new EmpleadosDTO();
    boolean empSeleccionado;
    boolean horarSeleccionado;
    private EmpleadosDTO emplSeleccionado = new EmpleadosDTO();
    private EmpleadosHorariosDTO horarioSeleccionado = new EmpleadosHorariosDTO();
    private EmpleadosHorariosDTO horarioDTO = new EmpleadosHorariosDTO();
    private EmpleadosHorariosService horarioService;
    private EmpleadosMarcajesService marcajesService = new EmpleadosMarcajesService();
    private EmpleadosMarcajesDTO marcajeDTO = new EmpleadosMarcajesDTO();
    @FXML
    private TableView tablaHorarios;
    @FXML
    private BorderPane bpPantalla;
    @FXML
    private VBox vbContenedor;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabEmpleados;
    @FXML
    private TableView tablaEmpleados;
    @FXML
    private JFXTextField txtBuscarEmpleados;
    @FXML
    private Tab tabCrear;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXComboBox<EmpleadosDTO> cbxJefes;
    @FXML
    private JFXComboBox<RolesDTO> cbxRoles;
    @FXML
    private JFXTextField txtCedula;
    @FXML
    private JFXTextField txtContrasena;
    @FXML
    private JFXComboBox<EmpleadosDTO> cbxEmpleados;
    @FXML
    private Label lblTitulo;
    @FXML
    private Tab tabHorarios;
    @FXML
    private Tab tabMarcajes;
    @FXML
    private JFXComboBox<String> entradaHoras;
    @FXML
    private JFXComboBox<String> entradaMinutos;
    @FXML
    private JFXComboBox<String> salidaHoras;
    @FXML
    private JFXComboBox<String> salidaMinutos;
    @FXML
    private JFXComboBox<String> cbxDiaEntrada;
    @FXML
    private JFXComboBox<String> cbxDiaSalida;
    @FXML
    private TableView tablaMarcajes;
    @FXML
    private JFXTextField txtBuscarHorarios;
    @FXML
    private JFXTextField txtBuscarMarcajes;
    @FXML
    private JFXComboBox<EmpleadosHorariosDTO> cbxHorarios;
    @FXML
    private JFXComboBox<String> entradaHoras2;
    @FXML
    private JFXComboBox<String> entradaMinutos2;
    @FXML
    private JFXComboBox<String> salidaHoras2;
    @FXML
    private JFXComboBox<String> salidaMinutos2;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        empSeleccionado = false;
        horarSeleccionado = false;
        listEmpleados = new ArrayList<>();
        listHorariosEmp = new ArrayList<>();
        horarioService = new EmpleadosHorariosService();
        empleadoService = new EmpleadosService();
        llenarRelojs();
        ObservableList items = FXCollections.observableArrayList("Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado", "Domingo");
        cbxDiaEntrada.setItems(items);
        cbxDiaSalida.setItems(items);
        cargarTablaHorarios();
        llenarComboBoxs();
    }

    @Override
    public void initialize() {
        cargarTablaEmpleados();
        llenarComboBoxs();
        clickTablas();
    }

    //tab de empleados
    public void llenarComboBoxs() {
        cbxJefes.getItems().clear();
        cbxEmpleados.getItems().clear();
        cbxRoles.getItems().clear();
        Respuesta res = empleadoService.getAll();
        listJefes = (List<EmpleadosDTO>) res.getResultado("Empleados");
        if (listJefes != null) {
            ObservableList items = FXCollections.observableArrayList(listJefes);
            cbxJefes.setItems(items);
            cbxEmpleados.setItems(items);
        }
        Respuesta resp = rolesService.getAll();
        listRoles = (List<RolesDTO>) resp.getResultado("Roles");
        if (listRoles != null) {
            ObservableList items = FXCollections.observableArrayList(listRoles);
            cbxRoles.setItems(items);
        }
    }

    public void clickTablas() {
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

        tablaHorarios.setRowFactory(tv -> {
            TableRow<EmpleadosHorariosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    horarSeleccionado = true;
                    horarioSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actLimpiarCamposEmplead(ActionEvent event) {
        limpiarCampos();
    }

    public void cargarTablaEmpleados() {
        tablaEmpleados.getColumns().clear();
        TableColumn<EmpleadosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<EmpleadosDTO, String> colCedula = new TableColumn<>("Cedula");
        colCedula.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCedula()));
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

    public boolean validarCampos() {
        if (txtCedula.getText() == null || txtNombre.getText() == null || txtContrasena.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos Nombre, Cedula y Contraseña son obligatorios");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarEmpleado(ActionEvent event) {
        if (empSeleccionado == true) {  //editar
            if (validarCampos()) {
                empleadoDTO.setId(emplSeleccionado.getId());
                empleadoDTO.setCedula(txtCedula.getText());
                empleadoDTO.setContrasenaEncriptada(txtContrasena.getText());
                empleadoDTO.setJefe(cbxJefes.getValue());
                empleadoDTO.setNombre(txtNombre.getText());
                empleadoDTO.setRol(cbxRoles.getValue());

                Respuesta res = empleadoService.modificarEmpleado(emplSeleccionado.getId(), empleadoDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Empleado editado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error al editar ", res.getMensaje());
                }
            }
        } else {  //guardar nuevo
            if (validarCampos()) {
                empleadoDTO = new EmpleadosDTO();
                empleadoDTO.setCedula(txtCedula.getText());
                empleadoDTO.setContrasenaEncriptada(txtContrasena.getText());
                empleadoDTO.setJefe(cbxJefes.getValue());
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

    @FXML
    private void actBuscarEmpleados(ActionEvent event) {

    }

    void limpiarCampos() {
        txtNombre.setText(null);
        txtCedula.setText(null);
        txtContrasena.setText(null);
        cbxJefes.setValue(null);
        cbxRoles.setValue(null);
        empSeleccionado = false;
        emplSeleccionado = new EmpleadosDTO();
    }

    @FXML
    private void actTabPane(MouseEvent event) {
        if (tabEmpleados.isSelected()) {
            cargarTablaEmpleados();
        } else if (tabCrear.isSelected() && empSeleccionado == false) {
            limpiarCampos();
        } else if (tabHorarios.isSelected()) {
            llenarComboBoxs();
        } else if (tabMarcajes.isSelected()) {
            cbxHorarios.getItems().clear();
            Respuesta resp = horarioService.getAll();
            listHorariosEmp = (List<EmpleadosHorariosDTO>) resp.getResultado("Empleados_Horarios");
            if (listHorariosEmp != null) {
                ObservableList items = FXCollections.observableArrayList(listHorariosEmp);
                cbxHorarios.setItems(items);
            }
        }
    }

    @FXML
    private void actIrAModificarEmpleados(ActionEvent event) {
        if (empSeleccionado == true) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tabCrear);
            cargarDatos();
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar empleado", "Debe seleccionar un empleado");
        }
    }

    public void cargarDatos() {
        txtCedula.setText(emplSeleccionado.getCedula());
        // txtContrasena.setText(emplSeleccionado.);
        txtNombre.setText(emplSeleccionado.getNombre());
        cbxJefes.setValue(emplSeleccionado.getJefe());
        cbxRoles.setValue(emplSeleccionado.getRol());
    }

    //tab de horarios
    public void cargarTablaHorarios() {
        tablaHorarios.getColumns().clear();
        TableColumn<EmpleadosHorariosDTO, String> colNombre = new TableColumn<>("Empleado");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getEmpleado())));
        TableColumn<EmpleadosHorariosDTO, String> colDiaE = new TableColumn<>("Dia de entrada");
        colDiaE.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaEntrada()));
        TableColumn<EmpleadosHorariosDTO, String> colDiaS = new TableColumn<>("Dia de salida");
        colDiaS.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaSalida()));
        TableColumn<EmpleadosHorariosDTO, String> colHoraE = new TableColumn<>("Hora de entrada");
        colHoraE.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getHoraEntrada())));
        TableColumn<EmpleadosHorariosDTO, String> colHoraS = new TableColumn<>("hora de salida");
        colHoraS.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getHoraSalida())));
        tablaHorarios.getColumns().addAll(colNombre, colDiaE, colDiaS/*, colHoraE, colHoraS*/);
        Respuesta res = horarioService.getAll();
        listHorariosEmp = (List<EmpleadosHorariosDTO>) res.getResultado("Empleados_Horarios");
        if (listHorariosEmp != null) {
            ObservableList items = FXCollections.observableArrayList(listHorariosEmp);
            tablaHorarios.setItems(items);
        } else {
            tablaHorarios.getItems().clear();
        }
    }

    public void llenarRelojs() {
        List<String> minutos = new ArrayList<>();
        List<String> horas = new ArrayList<>();
        for (int i = 0; i <= 59; i++) {
            if (i != 0 && i <= 24) {
                if (i <= 9) {
                    horas.add("0" + String.valueOf(i));
                } else {
                    horas.add(String.valueOf(i));
                }
            }
            if (i <= 9) {
                minutos.add("0" + String.valueOf(i));
            } else {
                minutos.add(String.valueOf(i));
            }
        }
        ObservableList items = FXCollections.observableArrayList(minutos);
        ObservableList items2 = FXCollections.observableArrayList(horas);
        entradaMinutos.setItems(items);
        salidaMinutos.setItems(items);
        entradaHoras.setItems(items2);
        salidaHoras.setItems(items2);
        entradaMinutos2.setItems(items);
        salidaMinutos2.setItems(items);
        entradaHoras2.setItems(items2);
        salidaHoras2.setItems(items2);
    }

    public boolean validarCamposHorario() {
        if (cbxDiaEntrada.getValue() == null || cbxEmpleados.getValue() == null || entradaHoras.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los siguientes campos son obligatorios\nEmpleado\nDia de entrada\nHora de entrada");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarHorario(ActionEvent event) {
        if (horarSeleccionado == true) {
            if (validarCamposHorario()) {
                horarioDTO.setId(horarioSeleccionado.getId());
                horarioDTO.setDiaEntrada(cbxDiaEntrada.getValue());
                horarioDTO.setDiaSalida(cbxDiaSalida.getValue());
                horarioDTO.setEmpleado(cbxEmpleados.getValue());
                //  horarioDTO.setHoraEntrada();
                Respuesta res = horarioService.modificarEmpleadoHorario(horarioSeleccionado.getId(), horarioDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Horario editado correctamente");
                    cargarTablaHorarios();
                }
            }
        } else {
            if (validarCamposHorario()) {
                horarioDTO = new EmpleadosHorariosDTO();
                horarioDTO.setDiaEntrada(cbxDiaEntrada.getValue());
                horarioDTO.setDiaSalida(cbxDiaSalida.getValue());
                horarioDTO.setEmpleado(cbxEmpleados.getValue());
                //  horarioDTO.setHoraEntrada();
                Respuesta res = horarioService.guardarEmpleadoHorario(horarioDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Horario guardado correctamente");
                    cargarTablaHorarios();
                }
            }
        }
    }

    @FXML
    private void actLimpiarCamposHorario(ActionEvent event) {
        cbxDiaEntrada.setValue(null);
        cbxDiaSalida.setValue(null);
        entradaHoras.setValue(null);
        entradaMinutos.setValue(null);
        salidaHoras.setValue(null);
        salidaMinutos.setValue(null);
        cbxEmpleados.setValue(null);
        horarSeleccionado = false;
        horarioSeleccionado = new EmpleadosHorariosDTO();
    }

    @FXML
    private void actBuscarHorarios(ActionEvent event) {
    }

    @FXML
    private void actEditarHorarios(ActionEvent event) {
        if (horarSeleccionado == true) {
            cbxDiaEntrada.setValue(horarioSeleccionado.getDiaEntrada());
            cbxDiaSalida.setValue(horarioSeleccionado.getDiaSalida());
            cbxEmpleados.setValue(horarioSeleccionado.getEmpleado());
            //  entradaHoras.setValue();
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Horario", "Debe seleccionar un horario a editar");
        }
    }

    //tab marcajes


    @FXML
    private void actGuardarMarcajes(ActionEvent event) {

    }

    @FXML
    private void actBuscarMarcajes(ActionEvent event) {
    }

    @FXML
    private void actEditarMarcajes(ActionEvent event) {
    }

    @FXML
    private void actLimpiarCamposMarcajes(ActionEvent event) {
    }

}
