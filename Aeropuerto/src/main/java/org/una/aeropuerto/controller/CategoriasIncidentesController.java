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
import java.util.Optional;
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
import javafx.util.Pair;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.service.IncidentesCategoriasService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

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
    IncidentesCategoriasDTO categSuperiorSelec = new IncidentesCategoriasDTO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroCategorias.setItems(filtro);
        clickTabla();
    }

    @Override
    public void initialize() {
        limpiarCampos();
        cargarColumnas();
    }

    public void clickTabla() {
        tablaCategorias.setRowFactory(tv -> {
            TableRow<IncidentesCategoriasDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    catSelec = true;
                    categoriaSelec = row.getItem();
                    txtDescripcion.setText(categoriaSelec.getDescripcion());
                    txtNombre.setText(categoriaSelec.getNombre());
                    if (categoriaSelec.getCategoriaSuperior() != null) {
                        txtCategoriaSuperior.setText(categoriaSelec.getCategoriaSuperior().getNombre());
                    }else{
                        txtCategoriaSuperior.setText(null);
                    }
                }
            });
            return row;
        });
    }

    public boolean validarActivos() {
        if (categoriaSelec.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarCategorias(ActionEvent event) {
        if (catSelec == true) {
            if (validarActivos()) {
                categoriaSelec.setId(categoriaSelec.getId());
                categoriaSelec.setDescripcion(txtDescripcion.getText());
                categoriaSelec.setNombre(txtNombre.getText());
                if (categSuperiorSelec.getNombre() != null && txtCategoriaSuperior.getText() != null) {
                    categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
                }
                Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaSelec);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Categoria editada correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                }
            }
        } else {
            if (txtNombre.getText() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo de nombre es obligatorio");
            } else {
                categoriaDTO = new IncidentesCategoriasDTO();
                categoriaDTO.setDescripcion(txtDescripcion.getText());
                categoriaDTO.setNombre(txtNombre.getText());
                if (categSuperiorSelec.getNombre() != null && txtCategoriaSuperior.getText() != null) {
                    categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
                }
                Respuesta res = categoriaService.guardarIncidentesCategorias(categoriaDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoria guardada correctamente");
                } else {
                    System.out.println(res.getMensajeInterno());
                    Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
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
        colCat.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCategoriaSuperior() == null ? "Sin Categoria superior" : String.valueOf(String.valueOf(p.getValue().getCategoriaSuperior()))));
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        TableColumn<IncidentesCategoriasDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaCategorias.getColumns().addAll(colNombre, colCat, colDesc, colEst);
    }

    @FXML
    private void actBuscarCategorias(ActionEvent event) {
        cargarColumnas();
        tablaCategorias.getItems().clear();
        if (cbxFiltroCategorias.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
        } else {
            Respuesta res;
            if (cbxFiltroCategorias.getValue().equals("Nombre")) {
                res = categoriaService.getByNombre(txtBuscarCateg.getText());
            } else {
                if (txtBuscarCateg.getText().equals("activo") || txtBuscarCateg.getText().equals("Activo")) {
                    res = categoriaService.getByEstado(true);
                } else if (txtBuscarCateg.getText().equals("inactivo") || txtBuscarCateg.getText().equals("Inactivo")) {
                    res = categoriaService.getByEstado(false);
                } else {
                    res = categoriaService.getByNombre("");
                }
            }
            if (res.getEstado()) {
                tablaCategorias.getItems().addAll((List<IncidentesCategoriasDTO>) res.getResultado("Incidentes_Categorias"));
            } else {
                Mensaje.show(Alert.AlertType.ERROR, "Buscar Categorias ", res.getMensaje());
            }
        }
    }

    public void limpiarCampos() {
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtCategoriaSuperior.setText(null);
        catSelec = false;
        categoriaSelec = new IncidentesCategoriasDTO();
        categSuperiorSelec = new IncidentesCategoriasDTO();
    }

    @FXML
    private void actLimpiarCampos(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void actInactivarCateg(ActionEvent event) {
        if (catSelec == true) {
            Boolean puedeInactivar = Boolean.FALSE;
            String cedula = "";
            String codigo = "";
            if(UserAuthenticated.getInstance().isRol("GERENTE")){
                cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                codigo = (String) AppContext.getInstance().get("CodigoGerente");
                puedeInactivar = Boolean.TRUE;
            }else if(UserAuthenticated.getInstance().isRol("GESTOR")) {
                Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Empleado");
                if(result.isPresent()){
                    cedula = result.get().getKey();
                    codigo = result.get().getValue();
                    puedeInactivar = Boolean.TRUE;
                }
            }
            if(puedeInactivar){
                Respuesta res = categoriaService.inactivar(categoriaSelec, categoriaSelec.getId(), cedula, codigo);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Categoría", "La categoría : "+categoriaSelec.getNombre()+" ha sido inactivada");
                }else{
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Categoría", res.getMensaje());
                }
            }
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

    @Override
    public void cargarTema() {
    }

}
