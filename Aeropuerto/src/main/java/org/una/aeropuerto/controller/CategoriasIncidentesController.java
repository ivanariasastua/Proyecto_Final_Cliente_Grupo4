/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
    private JFXComboBox<String> cbxFiltroCategorias;
    @FXML
    private JFXTextField txtBuscarCateg;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    private IncidentesCategoriasDTO categoriaDTO = new IncidentesCategoriasDTO();
    private IncidentesCategoriasService categoriaService = new IncidentesCategoriasService();
    List<IncidentesCategoriasDTO> listCategorias = new ArrayList<>();
    boolean catSelec = false;
    IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();
    IncidentesCategoriasDTO categSuperiorSelec = new IncidentesCategoriasDTO();
    private Map<String,String> modoDesarrollo;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private VBox vbContTabla;
    @FXML
    private GridPane gpCont;
    @FXML
    private BorderPane bpRoot;
    @FXML
    private JFXTextField txtcategoriaSuperior;
    @FXML
    private JFXTextField txtNombre;
    @FXML
    private JFXTextArea txtDescripcion;
    @FXML
    private Tab tabCrearEditar;
    @FXML
    private TabPane tabPane;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroCategorias.setItems(filtro);
        clickTabla();
        addListener();
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        tablaCategorias.getItems().clear();
        cargarColumnas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeigth(contenedor.getHeight());
        limpiarCampos();
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista CategoriasIncidentes");
        modoDesarrollo.put("Buscar", "Responde al método actBuscarCategorias");
        modoDesarrollo.put("Inactivar", "Responde al método actInactivarCateg");
        modoDesarrollo.put("Editar", "Responde al método actEditarCategorias");
        modoDesarrollo.put("Buscar Categoría", "Responde al método actBuscarCatSuperior");
        modoDesarrollo.put("Limpiar", "Responde al método actLimpiar");
        modoDesarrollo.put("Guardar", "Responde al método actGuardarCategoria");
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
        TableColumn<IncidentesCategoriasDTO, String> colCat = new TableColumn<>("Categoría superior");
        colCat.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getCategoriaSuperior() == null ? "Sin Categoría superior" : String.valueOf(String.valueOf(p.getValue().getCategoriaSuperior()))));
        TableColumn<IncidentesCategoriasDTO, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getDescripcion())));
        TableColumn<IncidentesCategoriasDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaCategorias.getColumns().addAll(colNombre, colCat, colDesc, colEst);
    }

    @FXML
    private void actBuscarCategorias(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            cargarColumnas();
            tablaCategorias.getItems().clear();
            if (cbxFiltroCategorias.getValue() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cúal tipo desea filtrar la información");
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
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Categorías ", res.getMensaje());
                }
            }
        }
    }

    @FXML
    private void actInactivarCateg(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (catSelec == true) {
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "";
                String codigo = "";
                if (UserAuthenticated.getInstance().isRol("GERENTE")) {
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                } else if (UserAuthenticated.getInstance().isRol("GESTOR")) {
                    Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Empleado");
                    if (result.isPresent()) {
                        cedula = result.get().getKey();
                        codigo = result.get().getValue();
                        puedeInactivar = Boolean.TRUE;
                    }
                }
                if (puedeInactivar) {
                    Respuesta res = categoriaService.inactivar(categoriaSelec, categoriaSelec.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Categoría", "La categoría : " + categoriaSelec.getNombre() + " ha sido inactivada");

                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Categoría", res.getMensaje());
                    }
                }
                catSelec = false;
            }
        }
    }

    @Override
    public void cargarTema() {
    }

    private void addListener() {
        contenedor.widthProperty().addListener(w -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener(h -> {
            adjustHeigth(contenedor.getHeight());
        });
    }

    private void adjustWidth(double ancho) {
        bpRoot.setPrefWidth(ancho);
        vbContTabla.setPrefWidth(ancho);
        gpCont.setPrefWidth(ancho);
        txtBuscarCateg.setPrefWidth((ancho / 2) - (108 + 87));
        tablaCategorias.setPrefWidth(ancho - 430);
    }

    private void adjustHeigth(double altura) {
        bpRoot.setPrefHeight(altura);
        vbContTabla.setPrefHeight(altura);
        gpCont.setPrefHeight(altura);
        tablaCategorias.setPrefHeight(altura - (79 + 104 + 20));
    }

    public void cargarDatos() {
        txtDescripcion.setText(categoriaSelec.getDescripcion());
        txtNombre.setText(categoriaSelec.getNombre());
        if (categoriaSelec.getCategoriaSuperior() != null) {
            txtcategoriaSuperior.setText(categoriaSelec.getCategoriaSuperior().getNombre());
        } else {
            txtcategoriaSuperior.setText("(No tiene Categoría superior)");
        }
    }

    //tab Crear/Editar
    @FXML
    private void actEditarCategorias(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (catSelec) {
                if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                    selectionModel.select(tabCrearEditar);
                    cargarDatos();
                } else {
                    catSelec = false;
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Categoría", "Debe seleccionar una Categoría");
            }
        }
    }

    @FXML
    private void actBuscarCatSuperior(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.UTILITY);
            categSuperiorSelec = (IncidentesCategoriasDTO) AppContext.getInstance().get("CategoriaSup");
            if (categSuperiorSelec != null) {
                txtcategoriaSuperior.setText(categSuperiorSelec.getNombre());
            }
        }
    }

    @FXML
    private void actGuardarCategoria(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (catSelec == true) {
                if (validarActivos()) {
                    categoriaSelec.setId(categoriaSelec.getId());
                    categoriaSelec.setDescripcion(txtDescripcion.getText());
                    categoriaSelec.setNombre(txtNombre.getText());
                    if (categSuperiorSelec.getNombre() != null && txtcategoriaSuperior.getText() != "(No tiene categoria superior)") {
                        categoriaSelec.setCategoriaSuperior(categSuperiorSelec);
                    }
                    Respuesta res = categoriaService.modificarIncidentesCategorias(categoriaSelec.getId(), categoriaSelec);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Categoría editada correctamente");
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
                    if (categSuperiorSelec.getNombre() != null && txtcategoriaSuperior.getText() != null) {
                        categoriaDTO.setCategoriaSuperior(categSuperiorSelec);
                    }
                    Respuesta res = categoriaService.guardarIncidentesCategorias(categoriaDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Categoría guardada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

    public boolean validarActivos() {
        if (categoriaSelec.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actLimpiar(ActionEvent event) {
        limpiarCampos();
    }

    public void limpiarCampos() {
        categSuperiorSelec = new IncidentesCategoriasDTO();
        txtDescripcion.setText(null);
        txtNombre.setText(null);
        txtcategoriaSuperior.setText(null);
        catSelec = false;
        categoriaSelec = new IncidentesCategoriasDTO();
    }
}
