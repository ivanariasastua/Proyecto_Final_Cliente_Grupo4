/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.util.Mensaje;

public class HabilitarUsuariosController extends Controller implements Initializable{

    @FXML
    private TableView<EmpleadosDTO> tvEmpleados;
    @FXML
    private TableColumn<EmpleadosDTO, String> tcNombre;
    @FXML
    private TableColumn<EmpleadosDTO, String> tcCedula;
    @FXML
    private TableColumn<EmpleadosDTO, String> tcJefe;
    @FXML
    private TableColumn<EmpleadosDTO, String> tcRol;
    @FXML
    private TableColumn<EmpleadosDTO, String> tcEstado;
    @FXML
    private ComboBox<RolesDTO> cbFiltro;
    
    private EmpleadosService empService;
    private RolesService rolService;
    private List<EmpleadosDTO> empleadosSeleccionados;
    
    @Override
    public void cargarTema() {
        
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        rolService = new RolesService();
        empService = new EmpleadosService();
        initTableView();
        initComboBoxRoles();
    }
    
    public void initTableView(){
        tvEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tcNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        tcCedula.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCedula()));
        tcJefe.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getJefe() == null ? "No tiene" : String.valueOf(p.getValue().getJefe())));
        tcRol.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getRol() )));
        tcEstado.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().isEstado() ? "Activo" : "Inactivo"));
    }
    
    public void initComboBoxRoles(){
        Respuesta res = rolService.getAll();
        List<RolesDTO> roles = (List<RolesDTO>) res.getResultado("Roles");
        cbFiltro.getItems().clear();
        cbFiltro.getItems().addAll(roles);
    }
    
    public Task TaskFiltradoEmpleadoNoAprobado(){
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Respuesta res;
                RolesDTO rol = cbFiltro.getValue();
                updateMessage("Filtrando empleados.");
                updateProgress(1, 3);
                res = empService.getNoAprobadosbyRol(rol.getId());
                updateMessage("Filtrando empleados..");
                updateProgress(2, 3);
                Platform.runLater( () -> {
                    if(res.getEstado()){
                        tvEmpleados.getItems().addAll((List<EmpleadosDTO>)res.getResultado("Empleados"));
                    }else{
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
    
    @FXML
    private void actFiltrarEmpleados(ActionEvent event) {
        if(cbFiltro.getSelectionModel().getSelectedItem() != null){
            tvEmpleados.getItems().clear();
            Mensaje.showProgressDialog(TaskFiltradoEmpleadoNoAprobado(), "Buscar Empleados", "Filtrando empleados");
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "No es posible filtrar", "Elija un rol para filtrar los empleados no aprobados");
        }
    }

    @FXML
    private void actHabilitarEmpleado(ActionEvent event) {
        Respuesta res;
        String empAprobados = "";
        empleadosSeleccionados = tvEmpleados.getSelectionModel().getSelectedItems();
        if(!empleadosSeleccionados.isEmpty()){
            for(EmpleadosDTO empleado : empleadosSeleccionados){
                res = empService.Aprobar(empleado.getId());
                if(res.getEstado()){
                    empAprobados += empleado.getNombre()+" "+empleado.getCedula()+"\n";
                    tvEmpleados.getItems().remove(empleado);
                }else{
                    System.out.println(empleado.getId());
                    System.out.println("error");
                    System.out.println(res.getMensaje());
                    System.out.println(res.getMensajeInterno());
                }
            }
            Mensaje.show(Alert.AlertType.CONFIRMATION, "Empleados Aprobados", empAprobados);
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Datos no seleccionados", "Seleccionar empleados de la tabla para aprobarlos.");
        }
    }
}
