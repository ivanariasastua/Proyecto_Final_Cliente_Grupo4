/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.EmpleadosHorariosDTO;
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;
import org.una.aeropuerto.dto.EmpleadosAreasTrabajosDTO;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.EmpleadosHorariosService;
import org.una.aeropuerto.service.EmpleadosMarcajesService;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.service.EmpleadosAreasTrabajosService;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.dto.AreasTrabajosDTO;

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
    private EmpleadosAreasTrabajosService empAreasService = new EmpleadosAreasTrabajosService();
    private List<EmpleadosDTO> listEmpleados;
    private List<EmpleadosHorariosDTO> listHorariosEmp;
    private List<EmpleadosMarcajesDTO> listMarcajes = new ArrayList<>();
    private List<EmpleadosDTO> listJefes = new ArrayList<>();
    private List<EmpleadosDTO> listEmp = new ArrayList<>();
    private List<RolesDTO> listRoles = new ArrayList<>();
    private RolesService rolesService = new RolesService();
    private EmpleadosDTO empleadoDTO = new EmpleadosDTO(), jefeSelect;
    boolean empSeleccionado;
    boolean horarSeleccionado;
    private EmpleadosDTO emplSeleccionado = new EmpleadosDTO();
    private EmpleadosHorariosDTO horarioSeleccionado = new EmpleadosHorariosDTO();
    private EmpleadosHorariosDTO horarioDTO = new EmpleadosHorariosDTO();
    private EmpleadosHorariosService horarioService;
    private EmpleadosMarcajesService marcajesService = new EmpleadosMarcajesService();
    private EmpleadosMarcajesDTO marcajeDTO = new EmpleadosMarcajesDTO();
    private AreasTrabajosDTO area = null;
    @FXML private TableView tablaHorarios;
    @FXML private BorderPane bpPantalla;
    @FXML private VBox vbContenedor;
    @FXML private TabPane tabPane;
    @FXML private Tab tabCrear;
    @FXML private JFXTextField txtNombre;
    @FXML private JFXComboBox<RolesDTO> cbxRoles;
    @FXML private JFXTextField txtCedula;
    @FXML private JFXTextField txtContrasena;
    @FXML private Label lblTitulo;
    @FXML private Tab tabHorarios;
    @FXML private Tab tabMarcajes;
    @FXML private JFXComboBox<String> entradaHoras;
    @FXML private JFXComboBox<String> entradaMinutos;
    @FXML private JFXComboBox<String> salidaHoras;
    @FXML private JFXComboBox<String> salidaMinutos;
    @FXML private JFXComboBox<String> cbxDiaEntrada;
    @FXML private JFXComboBox<String> cbxDiaSalida;
    @FXML private JFXComboBox<EmpleadosHorariosDTO> cbxHorarios;
    @FXML private Tab tabArear;
    @FXML private JFXTextField txtId;
    @FXML private JFXTextField txtJefe;
    @FXML private Label lblArea;
    @FXML private TableView<EmpleadosAreasTrabajosDTO> tvAreas;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colArea;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colDescripcion;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colEstado;
    @FXML
    private JFXButton btnActInac;

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
        llenarComboBoxs();
        clickTablas();
        cargarTablaHorarios();
        cargarTablaAreas();
    }

    @Override
    public void initialize() {
    }

    public void llenarComboBoxs() {
        cbxRoles.getItems().clear();
        Respuesta resp = rolesService.getAll();
        listRoles = (List<RolesDTO>) resp.getResultado("Roles");
        if (listRoles != null) {
            ObservableList items = FXCollections.observableArrayList(listRoles);
            cbxRoles.setItems(items);
        }
    }

    public void clickTablas() {
        tablaHorarios.setRowFactory(tv -> {
            TableRow<EmpleadosHorariosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    horarSeleccionado = true;
                    horarioSeleccionado = row.getItem();
                    cargarDatosHorarios();
                }
            });
            return row;
        });
    }

    public void llenarListaAreas(){
        tvAreas.getItems().clear();
        tvAreas.getItems().addAll(emplSeleccionado.getEmpleadosAreasTrabajo());
    }
    
    @FXML
    private void actLimpiarCamposEmplead(ActionEvent event) {
        limpiarCampos();
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
                empleadoDTO.setJefe(jefeSelect);
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
                empleadoDTO.setJefe(jefeSelect);
                empleadoDTO.setNombre(txtNombre.getText());
                empleadoDTO.setRol(cbxRoles.getValue());
                Respuesta res = empleadoService.guardarEmpleado(empleadoDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Empleado guardado correctamente");
                    emplSeleccionado = (EmpleadosDTO) res.getResultado("Empleados");
                    tablaHorarios.getItems().clear();
                    tablaHorarios.getItems().addAll(emplSeleccionado.getHorarios());
                    llenarListaAreas();
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error al guardar ", res.getMensaje());
                }
            }
        }
    }

    void limpiarCampos() {
        txtNombre.setText(null);
        txtCedula.setText(null);
        txtContrasena.setText(null);
        txtJefe.clear();
        cbxRoles.setValue(null);
        empSeleccionado = false;
        emplSeleccionado = null;
        tablaHorarios.getItems().clear();
        tvAreas.getItems().clear();
    }

    private void actTabPane(MouseEvent event) {
        if (tabCrear.isSelected() && empSeleccionado == false) {
            limpiarCampos();
        } else if (tabHorarios.isSelected()) {
            
        } else if (tabMarcajes.isSelected()) {
        }
    }

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
        txtId.setDisable(true);
        txtId.setText(emplSeleccionado.getId().toString());
        txtCedula.setText(emplSeleccionado.getCedula());
        txtNombre.setText(emplSeleccionado.getNombre());
        txtJefe.setText(emplSeleccionado.getJefe() == null ? "Sin jefe directo" : emplSeleccionado.getJefe().getNombre());
        cbxRoles.setValue(emplSeleccionado.getRol());
    }

    public void cargarTablaHorarios() {
        String pattern = " HH:mm:ss";
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(timeZone);
        tablaHorarios.getColumns().clear();
        TableColumn<EmpleadosHorariosDTO, String> colDiaE = new TableColumn<>("Dia de entrada");
        colDiaE.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaEntrada()));
        TableColumn<EmpleadosHorariosDTO, String> colDiaS = new TableColumn<>("Dia de salida");
        colDiaS.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaSalida()));
        TableColumn<EmpleadosHorariosDTO, String> colHoraE = new TableColumn<>("Hora de entrada");
        colHoraE.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getHoraEntrada() == null ? "Sin Hora" : String.valueOf(simpleDateFormat.format(p.getValue().getHoraEntrada()))));
        TableColumn<EmpleadosHorariosDTO, String> colHoraS = new TableColumn<>("hora de salida");
        colHoraS.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getHoraSalida() == null ? "Sin Hora" : String.valueOf(simpleDateFormat.format(p.getValue().getHoraSalida())) ));
        tablaHorarios.getColumns().addAll(colDiaE, colDiaS, colHoraE, colHoraS);
    }
    
    private void cargarTablaAreas(){
        colArea.setCellValueFactory( p -> new SimpleStringProperty(p.getValue().getAreaTrabajo().getNombre()));
        colDescripcion.setCellValueFactory( p -> new SimpleStringProperty(p.getValue().getAreaTrabajo().getDescripcion()));
        colEstado.setCellValueFactory( p -> new SimpleStringProperty(p.getValue().isEstado() ? "Activo" : "Inactiva"));
    }
    
    private void cargarDatosHorarios(){
        LocalDateTime he, hs;
        cbxDiaEntrada.getSelectionModel().select(horarioSeleccionado.getDiaEntrada());
        cbxDiaSalida.getSelectionModel().select(horarioSeleccionado.getDiaSalida());
        he = horarioSeleccionado.getHoraEntrada().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        hs = horarioSeleccionado.getHoraSalida().toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        entradaHoras.getSelectionModel().select((he.getHour() < 10) ? "0"+String.valueOf(he.getHour()) : String.valueOf(he.getHour()));
        entradaMinutos.getSelectionModel().select((he.getMinute() < 10) ? "0"+String.valueOf(he.getMinute()) : String.valueOf(he.getMinute()));
        salidaHoras.getSelectionModel().select((hs.getHour() < 10) ? "0"+String.valueOf(hs.getHour()) : String.valueOf(hs.getHour()));
        salidaMinutos.getSelectionModel().select((hs.getMinute() < 10) ? "0"+String.valueOf(hs.getMinute()) : String.valueOf(hs.getMinute()));
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
    }

    public boolean validarCamposHorario() {
        if (cbxDiaEntrada.getValue() == null || entradaHoras.getValue() == null) {
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
                horarioDTO.setEmpleado(emplSeleccionado);
                horarioDTO.setHoraEntrada(obtenerFecha(true));
                horarioDTO.setHoraSalida(obtenerFecha(false));
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
                horarioDTO.setEmpleado(emplSeleccionado);
                horarioDTO.setHoraEntrada(obtenerFecha(true));
                horarioDTO.setHoraSalida(obtenerFecha(false));
                Respuesta res = horarioService.guardarEmpleadoHorario(horarioDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Horario guardado correctamente");
                    cargarTablaHorarios();
                }
            }
        }
    }
    
    private Date obtenerFecha(boolean horaEntrada){
        LocalDateTime ldt = LocalDateTime.now(), ldt2;
        int hora = 0, min = 0;
        if(horaEntrada){
            try{
                hora = Integer.parseInt(entradaHoras.getSelectionModel().getSelectedItem());
                min = Integer.parseInt(entradaMinutos.getSelectionModel().getSelectedItem());
            }catch(NumberFormatException nfe){}
        }else{
            try{
                hora = Integer.parseInt(salidaHoras.getSelectionModel().getSelectedItem());
                min = Integer.parseInt(salidaMinutos.getSelectionModel().getSelectedItem());
            }catch(NumberFormatException nfe){}
        }
        ldt2 = LocalDateTime.of(ldt.getYear(), ldt.getMonth(), ldt.getDayOfMonth(), hora, min, 0, 0);
        return Date.from(ldt2.atZone(ZoneId.of("UTC")).toInstant());
    }

    @FXML
    private void actLimpiarCamposHorario(ActionEvent event) {
        cbxDiaEntrada.setValue(null);
        cbxDiaSalida.setValue(null);
        entradaHoras.setValue(null);
        entradaMinutos.setValue(null);
        salidaHoras.setValue(null);
        salidaMinutos.setValue(null);
        horarSeleccionado = false;
        horarioSeleccionado = null;
    }

    @FXML
    private void actGuardarMarcajes(ActionEvent event) {

    }

    @FXML
    private void actBuscarArea(ActionEvent event) {
        boolean existe = false;
        FlowController.getInstance().goViewInNoResizableWindow("BuscarArea", false, StageStyle.UTILITY);
        if(AppContext.getInstance().get("Area") != null){
            area = (AreasTrabajosDTO) AppContext.getInstance().get("Area");
            for(EmpleadosAreasTrabajosDTO empArea : emplSeleccionado.getEmpleadosAreasTrabajo()){
                if(empArea.getAreaTrabajo().getNombre().equals(area.getNombre())){
                    existe = true;
                    break;
                }
            }
            if(existe){
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Area", "El area de trabajo ya esta agregada");
            }else{
                lblArea.setText(area.getNombre());
            }
        }
    }

    @FXML
    private void actInactivarHorarioEmpleado(ActionEvent event) {
    }

    @FXML
    private void actActivarHorarioEmpleado(ActionEvent event) {
    }

    @FXML
    private void actBuscarEmpleado(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.UTILITY);
        emplSeleccionado = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if(emplSeleccionado != null){
            tablaHorarios.getItems().clear();
            tablaHorarios.getItems().addAll(emplSeleccionado.getHorarios());
            llenarListaAreas();
            cargarDatos();
            if(emplSeleccionado.isEstado()){
                btnActInac.setText("Inactivar");
            }else{
                btnActInac.setText("Activar");
            }
        }
    }

    @FXML
    private void accionBuscraJefe(MouseEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.UTILITY);
        jefeSelect  = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if(jefeSelect != null){
            txtJefe.setText(jefeSelect.getNombre());
        }else{
            txtJefe.setText("Sin jefe directo");
        }
    }

    @FXML
    private void actInactivarAreaEmpleado(ActionEvent event) {
    }

    @FXML
    private void actAgregarArea(ActionEvent event) {
        if(area != null){
            EmpleadosAreasTrabajosDTO areaDto = new EmpleadosAreasTrabajosDTO(Long.valueOf("0"), emplSeleccionado, area, true);
            Respuesta res = empAreasService.guardarEmpleadoAreaTrabajo(areaDto);
            if(res.getEstado()){
                emplSeleccionado.getEmpleadosAreasTrabajo().add((EmpleadosAreasTrabajosDTO) res.getResultado("Empleados_Areas_Trabajos"));
                tvAreas.refresh();
                lblArea.setText("");
            }else{
                Mensaje.show(Alert.AlertType.ERROR, "Asignar Area de Trabajo", res.getMensaje());
            }
        }
    }

    @FXML
    private void actInactivarActivarEmpleado(ActionEvent event) {
    }
}