/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.service.EmpleadosService;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.dto.RolesDTO;
import org.una.aeropuerto.service.RolesService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.util.FlowController;

public class HabilitarUsuariosController extends Controller implements Initializable{

    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
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
    private JFXComboBox<RolesDTO> cbFiltro;
    
    private EmpleadosService empService;
    private RolesService rolService;
    private List<EmpleadosDTO> empleadosSeleccionados;
    private Map<String,String> modoDesarrollo;
    private ListView<String> lvDesarrollo;
    @FXML
    private BorderPane bpRoot;

    @Override
    public void initialize() {
        adjustWidth(contenedor.getWidth());
        adjustHeight(contenedor.getHeight());
        asignarInfoModoDesarrollo();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        rolService = new RolesService();
        empService = new EmpleadosService();
        initTableView();
        initComboBoxRoles();
        addListener();
        datosModoDesarrollo();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
    }
    
    public void initTableView(){
        tvEmpleados.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tcNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        tcCedula.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCedula()));
        tcJefe.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getEsJefe() == false ? "No" : "Sí"));
        tcRol.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getRol() )));
        tcEstado.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().isEstado() ? "Activo" : "Inactivo"));
    }
    
    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la ventana HabilitarUsuarios");
        modoDesarrollo.put("Buscar", "Buscar responde al método actFiltrarEmpleados");
        modoDesarrollo.put("Habilitar", "Habilitar responde al método actHabilitarEmpleado");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDesarrollo.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
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
                res = empService.getNoAprobadosbyRol(rol.getId());
                Platform.runLater( () -> {
                    if(res.getEstado()){
                        tvEmpleados.getItems().addAll((List<EmpleadosDTO>)res.getResultado("Empleados"));
                    }else{
                        System.out.println(res.getMensajeInterno());
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Empleados", res.getMensaje());
                    }
                });
                return true;
            }
        };
    }
    
    @FXML
    private void actFiltrarEmpleados(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        }else{
            if(cbFiltro.getSelectionModel().getSelectedItem() != null){
                tvEmpleados.getItems().clear();
                AppContext.getInstance().set("Task", TaskFiltradoEmpleadoNoAprobado());
                FlowController.getInstance().goViewCargar();
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "No es posible filtrar", "Elija un rol para filtrar los empleados no aprobados");
            }
        }
    }

    @FXML
    private void actHabilitarEmpleado(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Habilitar"));
        }else{
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
    
    private void addListener(){
        contenedor.widthProperty().addListener( w -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener( h -> {
            adjustHeight(contenedor.getHeight());
        });
    }
    
    private void adjustWidth(double ancho){
        bpRoot.setPrefWidth(ancho);
        tvEmpleados.setPrefWidth(ancho);
    }

    private void adjustHeight(double alto){
        bpRoot.setPrefHeight(alto);
        tvEmpleados.setPrefHeight(alto-223);
    }
}
