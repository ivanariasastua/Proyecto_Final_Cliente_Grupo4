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
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.dto.ServiciosGastosDTO;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.service.ServiciosGastosService;
import org.una.aeropuerto.service.ServiciosService;
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
    private JFXComboBox<EmpleadosDTO> cbxResponsable;
    @FXML
    private JFXComboBox<ServiciosDTO> cbxServicio;
    @FXML
    private JFXComboBox<Integer> cbxDuracion;
    @FXML
    private JFXComboBox<String> cbxTiempo;
    List<ServiciosDTO> listServicios;
    List<EmpleadosDTO> listResponsables;
    private EmpleadosService emplService = new EmpleadosService();
    private ServiciosService servService = new ServiciosService();
    private ServiciosGastosDTO servGastDTO = new ServiciosGastosDTO();
    private ServiciosGastosService servGastService = new ServiciosGastosService();
    List<ServiciosGastosDTO> listGastosServ = new ArrayList<>();
    @FXML
    private Tab tabGastos;
    @FXML
    private Tab tabCrearEditar;
    boolean gastSelec = false;
    ServiciosGastosDTO gastoSelecciondo = new ServiciosGastosDTO();
    @FXML
    private TabPane tabPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        listServicios = new ArrayList<>();
        llenarComboBoxs();

    }

    @Override
    public void initialize() {
        listServicios = new ArrayList<>();
        listResponsables = new ArrayList<>();
        servGastDTO = new ServiciosGastosDTO();
        cargarColumnasTabla();
        cargarDatosGastosServ();
        clickTabla();
    }

    public void llenarComboBoxs() {
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

        Respuesta resp = emplService.getAll();
        listResponsables = (List<EmpleadosDTO>) resp.getResultado("Empleados");
        if (listResponsables != null) {
            ObservableList<EmpleadosDTO> responsables = FXCollections.observableArrayList(listResponsables);
            cbxResponsable.setItems(responsables);
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
            cbxDuracion.setValue(Integer.parseInt(String.valueOf(duracion.charAt(0) + duracion.charAt(1))));
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
        TableColumn<ServiciosGastosDTO, String> colResp = new TableColumn<>("Responsable");
        colResp.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getResponsable())));
        TableColumn<ServiciosGastosDTO, String> colPago = new TableColumn<>("Estado de Pago");
        colPago.setCellValueFactory((p) -> new SimpleStringProperty(estadoPago(p.getValue().isEstadoPago())));
        TableColumn<ServiciosGastosDTO, String> colGast = new TableColumn<>("Estado de Gasto");
        colGast.setCellValueFactory((p) -> new SimpleStringProperty(estadoGasto(p.getValue().isEstadoGasto())));
        TableColumn<ServiciosGastosDTO, String> colPeriod = new TableColumn<>("Periodicidad");
        colPeriod.setCellValueFactory((p) -> new SimpleStringProperty(periodicidad(p.getValue().getPerioricidad())));

        tablaGastosS.getColumns().addAll(colServi, colEmpresa, colNumC, colfecha, colResp, colPago, colGast, colPeriod);

    }

    public void cargarDatosGastosServ() {
        Respuesta res = servGastService.getAll();
        listGastosServ = (List<ServiciosGastosDTO>) res.getResultado("Servicios_Gastos");
        if (listGastosServ != null) {
            ObservableList items = FXCollections.observableArrayList(listGastosServ);
            tablaGastosS.setItems(items);
        } else {
            tablaGastosS.getItems().clear();
        }
    }

    public void cargarDatos() {
        txtEmpresa.setText(gastoSelecciondo.getEmpresa());
        txtNumContrato.setText(gastoSelecciondo.getNumeroContrato());
        cbxResponsable.setValue(gastoSelecciondo.getResponsable());
        cbxServicio.setValue(gastoSelecciondo.getServicio());
        cbxEstadoGasto.setValue(estadoGasto(gastoSelecciondo.isEstadoGasto()));
        cbxEstadoPago.setValue(estadoPago(gastoSelecciondo.isEstadoPago()));
        cbxPerioricidad.setValue(periodicidad(gastoSelecciondo.getPerioricidad()));
        duracion(String.valueOf(gastoSelecciondo.getDuracion()));
    }

    @FXML
    private void actEditarGastoS(ActionEvent event) {
        if (gastSelec == true) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tabCrearEditar);
            cargarDatos();
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar empleado", "Debe seleccionar un empleado");
        }
    }

    //tab de crear gastos de servicios
    public boolean validarCamposGastos() {
        if (txtEmpresa.getText() == null || txtEmpresa.getText().isEmpty() || txtNumContrato.getText() == null || txtNumContrato.getText().isEmpty() || cbxServicio.getValue() == null || cbxResponsable.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Son obligatorios los siguientes campos: \nEmpresa\nNumero de contrato\nServicio\nResponsable");
            return false;
        }
        return true;
    }

    public void editarGastoServicio() {
        servGastDTO.setId(gastoSelecciondo.getId());
        servGastDTO.setEmpresa(txtEmpresa.getText());
        servGastDTO.setNumeroContrato(txtNumContrato.getText());
        servGastDTO.setResponsable(cbxResponsable.getValue());
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
        Respuesta res = servGastService.modificarGastoServicio(gastoSelecciondo.getId(), servGastDTO);
        if (res.getEstado()) {
            Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Gasto de servicio editado corectamente");
            cargarColumnasTabla();
            cargarDatosGastosServ();
        }
    }

    @FXML
    private void actGuardarGastoS(ActionEvent event) {
        if (gastSelec == true) {
            editarGastoServicio();
        } else {
            if (validarCamposGastos()) {
                servGastDTO = new ServiciosGastosDTO();
                servGastDTO.setEmpresa(txtEmpresa.getText());
                servGastDTO.setNumeroContrato(txtNumContrato.getText());
                servGastDTO.setResponsable(cbxResponsable.getValue());
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
                Respuesta res = servGastService.guardarGastoServicio(servGastDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Gasto de servicio guardado corectamente");
                    cargarColumnasTabla();
                    cargarDatosGastosServ();
                }
            }
        }
    }

    public void limpiarCampos() {
        txtEmpresa.setText(null);
        txtNumContrato.setText(null);
        cbxDuracion.setValue(null);
        cbxEstadoGasto.setValue(null);
        cbxEstadoPago.setValue(null);
        cbxPerioricidad.setValue(null);
        cbxResponsable.setValue(null);
        cbxTiempo.setValue(null);
        gastSelec = false;
        gastoSelecciondo = new ServiciosGastosDTO();
    }

    @FXML
    private void actLimpiarGastoS(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void actTabPane(MouseEvent event) {
        if (tabGastos.isSelected()) {
            cargarColumnasTabla();
            cargarDatosGastosServ();
        } else if (tabCrearEditar.isSelected() && gastSelec == false) {
            limpiarCampos();
        }
    }

}
