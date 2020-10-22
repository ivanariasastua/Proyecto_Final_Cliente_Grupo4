/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.EmpleadosHorariosDTO;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.EmpleadosHorariosService;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.service.EmpleadosAreasTrabajosService;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.dto.EmpleadosAreasTrabajosDTO;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class EmpleadosController extends Controller implements Initializable {

    private final EmpleadosService empleadoService = new EmpleadosService();
    private final EmpleadosAreasTrabajosService empAreasService = new EmpleadosAreasTrabajosService();
    private final RolesService rolesService = new RolesService();
    private final EmpleadosHorariosService horarioService = new EmpleadosHorariosService();
    
    
    private EmpleadosDTO empleadoDTO = null, jefeSelect = null, emplSeleccionado = null;
    private EmpleadosHorariosDTO horarioSeleccionado = null, horarioDTO = null;
    private AreasTrabajosDTO area = null;
    private EmpleadosAreasTrabajosDTO areaSelected = null;
    
    @FXML private TableView tablaHorarios;
    @FXML private BorderPane bpPantalla;
    @FXML private VBox vbContenedor;
    @FXML private TabPane tabPane;
    @FXML private Tab tabCrear;
    @FXML private JFXTextField txtNombre;
    @FXML private JFXComboBox<RolesDTO> cbxRoles;
    @FXML private JFXTextField txtCedula;
    @FXML private Label lblTitulo;
    @FXML private Tab tabHorarios;
    @FXML private JFXComboBox<String> entradaHoras;
    @FXML private JFXComboBox<String> entradaMinutos;
    @FXML private JFXComboBox<String> salidaHoras;
    @FXML private JFXComboBox<String> salidaMinutos;
    @FXML private JFXComboBox<String> cbxDiaEntrada;
    @FXML private JFXComboBox<String> cbxDiaSalida;
    @FXML private Tab tabArear;
    @FXML private JFXTextField txtId;
    @FXML private JFXTextField txtJefe;
    @FXML private Label lblArea;
    @FXML private TableView<EmpleadosAreasTrabajosDTO> tvAreas;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colArea;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colDescripcion;
    @FXML private TableColumn<EmpleadosAreasTrabajosDTO, String> colEstado;
    @FXML private JFXButton btnActInac;
    @FXML private JFXTextField txtCorreo;
    @FXML private RowConstraints rowContrasena;
    @FXML private JFXPasswordField txtPass;
    @FXML private JFXCheckBox cbViewPass;
    @FXML private JFXTextField txtViewPass;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        llenarRelojs();
        ObservableList items = FXCollections.observableArrayList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
        cbxDiaEntrada.setItems(items);
        cbxDiaSalida.setItems(items);
        llenarComboBoxs();
        clickTablas();
        cargarTablaHorarios();
        cargarTablaAreas();
        txtId.setDisable(true);    
    }

    @Override
    public void initialize() {
        limpiarCampos();
    }

    public void llenarComboBoxs() {
        cbxRoles.getItems().clear();
        Respuesta resp = rolesService.getAll();
        if (resp.getResultado("Roles") != null) {
            cbxRoles.getItems().clear();
            cbxRoles.getItems().addAll((List<RolesDTO>) resp.getResultado("Roles"));
        }
    }

    public void clickTablas() {
        tablaHorarios.setRowFactory(tv -> {
            TableRow<EmpleadosHorariosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
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
        if(emplSeleccionado == null){
            if (txtCedula.getText() == null || txtNombre.getText() == null || txtCorreo.getText() == null || txtPass.getText() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos Nombre, Cedula y Contraseña son obligatorios");
                return false;
            }
        }else{
            if (txtCedula.getText() == null || txtNombre.getText() == null || txtCorreo.getText() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos Nombre y Cedula son obligatorios");
                return false;
            }
        }
        return true;
    }

    @FXML
    private void actGuardarEmpleado(ActionEvent event) {
        if (emplSeleccionado != null) {  //editar
            if (validarCampos()) {
                empleadoDTO.setId(emplSeleccionado.getId());
                empleadoDTO.setCedula(txtCedula.getText());
                empleadoDTO.setCorreo(txtCorreo.getText());
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
                empleadoDTO.setCorreo(txtCorreo.getText());
                empleadoDTO.setContrasenaEncriptada(txtPass.getText());
                empleadoDTO.setJefe(jefeSelect);
                empleadoDTO.setNombre(txtNombre.getText());
                empleadoDTO.setRol(cbxRoles.getValue());
                Respuesta res = empleadoService.guardarEmpleado(empleadoDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Empleado guardado correctamente");
                    emplSeleccionado = (EmpleadosDTO) res.getResultado("Empleados");
                    tablaHorarios.getItems().clear();
                    tvAreas.getItems().clear();
                } else {
                    System.out.println(res.getMensajeInterno());
                    Mensaje.show(Alert.AlertType.ERROR, "Error al guardar ", res.getMensaje());
                }
            }
        }
    }

    void limpiarCampos() {
        txtNombre.setText(null);
        txtCedula.setText(null);
        txtCorreo.setText(null);
        txtJefe.clear();
        cbxRoles.setValue(null);
        emplSeleccionado = null;
        tablaHorarios.getItems().clear();
        tvAreas.getItems().clear();
        txtId.setText("");
        txtPass.setText("");
        txtViewPass.setText("");
        txtPass.setVisible(true);
        cbViewPass.setVisible(true);
        txtViewPass.setVisible(true);
    }

    public void cargarDatos() {
        txtId.setText(emplSeleccionado.getId().toString());
        txtCedula.setText(emplSeleccionado.getCedula());
        txtNombre.setText(emplSeleccionado.getNombre());
        txtJefe.setText(emplSeleccionado.getJefe() == null ? "Sin jefe directo" : emplSeleccionado.getJefe().getNombre());
        if(emplSeleccionado.getJefe() != null){
            jefeSelect = emplSeleccionado.getJefe();
        }
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
        if (horarioSeleccionado != null) {
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
        horarioSeleccionado = null;
        area = null;
        areaSelected = null;
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
        if(horarioSeleccionado != null){
            Boolean puedeInactivar = Boolean.FALSE;
            String cedula = "", codigo = "";
            if(UserAuthenticated.getInstance().isRol("GERENTE")){
                cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                codigo = (String) AppContext.getInstance().get("CodigoGerente");
                puedeInactivar = Boolean.TRUE;
            }else if(UserAuthenticated.getInstance().isRol("GESTOR")){
                Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Horario de Empleado");
                if(result.isPresent()){
                    cedula = result.get().getKey();
                    codigo = result.get().getValue();
                    puedeInactivar = Boolean.TRUE;
                }
            }
            if(puedeInactivar){
                Respuesta res = horarioService.inactivar(horarioSeleccionado, horarioSeleccionado.getId(), cedula, codigo);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Horario de empleado", "El horario ha sido inactivado");
                }else{
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Horario de empleados", res.getMensaje());
                }
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Inactivar Area de Trabajo de empleado", "No ha seleccionado ninguna area de trabajo");
        }
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
            txtPass.setVisible(false);
            cbViewPass.setVisible(false);
            txtViewPass.setVisible(false);
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
        if(areaSelected != null){
            Boolean puedeInactivar = Boolean.FALSE;
            String cedula = "", codigo = "";
            if(UserAuthenticated.getInstance().isRol("GERENTE")){
                cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                codigo = (String) AppContext.getInstance().get("CodigoGerente");
                puedeInactivar = Boolean.TRUE;
            }else if(UserAuthenticated.getInstance().isRol("GESTOR")){
                Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Area de Trabajo de Empleado");
                if(result.isPresent()){
                    cedula = result.get().getKey();
                    codigo = result.get().getValue();
                    puedeInactivar = Boolean.TRUE;
                }
            }
            if(puedeInactivar){
                Respuesta res = empAreasService.inactivar(areaSelected, areaSelected.getId(), cedula, codigo);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Area de Trabajo de empleado", "El area ha sido inactivado");
                }else{
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Area de Trabajo de empleados", res.getMensaje());
                }
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Inactivar Area de Trabajo de empleado", "No ha seleccionado ninguna area de trabajo");
        }
    }

    @FXML
    private void actAgregarArea(ActionEvent event) {
        if(area != null){
            EmpleadosAreasTrabajosDTO areaDto = new EmpleadosAreasTrabajosDTO(Long.valueOf("0"), emplSeleccionado, area, true);
            Respuesta res = empAreasService.guardarEmpleadoAreaTrabajo(areaDto);
            if(res.getEstado()){
                emplSeleccionado.getEmpleadosAreasTrabajo().add((EmpleadosAreasTrabajosDTO) res.getResultado("Empleados_Areas_Trabajos"));
                tvAreas.getItems().clear();
                tvAreas.getItems().addAll(emplSeleccionado.getEmpleadosAreasTrabajo());
                lblArea.setText("");
            }else{
                Mensaje.show(Alert.AlertType.ERROR, "Asignar Area de Trabajo", res.getMensaje());
            }
        }
    }

    @FXML
    private void actInactivarEmpleado(ActionEvent event) {
        if(emplSeleccionado != null){
            Boolean puedeInactivar = Boolean.FALSE;
            String cedula = "", codigo = "";
            if(UserAuthenticated.getInstance().isRol("GERENTE")){
                cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                codigo = (String) AppContext.getInstance().get("CodigoGerente");
                puedeInactivar = Boolean.TRUE;
            }else if(UserAuthenticated.getInstance().isRol("GESTOR")){
                Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Empleado");
                if(result.isPresent()){
                    cedula = result.get().getKey();
                    codigo = result.get().getValue();
                    puedeInactivar = Boolean.TRUE;
                }
            }
            if(puedeInactivar){
                Respuesta res = empleadoService.inactivar(emplSeleccionado, emplSeleccionado.getId(), cedula, codigo);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Empleados", "El usuario: "+emplSeleccionado.getNombre()+" ha sido inactivado");
                }else{
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Empleados", res.getMensaje());
                }
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Inactivar Empleado", "No ha seleccionado ningún empleado");
        }
    }

    @Override
    public void cargarTema() {
    }

    @FXML
    private void actTablaAreas(MouseEvent event) {
        if(tvAreas.getSelectionModel().getSelectedItem() != null){
            areaSelected = tvAreas.getSelectionModel().getSelectedItem();
        }
    }
}