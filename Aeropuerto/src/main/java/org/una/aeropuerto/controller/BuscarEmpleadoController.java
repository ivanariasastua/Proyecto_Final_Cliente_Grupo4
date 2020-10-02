/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.service.EmpleadosService;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class BuscarEmpleadoController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtBuscarEmpleados;
    @FXML
    private JFXComboBox<String> cbBuscarEmpleado;
    @FXML
    private TableView<EmpleadosDTO> tablaEmpleados;
    
    private EmpleadosService service;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initVista();
        service = new EmpleadosService();
    }    

    @FXML
    private void accionBuscarEmpleado(ActionEvent event) {
        if(cbBuscarEmpleado.getSelectionModel().getSelectedItem() != null && !txtBuscarEmpleados.getText().isEmpty()){
            tablaEmpleados.getItems().clear();
            String var = cbBuscarEmpleado.getSelectionModel().getSelectedItem();
            Respuesta res;
            if(var.equals("Por nombre")){
                res = service.getByNombre(txtBuscarEmpleados.getText());
            }else if(var.equals("Por area")){
                res = service.getByArea(txtBuscarEmpleados.getText());
            }else{
                res = service.getByCedula(txtBuscarEmpleados.getText());
            }
            if(res.getEstado()){
                tablaEmpleados.getItems().addAll((List<EmpleadosDTO>)res.getResultado("Empleados"));
            }else{
                Mensaje.show(Alert.AlertType.ERROR, "Buscar Empleados", res.getMensaje());
            }
        }
    }

    @FXML
    private void accionTablaEmpleados(MouseEvent event) {
        if(tablaEmpleados.getSelectionModel().getSelectedItem() != null){
            AppContext.getInstance().set("empSelect", tablaEmpleados.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void accionSeleccionar(ActionEvent event) {
        this.getStage().close();
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        Limpiar();
    }
    
    private void initVista(){
        cbBuscarEmpleado.getItems().clear();
        cbBuscarEmpleado.getItems().add("Por nombre");
        cbBuscarEmpleado.getItems().add("Por area");
        cbBuscarEmpleado.getItems().add("Por cedula");
        tablaEmpleados.getColumns().clear();
        TableColumn<EmpleadosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<EmpleadosDTO, String> colCedula = new TableColumn<>("Cedula");
        colCedula.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCedula()));
        TableColumn<EmpleadosDTO, String> colJefe = new TableColumn<>("Jefe");
        colJefe.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getJefe() == null ? "No tiene" : String.valueOf(p.getValue().getJefe())));
        TableColumn<EmpleadosDTO, String> colrol = new TableColumn<>("Rol");
        colrol.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getRol())));
        TableColumn<EmpleadosDTO, String> colestado = new TableColumn<>("Estado");
        colrol.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().isEstado() ? "Activo" : "Inactivo"));
        tablaEmpleados.getColumns().addAll(colNombre, colCedula, colJefe, colrol, colestado);
    }
    
    private void Limpiar(){
        tablaEmpleados.getItems().clear();
        txtBuscarEmpleados.clear();
        cbBuscarEmpleado.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
        Limpiar();
    }
    
}