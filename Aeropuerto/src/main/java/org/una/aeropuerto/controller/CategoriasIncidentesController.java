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
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.una.aeropuerto.App;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
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
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private VBox vbContTabla;
    @FXML
    private GridPane gpCont;
    @FXML
    private BorderPane bpRoot;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroCategorias.setItems(filtro);
        clickTabla();
        addListener();
    }

    @Override
    public void initialize() {
        tablaCategorias.getItems().clear();
        cargarColumnas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeigth(contenedor.getHeight());
    }

    public void cargarVista(IncidentesCategoriasDTO incidente) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("MantCategorias.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        MantCategoriasController editar = loader.getController();
        editar.cargarDatos(incidente);
    }

    public void clickTabla() {
        tablaCategorias.setRowFactory(tv -> {
            TableRow<IncidentesCategoriasDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    catSelec = true;
                    categoriaSelec = row.getItem();
                }
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    try {
                        categoriaSelec = row.getItem();
                        cargarVista(categoriaSelec);
                        catSelec = false;
                    } catch (IOException ex) {
                    }

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
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
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
                        catSelec = false;
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Categoría", res.getMensaje());
                    }
                }
            }
        }
    }

    @Override
    public void cargarTema() {
    }

    private void addListener() {
        System.out.println("Entro listener");
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

    @FXML
    private void actCrearCategorias(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            FlowController.getInstance().goViewInNoResizableWindow("MantCategorias", false, StageStyle.UTILITY);
        }
    }
}
