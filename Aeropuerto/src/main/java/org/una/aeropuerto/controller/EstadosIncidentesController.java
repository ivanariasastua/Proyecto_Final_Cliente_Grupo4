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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.una.aeropuerto.dto.IncidentesRegistradosDTO;
import org.una.aeropuerto.dto.IncidentesRegistradosEstadosDTO;
import org.una.aeropuerto.service.IncidentesRegistradosEstadosService;
import org.una.aeropuerto.service.IncidentesRegistradosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class EstadosIncidentesController extends Controller implements Initializable {

    @FXML
    private TableView tabla;
    @FXML
    private JFXComboBox<String> cbxEstados;
    @FXML
    private Label txtIncidente;
    private IncidentesRegistradosEstadosService estadosService = new IncidentesRegistradosEstadosService();
    private IncidentesRegistradosEstadosDTO estadosDto = new IncidentesRegistradosEstadosDTO();
    List<IncidentesRegistradosEstadosDTO> listEstados = new ArrayList<>();
    Respuesta res;
    IncidentesRegistradosDTO incidenteRegistrado = new IncidentesRegistradosDTO();
    IncidentesRegistradosService incidentService = new IncidentesRegistradosService();
    Map<String, String> modoDesarrollo;
    List<IncidentesRegistradosEstadosDTO> estados;
    @FXML
    private JFXButton btnGuardar;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> item = FXCollections.observableArrayList("Registrado", "Iniciado", "Anulado", "En espera", "En ejecución", "Resuelto");
        cbxEstados.setItems(item);
        cargarColumnas();
    }

    @Override
    public void initialize() {
        estados = new ArrayList<>();
        cbxEstados.setValue(null);
        incidenteRegistrado = (IncidentesRegistradosDTO) AppContext.getInstance().get("EstadosIncidentes");
        txtIncidente.setText(incidenteRegistrado.getId().toString());
        cargarEstados();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista EstadosIncidentes");
        modoDesarrollo.put("Guardar", "Guardar responde al método actGuardar");
    }

    public void cargarColumnas() {
        tabla.getColumns().clear();
        TableColumn<IncidentesRegistradosEstadosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getEstado()));
        TableColumn<IncidentesRegistradosEstadosDTO, String> colFecha = new TableColumn<>("Fecha de registro");
        colFecha.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getFechaRegistro())));
        tabla.getColumns().addAll(colEstado, colFecha);
    }

    public void cargarEstados() {
        estados = new ArrayList<>();
        tabla.getItems().clear();
        Respuesta resp = incidentService.findById(incidenteRegistrado.getId());
        if (resp.getEstado()) {
            IncidentesRegistradosDTO incident = (IncidentesRegistradosDTO) resp.getResultado("Incidentes_Registrados");
            if (incident.getIncidentesRegistradosEstados() != null) {
                tabla.getItems().addAll((List<IncidentesRegistradosEstadosDTO>) incident.getIncidentesRegistradosEstados());
                estados = incident.getIncidentesRegistradosEstados();
            }
        }

    }

    private boolean validarEstados() {
        if (estados != null) {
            for (int i = 0; i < estados.size(); i++) { 
                if (estados.get(i).getEstado().equals("Anulado")) {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", "El incidente ya se encuentra Anulado");
                    return false;
                }
                if (estados.get(i).getEstado().equals("Resuelto")) {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", "El incidente ya se encuentra Resuelto");
                    return false;
                }
            }
        }
        return true;
    }

    @FXML
    private void actGuardar(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (cbxEstados.getValue().isEmpty() || cbxEstados.getValue() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo de estados es obligatorio");
            } else {
                estadosDto = new IncidentesRegistradosEstadosDTO();
                estadosDto.setEstado(cbxEstados.getValue());
                estadosDto.setIncidenteRegistrado(incidenteRegistrado);
                if (validarEstados()) {
                    Respuesta resp = estadosService.guardarIncidentesRegistradosEstados(estadosDto);
                    if (resp.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Estado del incidente guardado correctamente");
                        cargarEstados();
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

}
