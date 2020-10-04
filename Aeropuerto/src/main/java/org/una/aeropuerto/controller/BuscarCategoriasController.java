/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

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

    
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    List<IncidentesCategoriasDTO> listCategorias;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @Override
    public void initialize() {
        Limpiar();
        listCategorias = new ArrayList<>();
    }

    @FXML
    private void actBuscar(ActionEvent event) {
        if(txtBuscar.getText()!=null){
            cargarColumnas();
            Respuesta res = categoriaService.getByNombre(txtBuscar.getText());
                listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
                if (listCategorias != null) {
                    ObservableList items = FXCollections.observableArrayList(listCategorias);
                    tablaCategorias.setItems(items);
                } else {
                    tablaCategorias.getItems().clear();
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
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        TableColumn<IncidentesCategoriasDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaCategorias.getColumns().addAll(colNombre,colDesc, colEst);
    }

    @FXML
    private void actSeleccionar(ActionEvent event) {
        this.closeWindow();
    }

    public void Limpiar(){
        txtBuscar.clear();
        tablaCategorias.getItems().clear();
    }

    @FXML
    private void actClickTabla(MouseEvent event) {
        if(tablaCategorias.getSelectionModel().getSelectedItem() != null){
            AppContext.getInstance().set("CategoriaSup", tablaCategorias.getSelectionModel().getSelectedItem());
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar dato", "Debe seleccionar la categoria");
        }
    }

    @FXML
    private void actCancelar(ActionEvent event) {
        this.closeWindow();
    }
    
    
}