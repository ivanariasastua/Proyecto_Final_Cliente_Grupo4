/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class CategoriasIncidentesController extends Controller implements Initializable {

    @FXML
    private TableView tablaCategorias;
    @FXML
    private JFXComboBox<IncidentesCategoriasDTO> cbxCategoriasSuperiores;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextArea txtDescripcion;

    private IncidentesCategoriasDTO categoriaDTO = new IncidentesCategoriasDTO();
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    List<IncidentesCategoriasDTO> listCategorias = new ArrayList<>();
    boolean catSelec = false;
    IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cargarCategorias();
    }

    @Override
    public void initialize() {
        Respuesta res = categoriaService.getAll();
        listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
        if (listCategorias != null) {
            ObservableList items = FXCollections.observableArrayList(listCategorias);
            cbxCategoriasSuperiores.setItems(items);
        }
    }

    public void clickTabla() {
        tablaCategorias.setRowFactory(tv -> {
            TableRow<IncidentesCategoriasDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    catSelec = true;
                    categoriaSelec = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actGuardarCategorias(ActionEvent event) {
        if (catSelec == true) {
            categoriaDTO.setId(categoriaSelec.getId());
            categoriaDTO.setCategoriaSuperior(cbxCategoriasSuperiores.getValue());
            categoriaDTO.setDescripcion(txtDescripcion.getText());
            categoriaDTO.setNombre(txtNombre.getText());
            Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaDTO);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoria guardada correctamente");
                cargarCategorias();

            }
        } else {
            if (txtNombre.getText() == null || txtNombre.getText().isEmpty()) {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo de nombre es obligatorio");
            } else {
                categoriaDTO = new IncidentesCategoriasDTO();
                categoriaDTO.setCategoriaSuperior(cbxCategoriasSuperiores.getValue());
                categoriaDTO.setDescripcion(txtDescripcion.getText());
                categoriaDTO.setNombre(txtNombre.getText());
                Respuesta res = categoriaService.guardarIncidentesCategorias(categoriaDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoria guardada correctamente");
                    cargarCategorias();
                }
            }
        }
    }

    public void cargarCategorias() {
        tablaCategorias.getColumns().clear();
        TableColumn<IncidentesCategoriasDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<IncidentesCategoriasDTO, String> colCat = new TableColumn<>("Categoria superior");
        colCat.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCategoriaSuperior())));
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        tablaCategorias.getColumns().addAll(colNombre, colCat, colDesc);
        Respuesta res = categoriaService.getAll();
        listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
        if (listCategorias != null) {
            ObservableList items = FXCollections.observableArrayList(listCategorias);
            tablaCategorias.setItems(items);
        } else {
            tablaCategorias.getItems().clear();
        }
    }

    @FXML
    private void actBuscarCategorias(ActionEvent event) {
    }

    @FXML
    private void actLimpiarCampos(ActionEvent event) {
        txtDescripcion.setText(null);
        txtDescripcion.setText(null);
        cbxCategoriasSuperiores.setValue(null);
        catSelec = false;
        categoriaSelec = new IncidentesCategoriasDTO();
    }

    @FXML
    private void actEditarCat(ActionEvent event) {
        if (catSelec == true) {
            txtDescripcion.setText(categoriaSelec.getDescripcion());
            txtNombre.setText(categoriaSelec.getNombre());
            cbxCategoriasSuperiores.setValue(categoriaSelec.getCategoriaSuperior());
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar categoria", "Debe seleccionar una categoria");
        }
    }

}
