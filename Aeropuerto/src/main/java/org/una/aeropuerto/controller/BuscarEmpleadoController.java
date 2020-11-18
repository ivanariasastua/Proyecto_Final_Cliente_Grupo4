/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.UserAuthenticated;

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
    private Map<String, String> modoDesarrollo;
    @FXML
    private VBox vbDevelop;
    @FXML
    private ListView<String> lvDevelop;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initVista();
        service = new EmpleadosService();
        datosModoDesarrollo();
    }

    @FXML
    private void accionBuscarEmpleado(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        } else {
            if (cbBuscarEmpleado.getSelectionModel().getSelectedItem() != null && !txtBuscarEmpleados.getText().isEmpty()) {
                tablaEmpleados.getItems().clear();
                AppContext.getInstance().set("Task", TaskFiltrarEmpleado());
                FlowController.getInstance().goViewCargar();
            }
        }
    }

    @FXML
    private void accionTablaEmpleados(MouseEvent event) {
        if (tablaEmpleados.getSelectionModel().getSelectedItem() != null) {
            if (tablaEmpleados.getSelectionModel().getSelectedItem().isEstado()) {
                AppContext.getInstance().set("empSelect", tablaEmpleados.getSelectionModel().getSelectedItem());
            } else {
                Mensaje.show(Alert.AlertType.INFORMATION, "Seleccionar Empleados", "Los empleados inactivo no se pueden seleccionar");
            }
        }
    }

    @FXML
    private void accionSeleccionar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Seleccionar"));
        }else{
           this.getStage().close(); 
        }
        
    }

    @FXML
    private void accionLimpiar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Limpiar"));
        }else{
            Limpiar();
        }
        
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista BuscarEmpleado");
        modoDesarrollo.put("Limpiar", "Limpiar responde al método accionLimpiar");
        modoDesarrollo.put("Seleccionar", "Seleccionar responde al método accionSeleccionar");
        modoDesarrollo.put("Buscar", "Buscar: responde al método accionBuscarEmpleado");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDevelop.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDevelop.getItems().add(modoDesarrollo.get(info));
        }
    }

    public Task TaskFiltrarEmpleado() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                String var = cbBuscarEmpleado.getSelectionModel().getSelectedItem();
                Respuesta res;
                updateMessage("Filtrando empleados.");
                updateProgress(1, 3);
                if (var.equals("Por nombre")) {
                    res = service.getByNombre(txtBuscarEmpleados.getText());
                } else if (var.equals("Por area")) {
                    res = service.getByArea(txtBuscarEmpleados.getText());
                } else {
                    res = service.getByCedula(txtBuscarEmpleados.getText());
                }
                updateMessage("Filtrando empleados..");
                updateProgress(2, 3);
                Platform.runLater(() -> {
                    if (res.getEstado()) {
                        tablaEmpleados.getItems().addAll((List<EmpleadosDTO>) res.getResultado("Empleados"));
                    } else {
                        System.out.println(res.getMensajeInterno());
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Empleados", res.getMensaje());
                    }
                });
                updateMessage("Filtrando empleados...");
                updateProgress(3, 3);
                return true;
            }
        };
    }

    private void initVista() {
        cbBuscarEmpleado.getItems().clear();
        cbBuscarEmpleado.getItems().add("Por nombre");
        cbBuscarEmpleado.getItems().add("Por area");
        cbBuscarEmpleado.getItems().add("Por cedula");
        tablaEmpleados.getColumns().clear();
        TableColumn<EmpleadosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<EmpleadosDTO, String> colCedula = new TableColumn<>("Cédula");
        colCedula.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCedula()));
        TableColumn<EmpleadosDTO, String> colJefe = new TableColumn<>("Es jefe");
        colJefe.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getEsJefe() == false ? "No" : "Sí"));
        TableColumn<EmpleadosDTO, String> colrol = new TableColumn<>("Rol");
        colrol.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getRol() == null ? "Sin rol" : String.valueOf(p.getValue().getRol())));
        TableColumn<EmpleadosDTO, String> colestado = new TableColumn<>("Estado");
        colestado.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().isEstado() ? "Activo" : "Inactivo"));
        tablaEmpleados.getColumns().addAll(colNombre, colCedula, colJefe, colrol, colestado);
    }

    private void Limpiar() {
        tablaEmpleados.getItems().clear();
        txtBuscarEmpleados.clear();
        cbBuscarEmpleado.getSelectionModel().clearSelection();
    }

    @Override
    public void initialize() {
        Limpiar();
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            vbDevelop.setPrefWidth(250);
            lvDevelop.setPrefWidth(250);
            vbDevelop.setVisible(true);
            lvDevelop.setVisible(true);
            asignarInfoModoDesarrollo();
        }else{
            vbDevelop.setPrefWidth(0);
            lvDevelop.setPrefWidth(0);
            vbDevelop.setVisible(false);
            lvDevelop.setVisible(false);
        }
    }

}
