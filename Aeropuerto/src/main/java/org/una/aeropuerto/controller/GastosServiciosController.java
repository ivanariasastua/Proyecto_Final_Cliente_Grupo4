/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import org.una.aeropuerto.util.DateUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.dto.ServiciosGastosDTO;
import org.una.aeropuerto.service.ServiciosGastosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Formato;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class GastosServiciosController extends Controller implements Initializable {
    
    @FXML
    private TableView tablaGastosS;
    @FXML
    private JFXComboBox<String> cbxEstadoPago;
    @FXML
    private JFXTextField txtNumContrato;
    @FXML
    private JFXTextField txtEmpresa;
    @FXML
    private JFXComboBox<String> cbxEstadoGasto;
    @FXML
    private JFXComboBox<String> cbxPerioricidad;
    @FXML
    private JFXComboBox<Integer> cbxDuracion;
    @FXML
    private JFXComboBox<String> cbxTiempo;
    @FXML
    private Tab tabGastos;
    @FXML
    private Tab tabCrearEditar;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXTextField txtBuscarGastosS;
    @FXML
    private JFXComboBox<String> cbxFiltro;
    @FXML
    private JFXTextField txtResponsable;
    
    List<ServiciosDTO> listServicios;
    private ServiciosGastosDTO servGastDTO = new ServiciosGastosDTO();
    private ServiciosGastosService servGastService = new ServiciosGastosService();
    boolean gastSelec = false;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    ServiciosGastosDTO gastoSelecciondo = new ServiciosGastosDTO();
    EmpleadosDTO responsableSelec;
    ServiciosDTO servicioSelec;
    private ListView<String> lvDesarrollo;
    private Map<String, String> modoDesarrollo;
    @FXML
    private JFXTextField txtServicio;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private BorderPane bpRoot;
    @FXML
    private VBox vbRoot;
    @FXML
    private VBox vbTabla;
    @FXML
    private GridPane gpCrearEditar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        listServicios = new ArrayList<>();
        llenarComboBoxs();
        clickTabla();
        addListner();
        datosModoDesarrollo();
        txtEmpresa.setTextFormatter(Formato.getInstance().maxLengthFormat(50));
        txtNumContrato.setTextFormatter(Formato.getInstance().maxLengthFormat(50));
    }
    
    @Override
    public void initialize() {
        listServicios = new ArrayList<>();
        servGastDTO = new ServiciosGastosDTO();
        responsableSelec = new EmpleadosDTO();
        servicioSelec = new ServiciosDTO();
        limpiarCampos();
        cargarColumnasTabla();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeigth(contenedor.getHeight());
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            asignarInfoModoDesarrollo();
        }
    }
    
    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista GastosServicios");
        modoDesarrollo.put("Buscar Gasto", "Buscar responde al método actBuscarGastosServicios");
        modoDesarrollo.put("Editar Gasto", "Editar responde al método actEditarGastoS");
        modoDesarrollo.put("Inactivar Gasto", "Inactivar responde al método actInactivarGastoS");
        modoDesarrollo.put("Buscar Servicio", "Buscar Servicio responde al método actBuscarServicio");
        modoDesarrollo.put("Buscar Empleado", "Buscar Empleado responde al método actBuscarResponsable");
        modoDesarrollo.put("Limpiar Gasto", "Limpiar responde al método actLimpiarGastoS");
        modoDesarrollo.put("Guardar Gasto", "Guardar responde al método actGuardarGastoS");
    }
    
    private void asignarInfoModoDesarrollo() {
        lvDesarrollo.getItems().clear();
        for (String info : modoDesarrollo.keySet()) {
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    public void llenarComboBoxs() {
        ObservableList filtro = FXCollections.observableArrayList("Empresa", "Número de contrato", "Servicio");
        cbxFiltro.setItems(filtro);
        
        ObservableList<String> estadoPag = FXCollections.observableArrayList("Pago", "Pendiente");
        cbxEstadoPago.setItems(estadoPag);
        
        ObservableList<String> estadoGast = FXCollections.observableArrayList("Activo", "Anulado");
        cbxEstadoGasto.setItems(estadoGast);
        
        ObservableList<String> periodici = FXCollections.observableArrayList("Diario", "Semanal", "Quincenal", "Mensual", "Anual");
        cbxPerioricidad.setItems(periodici);
        
        List<Integer> numeros = new ArrayList<>();
        for (int i = 1; i <= 31; i++) {
            numeros.add(i);
        }
        ObservableList<Integer> duaracion = FXCollections.observableArrayList(numeros);
        cbxDuracion.setItems(duaracion);
        
        ObservableList<String> tiempo = FXCollections.observableArrayList("Día(s)", "Semana(s)", "Mes(es)", "Año(s)");
        cbxTiempo.setItems(tiempo);
    }
    
    public String estadoPago(boolean num) {
        if (num == true) {
            return "Pago";
        } else if (num == false) {
            return "Pendiente";
        }
        return "";
    }
    
    public String estadoGasto(boolean num) {
        if (num == true) {
            return "Activo";
        } else if (num == false) {
            return "Anulado";
        }
        return "";
    }
    
    public String periodicidad(Integer num) {
        switch (num) {
            case 1:
                return "Diario";
            case 2:
                return "Semanal";
            case 3:
                return "Quincenal";
            case 4:
                return "Mensual";
            case 5:
                return "Anual";
        }
        return "";
    }
    
    public String retornarPeriodo(char num) {
        if (num == '1') {
            return "Día(s)";
        } else if (num == '2') {
            return "Semana(s)";
        } else if (num == '3') {
            return "Mes(es)";
        } else if (num == '4') {
            return "Año(s)";
        }
        return "";
    }
    
    public void duracion(String duracion) {
        if (duracion.length() == 3) {
            String num = duracion.charAt(0) + "" + duracion.charAt(1);
            cbxDuracion.setValue(Integer.valueOf(num));
            cbxTiempo.setValue(retornarPeriodo(duracion.charAt(2)));
        } else if (duracion.length() == 2) {
            cbxDuracion.setValue(Integer.parseInt(String.valueOf(duracion.charAt(0))));
            cbxTiempo.setValue(retornarPeriodo(duracion.charAt(1)));
        }
    }
    
    public void clickTabla() {
        tablaGastosS.setRowFactory(tv -> {
            TableRow<ServiciosGastosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    gastSelec = true;
                    gastoSelecciondo = row.getItem();
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
    
    public void cargarDatos() {
        responsableSelec = gastoSelecciondo.getResponsable();
        txtEmpresa.setText(gastoSelecciondo.getEmpresa());
        txtNumContrato.setText(gastoSelecciondo.getNumeroContrato());
        txtResponsable.setText(gastoSelecciondo.getResponsable().getNombre());
        txtServicio.setText(gastoSelecciondo.getServicio().getNombre());
        cbxEstadoGasto.setValue(estadoGasto(gastoSelecciondo.isEstadoGasto()));
        cbxEstadoPago.setValue(estadoPago(gastoSelecciondo.isEstadoPago()));
        cbxPerioricidad.setValue(periodicidad(gastoSelecciondo.getPerioricidad()));
        duracion(String.valueOf(gastoSelecciondo.getDuracion()));
    }
    
    @FXML
    private void actEditarGastoS(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Editar Gasto"));
        } else {
            if (gastSelec) {
                if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                    selectionModel.select(tabCrearEditar);
                    cargarDatos();
                } else {
                    gastSelec = false;
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Gasto", "Debe seleccionar un gasto de servicio");
            }
        }
    }
    
    public boolean validarCamposGastos() {
        if (txtEmpresa.getText() == null || txtEmpresa.getText().isEmpty() || txtNumContrato.getText() == null || txtNumContrato.getText().isEmpty() || txtServicio.getText() == null || txtResponsable.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Son obligatorios los siguientes campos: \nEmpresa\nNúmero de contrato\nServicio\nResponsable");
            return false;
        }
        return true;
    }
    
    public void guardar() {
        servGastDTO.setEmpresa(txtEmpresa.getText());
        servGastDTO.setNumeroContrato(txtNumContrato.getText());
        if (cbxEstadoGasto.getValue().equals("Activo")) {
            servGastDTO.setEstadoGasto(true);
        } else {
            servGastDTO.setEstadoGasto(false);
        }
        if (cbxEstadoPago.getValue().equals("Pago")) {
            servGastDTO.setEstadoPago(true);
        } else {
            servGastDTO.setEstadoPago(false);
        }
        if (cbxPerioricidad.getValue().equals("Diario")) {
            servGastDTO.setPerioricidad(1);
        } else if (cbxPerioricidad.getValue().equals("Semanal")) {
            servGastDTO.setPerioricidad(2);
        } else if (cbxPerioricidad.getValue().equals("Quincenal")) {
            servGastDTO.setPerioricidad(3);
        } else if (cbxPerioricidad.getValue().equals("Mensual")) {
            servGastDTO.setPerioricidad(4);
        } else if (cbxPerioricidad.getValue().equals("Anual")) {
            servGastDTO.setPerioricidad(5);
        }
        if (cbxTiempo.getValue().equals("Día(s)")) {
            servGastDTO.setDuracion(Long.valueOf(cbxDuracion.getValue() + "1"));
        } else if (cbxTiempo.getValue().equals("Semana(s)")) {
            servGastDTO.setDuracion(Long.valueOf(cbxDuracion.getValue() + "2"));
        } else if (cbxTiempo.getValue().equals("Mes(es)")) {
            servGastDTO.setDuracion(Long.valueOf(cbxDuracion.getValue() + "3"));
        } else if (cbxTiempo.getValue().equals("Año(s)")) {
            servGastDTO.setDuracion(Long.valueOf(cbxDuracion.getValue() + "4"));
        }
    }
    
    public boolean validarActivos() {
        if (gastoSelecciondo.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }
    
    @FXML
    private void actGuardarGastoS(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Guardar Gasto"));
        } else {
            if (gastSelec == true) {
                if (validarActivos()) {
                    servGastDTO.setId(gastoSelecciondo.getId());
                    if (gastoSelecciondo.isEstado()) {
                        servGastDTO.setEstado(true);
                    }
                    guardar();
                    if (responsableSelec.getNombre() != null) {
                        servGastDTO.setResponsable(responsableSelec);
                    } else {
                        servGastDTO.setResponsable(gastoSelecciondo.getResponsable());
                    }
                    if (servicioSelec.getNombre() == null) {
                        servGastDTO.setServicio(gastoSelecciondo.getServicio());
                        
                    } else {
                        servGastDTO.setServicio(servicioSelec);
                    }
                    Respuesta res = servGastService.modificarGastoServicio(gastoSelecciondo.getId(), servGastDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Gasto de servicio editado corectamente");
                        ServiciosGastosDTO act = (ServiciosGastosDTO) res.getResultado("Servicios_Gastos");
                        actualizarDatos(tablaGastosS.getItems(), act);
                        Platform.runLater(() -> {
                            tablaGastosS.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            } else {
                if (validarCamposGastos()) {
                    servGastDTO = new ServiciosGastosDTO();
                    guardar();
                    servGastDTO.setResponsable(responsableSelec);
                    servGastDTO.setServicio(servicioSelec);
                    Respuesta res = servGastService.guardarGastoServicio(servGastDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Gasto de servicio guardado corectamente");
                        ServiciosGastosDTO act = (ServiciosGastosDTO) res.getResultado("Servicios_Gastos");
                        tablaGastosS.getItems().add(act);
                        Platform.runLater(() -> {
                            tablaGastosS.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }
    
    public void limpiarCampos() {
        servGastDTO = new ServiciosGastosDTO();
        txtServicio.setText(null);
        txtEmpresa.setText(null);
        txtNumContrato.setText(null);
        cbxDuracion.setValue(null);
        cbxEstadoGasto.setValue(null);
        cbxEstadoPago.setValue(null);
        cbxPerioricidad.setValue(null);
        txtResponsable.setText(null);
        cbxTiempo.setValue(null);
        gastSelec = false;
        gastoSelecciondo = new ServiciosGastosDTO();
        responsableSelec = new EmpleadosDTO();
        servicioSelec = new ServiciosDTO();
    }
    
    @FXML
    private void actLimpiarGastoS(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Limpiar Gasto"));
        } else {
            limpiarCampos();
        }
    }
    
    @FXML
    private void actTabPane(MouseEvent event) {
        if (tabGastos.isSelected()) {
        } else if (tabCrearEditar.isSelected()) {
            
        }
    }
    
    @FXML
    private void actInactivarGastoS(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar Gasto"));
        } else {
            if (gastoSelecciondo != null) {
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "", codigo = "";
                if (UserAuthenticated.getInstance().isRol("GERENTE")) {
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                } else if (UserAuthenticated.getInstance().isRol("GESTOR")) {
                    Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Gasto en Servicio");
                    if (result.isPresent()) {
                        cedula = result.get().getKey();
                        codigo = result.get().getValue();
                        puedeInactivar = Boolean.TRUE;
                    }
                }
                if (puedeInactivar) {
                    Respuesta res = servGastService.inactivar(gastoSelecciondo, gastoSelecciondo.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Gasto en Servicio", "El Gasto en Servicio ha sido inactivado");
                        gastSelec = false;
                        ServiciosGastosDTO act = (ServiciosGastosDTO) res.getResultado("Servicios_Gastos");
                        actualizarDatos(tablaGastosS.getItems(), act);
                        Platform.runLater(() -> {
                            tablaGastosS.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Gasto en Servicio", res.getMensaje());
                    }
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Inactivar Gasto en Servicio", "No ha seleccionado ninguna Gasto en Servicio");
            }
        }
    }
    
    private void actualizarDatos(List<ServiciosGastosDTO> list, ServiciosGastosDTO gat) {
        for (ServiciosGastosDTO s : list) {
            if (s.getId().equals(gat.getId())) {
                s.setEstado(gat.isEstado());
                s = gat;
            }
        }
    }
    
    public void cargarColumnasTabla() {
        tablaGastosS.getColumns().clear();
        TableColumn<ServiciosGastosDTO, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getEmpresa()));
        TableColumn<ServiciosGastosDTO, String> colNumC = new TableColumn<>("Número contrato");
        colNumC.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNumeroContrato()));
        TableColumn<ServiciosGastosDTO, String> colfecha = new TableColumn<>("Fecha registro");
        colfecha.setCellValueFactory((p) -> new SimpleStringProperty(DateUtils.asLocalDate(p.getValue().getFechaRegistro()).toString()));
        TableColumn<ServiciosGastosDTO, String> colServi = new TableColumn<>("Servicio");
        colServi.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getServicio())));
        TableColumn<ServiciosGastosDTO, String> colPago = new TableColumn<>("Estado de Pago");
        colPago.setCellValueFactory((p) -> new SimpleStringProperty(estadoPago(p.getValue().isEstadoPago())));
        TableColumn<ServiciosGastosDTO, String> colGast = new TableColumn<>("Estado de Gasto");
        colGast.setCellValueFactory((p) -> new SimpleStringProperty(estadoGasto(p.getValue().isEstadoGasto())));
        TableColumn<ServiciosGastosDTO, String> colPeriod = new TableColumn<>("Periodicidad");
        colPeriod.setCellValueFactory((p) -> new SimpleStringProperty(periodicidad(p.getValue().getPerioricidad())));
        TableColumn<ServiciosGastosDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        
        tablaGastosS.getColumns().addAll(colServi, colEmpresa, colNumC, colfecha, colPago, colGast, colPeriod, colEst);
    }
    
    @FXML
    private void actBuscarGastosServicios(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Gasto"));
        } else {
            AppContext.getInstance().set("Task", buscarGastosTask());
            FlowController.getInstance().goViewCargar();
        }
    }
    
    private Task buscarGastosTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (cbxFiltro.getValue() == null) {
                    Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cúal tipo desea filtrar la información");
                } else {
                    if (!txtBuscarGastosS.getText().isEmpty()) {
                        Platform.runLater(() -> {
                            cargarColumnasTabla();
                        });
                        tablaGastosS.getItems().clear();
                        Respuesta res;
                        if (cbxFiltro.getValue().equals("Empresa")) {
                            res = servGastService.getByEmpresa(txtBuscarGastosS.getText());
                        } else if (cbxFiltro.getValue().equals("Número de contrato")) {
                            res = servGastService.getByContrato(txtBuscarGastosS.getText());
                        } else {
                            res = servGastService.findByServicio(txtBuscarGastosS.getText());
                        }
                        if (res.getEstado()) {
                            tablaGastosS.getItems().addAll((List<ServiciosGastosDTO>) res.getResultado("Servicios_Gastos"));
                        } else {
                            Mensaje.show(Alert.AlertType.ERROR, "Buscar Gastos de servicios ", res.getMensaje());
                        }
                    }
                }
                return true;
            }
            
        };
    }
    
    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Empleado"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            responsableSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if (responsableSelec != null) {
                txtResponsable.setText(responsableSelec.getNombre());
            }
        }
    }
    
    @FXML
    private void actBuscarServicio(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Servicio"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarServicios", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarServicios", false, StageStyle.DECORATED);
            servicioSelec = (ServiciosDTO) AppContext.getInstance().get("servSelect");
            if (servicioSelec != null) {
                txtServicio.setText(servicioSelec.getNombre());
            }
        }
    }
    
    @Override
    public void cargarTema() {
    }
    
    private void addListner() {
        contenedor.widthProperty().addListener(w -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener(h -> {
            adjustHeigth(contenedor.getHeight());
        });
    }
    
    private void adjustWidth(double ancho) {
        bpRoot.setPrefWidth(ancho);
        vbRoot.setPrefWidth(ancho);
        tabPane.setPrefWidth(ancho);
        vbTabla.setPrefWidth(ancho);
        tablaGastosS.setPrefWidth(ancho);
        gpCrearEditar.setPrefWidth(ancho);
        cbxDuracion.setPrefWidth((ancho / 2) - 50);
        cbxEstadoGasto.setPrefWidth((ancho / 2) - 50);
        cbxEstadoPago.setPrefWidth((ancho / 2) - 50);
        cbxPerioricidad.setPrefWidth((ancho / 2) - 50);
        cbxTiempo.setPrefWidth((ancho / 2) - 50);
    }
    
    private void adjustHeigth(double altura) {
        bpRoot.setPrefHeight(altura);
        vbRoot.setPrefHeight(altura);
        tabPane.setPrefHeight(altura - 50);
        vbTabla.setPrefHeight(altura - 100);
        tablaGastosS.setPrefHeight(altura - 261);
        gpCrearEditar.setPrefHeight(altura);
    }
}
