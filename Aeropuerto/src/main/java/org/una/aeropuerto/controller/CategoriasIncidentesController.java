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
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
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
    private JFXTextField txtCategoriaSuperior;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private JFXComboBox<String> cbxFiltroCategorias;
    @FXML
    private JFXTextField txtBuscarCateg;

    private IncidentesCategoriasDTO categoriaDTO = new IncidentesCategoriasDTO();
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    List<IncidentesCategoriasDTO> listCategorias = new ArrayList<>();
    boolean catSelec = false;
    IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();
    IncidentesCategoriasDTO categSuperiorSelec;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroCategorias.setItems(filtro);
        clickTabla();
    }

    @Override
    public void initialize() {
        categSuperiorSelec = new IncidentesCategoriasDTO();
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
            categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
            categoriaDTO.setDescripcion(txtDescripcion.getText());
            categoriaDTO.setNombre(txtNombre.getText());
            Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaDTO);
            if (res.getEstado()) {
                Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Categoria editada correctamente");
            }
        } else {
            if (txtNombre.getText() == null || txtNombre.getText().isEmpty()) {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo de nombre es obligatorio");
            } else {
                categoriaDTO = new IncidentesCategoriasDTO();
                categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
                categoriaDTO.setDescripcion(txtDescripcion.getText());
                categoriaDTO.setNombre(txtNombre.getText());
                Respuesta res = categoriaService.guardarIncidentesCategorias(categoriaDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoria guardada correctamente");
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
        TableColumn<IncidentesCategoriasDTO, String> colCat = new TableColumn<>("Categoria superior");
        colCat.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCategoriaSuperior())));
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        TableColumn<IncidentesCategoriasDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaCategorias.getColumns().addAll(colNombre, colCat, colDesc, colEst);
    }

    @FXML
    private void actBuscarCategorias(ActionEvent event) {
        cargarColumnas();
        if (cbxFiltroCategorias.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
        } else {
            if (cbxFiltroCategorias.getValue().equals("Nombre")) {
                Respuesta res = categoriaService.getByNombre(txtBuscarCateg.getText());
                listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
                if (listCategorias != null) {
                    ObservableList items = FXCollections.observableArrayList(listCategorias);
                    tablaCategorias.setItems(items);
                } else {
                    tablaCategorias.getItems().clear();
                }
            } else {
                if (txtBuscarCateg.getText().contains("activo") || txtBuscarCateg.getText().contains("Activo")) {
                    Respuesta res = categoriaService.getByEstado(true);
                    listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
                    if (listCategorias != null) {
                        ObservableList items = FXCollections.observableArrayList(listCategorias);
                        tablaCategorias.setItems(items);
                    } else {
                        tablaCategorias.getItems().clear();
                    }
                } else if (txtBuscarCateg.getText().contains("inactivo") || txtBuscarCateg.getText().contains("Inactivo")) {
                    Respuesta res = categoriaService.getByEstado(false);
                    listCategorias = (List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias");
                    if (listCategorias != null) {
                        ObservableList items = FXCollections.observableArrayList(listCategorias);
                        tablaCategorias.setItems(items);
                    } else {
                        tablaCategorias.getItems().clear();
                    }
                } else {
                    tablaCategorias.getItems().clear();
                }
            }
        }
    }

    @FXML
    private void actLimpiarCampos(ActionEvent event) {
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtCategoriaSuperior.setText(null);
        catSelec = false;
        categoriaSelec = new IncidentesCategoriasDTO();
        categSuperiorSelec= new IncidentesCategoriasDTO();
    }

    @FXML
    private void actEditarCat(ActionEvent event) {
        if (catSelec == true) {
            if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                txtDescripcion.setText(categoriaSelec.getDescripcion());
                txtNombre.setText(categoriaSelec.getNombre());
                if(categoriaSelec.getCategoriaSuperior()!=null){
                    txtCategoriaSuperior.setText(categoriaSelec.getCategoriaSuperior().getNombre());
                }
            } else {
                catSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar categoria", "Debe seleccionar una categoria");
        }
    }

    @FXML
    private void actInactivarCateg(ActionEvent event) {
        if (catSelec == true) {
            if (Mensaje.showConfirmation("Inactivar ", null, "Seguro que desea inactivar la información?")) {
                categoriaSelec.setEstado(false);
                Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaSelec);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Información inactivada correctamente");
                    catSelec = false;
                }
            } else {
                catSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar información", "Debe seleccionar información de la tabla");
        }
    }

    @FXML
    private void actBuscarCategoriaSuperior(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.UTILITY);
        categSuperiorSelec = (IncidentesCategoriasDTO) AppContext.getInstance().get("CategoriaSup");
        if (categSuperiorSelec != null) {
            txtCategoriaSuperior.setText(categSuperiorSelec.getNombre());
        }
    }

}
