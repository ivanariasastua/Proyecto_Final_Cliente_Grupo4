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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class BuscarCategoriasController extends Controller implements Initializable {

    @FXML
    private TableView tablaCategorias;
    @FXML
    private JFXTextField txtBuscar;

    private Map<String,String> modoDesarrollo;
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    List<IncidentesCategoriasDTO> listCategorias;
    IncidentesCategoriasDTO catSelect;
    @FXML
    private VBox vbDevelop;
    @FXML
    private ListView<String> lvDevelop;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        Limpiar();
        cargarColumnas();
        listCategorias = new ArrayList<>();
        catSelect = new IncidentesCategoriasDTO();
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
        modoDesarrollo.put("Vista", "Nombre de la vista BuscarCategorias");
        modoDesarrollo.put("Buscar", "Buscar responde al método actBuscar");
        modoDesarrollo.put("Seleccionar", "Seleccionar responde al método actSeleccionar");
        modoDesarrollo.put("Cancelar", "Cancelar responde al método actCancelar");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDevelop.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDevelop.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    @FXML
    private void actBuscar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        }else{
            tablaCategorias.getItems().clear();
            if (txtBuscar.getText() != null) {
                cargarColumnas();
                Respuesta res = categoriaService.getByNombre(txtBuscar.getText());
                if (res.getEstado()) {
                    listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
                    if (listCategorias != null) {
                        ObservableList items = FXCollections.observableArrayList(listCategorias);
                        tablaCategorias.setItems(items);
                    }
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Categorías", res.getMensaje());
                }
            }
        }
    }

    public String estado(boolean estad) {
        if (estad == true) {
            return "Activo";
        } else {
            return "Inactivo";
        }
    }

    public void cargarColumnas() {
        tablaCategorias.getColumns().clear();
        TableColumn<IncidentesCategoriasDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        TableColumn<IncidentesCategoriasDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaCategorias.getColumns().addAll(colNombre, colDesc, colEst);
    }

    @FXML
    private void actSeleccionar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Seleccionar"));
        }else{
            if (catSelect.getNombre() != null) {
                if (catSelect.isEstado()) {
                    this.closeWindow();
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Inactivo", "El dato está inactivo, no puede realizar más acciones con dicha información");
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar la categoría");
            }
        }
    }

    public void Limpiar() {
        txtBuscar.clear();
        tablaCategorias.getItems().clear();
    }

    @FXML
    private void actClickTabla(MouseEvent event) {
        catSelect = (IncidentesCategoriasDTO) tablaCategorias.getSelectionModel().getSelectedItem();
        if (tablaCategorias.getSelectionModel().getSelectedItem() != null) {
            AppContext.getInstance().set("CategoriaSup", tablaCategorias.getSelectionModel().getSelectedItem());
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar la categoría");
        }
    }

    @FXML
    private void actCancelar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDevelop.getSelectionModel().select(modoDesarrollo.get("Cancelar"));
        }else{
           this.closeWindow(); 
        }
        
    }

    @Override
    public void cargarTema() {
    }

}
