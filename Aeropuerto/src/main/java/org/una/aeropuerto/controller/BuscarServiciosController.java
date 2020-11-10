/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class BuscarServiciosController extends Controller implements Initializable {

    @FXML
    private TableView tabla;
    @FXML
    private JFXTextField txtBuscar;

    ServiciosDTO servicSelec;
    ServiciosService servService = new ServiciosService();
    List<ServiciosDTO> listServ;
    Map<String,String> modoDesarrollo;
    @FXML
    private VBox vbDevelop;
    @FXML
    private ListView<String> lvDevelop;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarColumnas();
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        tabla.getItems().clear();
        servicSelec = new ServiciosDTO();
        listServ = new ArrayList<>();
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
        modoDesarrollo.put("Vista", "Nombre de la vista BuscarServicios");
        modoDesarrollo.put("Buscar", "Buscar responde al método actBuscar");
        modoDesarrollo.put("Seleccionar", "Seleccionar responde al método actSeleccionarServicio");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDevelop.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDevelop.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    public void cargarColumnas() {
        tabla.getColumns().clear();
        TableColumn<ServiciosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getNombre())));
        TableColumn<ServiciosDTO, String> colDescrip = new TableColumn<>("Descripción");
        colDescrip.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<ServiciosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tabla.getColumns().addAll(colNombre,colDescrip,colEstado);
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }

    @FXML
    private void actBuscar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        }else{
            tabla.getItems().clear();
            if (txtBuscar.getText() != null) {
                cargarColumnas();
                Respuesta res = servService.getByNombre(txtBuscar.getText());
                if (res.getEstado()) {
                    tabla.getItems().addAll((List<ServiciosDTO>) res.getResultado("Servicios"));
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Servicios", res.getMensaje());
                }
            }
        }
    }

    @FXML
    private void actSeleccionarServicio(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Seleccionar"));
        }else{
            if (servicSelec.getNombre() != null) {
                if (servicSelec.isEstado()) {
                    this.closeWindow();
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Inactivo", "El dato está inactivo, no puede realizar más acciones con dicha información");
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar un servicio");
            }
        }
    }

    @FXML
    private void actClickTabla(MouseEvent event) {
        servicSelec = (ServiciosDTO) tabla.getSelectionModel().getSelectedItem();
        if (servicSelec != null) {
            AppContext.getInstance().set("servSelect", servicSelec);
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar un servicio");
        }
    }

    @Override
    public void cargarTema() {
    }

}
