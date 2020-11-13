/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.util.FlowController;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class BuscarAreaController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtBuscar;
    @FXML
    private TableView<AreasTrabajosDTO> tvAreas;
    @FXML
    private TableColumn<AreasTrabajosDTO, Long> colId;
    @FXML
    private TableColumn<AreasTrabajosDTO, String> colArea;
    @FXML
    private TableColumn<AreasTrabajosDTO, String> colEstado;
    
    private Map<String,String> modoDesarrollo;
    
    private final AreasTrabajosService service = new AreasTrabajosService();
    AreasTrabajosDTO areaSelec;
    @FXML
    private VBox vbDevelop;
    @FXML
    private ListView<String> lvDevelop;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initVista();
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        Limpiar();
        areaSelec = new AreasTrabajosDTO();
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
    
    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista BuscarArea");
        modoDesarrollo.put("Buscar", "Buscar responde al método accionBuscar");
        modoDesarrollo.put("Seleccionar", "Seleccionar responde al método accionSeleccionar");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDevelop.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDevelop.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    @FXML
    private void accionBuscar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        }else{
            AppContext.getInstance().set("Task", buscarAreaTask());
            FlowController.getInstance().goViewCargar();
        }
    }

    private Task buscarAreaTask(){
        return new Task(){
            @Override
            protected Object call() throws Exception {
                if (!txtBuscar.getText().isEmpty()) {
                    Respuesta res = service.getByNombre(txtBuscar.getText());
                    if (res.getEstado()) {
                        tvAreas.getItems().clear();
                        tvAreas.getItems().addAll((List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos"));
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Areas", res.getMensaje());
                    }
                }
                return true;
            }
        };
    }
    
    @FXML
    private void accionTabla(MouseEvent event) {
        areaSelec = tvAreas.getSelectionModel().getSelectedItem();
        if (tvAreas.getSelectionModel().getSelectedItem() != null) {
            AppContext.getInstance().set("Area", tvAreas.getSelectionModel().getSelectedItem());
        }
    }

    @FXML
    private void accionSeleccionar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Seleccionar"));
        }else{
            if (areaSelec.getNombre() != null) {
                if (areaSelec.isEstado()) {
                    this.closeWindow();
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Inactivo", "El dato está inactivo, no puede realizar más acciones con dicha información");
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar el área de trabajo");
            }
        }
    }

    private void initVista() {
        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colArea.setCellValueFactory(new PropertyValueFactory("nombre"));
        colEstado.setCellValueFactory(per -> {
            String estadoString;
            if (per.getValue().isEstado()) {
                estadoString = "Activo";
            } else {
                estadoString = "Inactivo";
            }
            return new ReadOnlyStringWrapper(estadoString);
        });
    }

    private void Limpiar() {
        txtBuscar.clear();
        tvAreas.getItems().clear();
    }

    @Override
    public void cargarTema() {
    }

}
