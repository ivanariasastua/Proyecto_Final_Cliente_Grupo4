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
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.dto.ServiciosGastosDTO;
import org.una.aeropuerto.service.ServiciosGastosService;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

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
    private JFXComboBox<ServiciosDTO> cbxServicio;
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
    private ServiciosService servService = new ServiciosService();
    private ServiciosGastosDTO servGastDTO = new ServiciosGastosDTO();
    private ServiciosGastosService servGastService = new ServiciosGastosService();
    List<ServiciosGastosDTO> listGastosServ = new ArrayList<>();
    boolean gastSelec = false;
    ServiciosGastosDTO gastoSelecciondo = new ServiciosGastosDTO();
    EmpleadosDTO responsableSelec;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listServicios = new ArrayList<>();
        llenarComboBoxs();
        clickTabla();
    }

    @Override
    public void initialize() {
        listServicios = new ArrayList<>();
        servGastDTO = new ServiciosGastosDTO();
        responsableSelec = new EmpleadosDTO();
        limpiarCampos();
        cargarColumnasTabla();
    }

    public void llenarComboBoxs() {
        ObservableList filtro = FXCollections.observableArrayList("Empresa", "Numero de contraro", "Servicio");
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

        ObservableList<String> tiempo = FXCollections.observableArrayList("Dia(s)", "Semana(s)", "Mes(es)", "Año(s)");
        cbxTiempo.setItems(tiempo);

        Respuesta res = servService.getAll();
        listServicios = (List<ServiciosDTO>) res.getResultado("Servicios");
        if (listServicios != null) {
            ObservableList<ServiciosDTO> servicios = FXCollections.observableArrayList(listServicios);
            cbxServicio.setItems(servicios);
        }
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
            return "Dia(s)";
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
        } else {
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
        cbxServicio.setValue(gastoSelecciondo.getServicio());
        cbxEstadoGasto.setValue(estadoGasto(gastoSelecciondo.isEstadoGasto()));
        cbxEstadoPago.setValue(estadoPago(gastoSelecciondo.isEstadoPago()));
        cbxPerioricidad.setValue(periodicidad(gastoSelecciondo.getPerioricidad()));
        duracion(String.valueOf(gastoSelecciondo.getDuracion()));
    }

    @FXML
    private void actEditarGastoS(ActionEvent event) {
        if (gastSelec == true) {
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

    public boolean validarCamposGastos() {
        if (txtEmpresa.getText() == null || txtEmpresa.getText().isEmpty() || txtNumContrato.getText() == null || txtNumContrato.getText().isEmpty() || cbxServicio.getValue() == null || txtResponsable.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Son obligatorios los siguientes campos: \nEmpresa\nNumero de contrato\nServicio\nResponsable");
            return false;
        }
        return true;
    }

    public void guardar() {
        servGastDTO.setEmpresa(txtEmpresa.getText());
        servGastDTO.setNumeroContrato(txtNumContrato.getText());
        servGastDTO.setResponsable(responsableSelec);
        servGastDTO.setServicio(cbxServicio.getValue());
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
        if (cbxTiempo.getValue().equals("Dia(s)")) {
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
        if (gastSelec == true) {
            if(validarActivos()){
                servGastDTO.setId(gastoSelecciondo.getId());
                if(gastoSelecciondo.isEstado()){
                    servGastDTO.setEstado(true);
                }
                guardar();
                Respuesta res = servGastService.modificarGastoServicio(gastoSelecciondo.getId(), servGastDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Gasto de servicio editado corectamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                }
            }
        } else {
            if (validarCamposGastos()) {
                servGastDTO = new ServiciosGastosDTO();
                guardar();
                Respuesta res = servGastService.guardarGastoServicio(servGastDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Gasto de servicio guardado corectamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                }
            }
        }
    }

    public void limpiarCampos() {
        servGastDTO= new ServiciosGastosDTO();
        cbxServicio.setValue(null);
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
    }

    @FXML
    private void actLimpiarGastoS(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void actTabPane(MouseEvent event) {
        if (tabGastos.isSelected()) {
        } else if (tabCrearEditar.isSelected() && gastSelec == false) {
            limpiarCampos();
        }
    }

    @FXML
    private void actInactivarGastoS(ActionEvent event) {
        if (gastSelec == true) {
            if (Mensaje.showConfirmation("Inactivar", null, "Seguro que desea inactivar la información?")) {
                if(validarActivos()){
                    gastoSelecciondo.setEstado(false);
                    Respuesta res = servGastService.modificarGastoServicio(gastoSelecciondo.getId(), gastoSelecciondo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Se ha inactivado correctamente el gasto de servicio");
                        gastSelec = false;
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            } else {
                gastSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Gasto de servicio", "Debe seleccionar un gasto de servicio");
        }
    }

    public void cargarColumnasTabla() {
        tablaGastosS.getColumns().clear();
        TableColumn<ServiciosGastosDTO, String> colEmpresa = new TableColumn<>("Empresa");
        colEmpresa.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getEmpresa()));
        TableColumn<ServiciosGastosDTO, String> colNumC = new TableColumn<>("Numero contrato");
        colNumC.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNumeroContrato()));
        TableColumn<ServiciosGastosDTO, String> colfecha = new TableColumn<>("Fecha registro");
        colfecha.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getFechaRegistro())));
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
        cargarColumnasTabla();
        tablaGastosS.getItems().clear();
        if (cbxFiltro.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
        } else {
            Respuesta res;
            if (cbxFiltro.getValue().equals("Empresa")) {
                res = servGastService.getByEmpresa(txtBuscarGastosS.getText());
            } else if (cbxFiltro.getValue().equals("Numero de contraro")) {
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

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.UTILITY);
        responsableSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if (responsableSelec != null) {
            txtResponsable.setText(responsableSelec.getNombre());
        }
    }

}
