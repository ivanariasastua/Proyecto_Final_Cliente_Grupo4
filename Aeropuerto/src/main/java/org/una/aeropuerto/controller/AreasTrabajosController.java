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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.una.aeropuerto.App;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
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
public class AreasTrabajosController extends Controller implements Initializable {

    @FXML
    private TableView tablaAreasTrabajo;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    private AreasTrabajosService areasService = new AreasTrabajosService();
    private AreasTrabajosDTO areaSeleccionada = new AreasTrabajosDTO();
    boolean areaSelec = false;
    @FXML
    private JFXTextField txtBuscarAreasT;
    @FXML
    private JFXComboBox<String> cbxFiltroAreas;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private BorderPane bpRoot;
    @FXML
    private GridPane gpCont;
    @FXML
    private VBox vpRoot;
    @FXML
    private ColumnConstraints row;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroAreas.setItems(filtro);
        clickTablas();
        addListener();
    }

    @Override
    public void initialize() {
        tablaAreasTrabajo.getItems().clear();
        llenarColumnas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeigth(contenedor.getHeight());
    }

    public void llenarColumnas() {
        tablaAreasTrabajo.getColumns().clear();
        TableColumn<AreasTrabajosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<AreasTrabajosDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<AreasTrabajosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaAreasTrabajo.getColumns().addAll(colNombre, colDesc, colEstado);
    }

    @FXML
    private void actBuscarAreasTrabajos(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            llenarColumnas();
            tablaAreasTrabajo.getItems().clear();
            if (cbxFiltroAreas.getValue() == null) {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
            } else {
                Respuesta res;
                if (cbxFiltroAreas.getValue().equals("Nombre")) {
                    res = areasService.getByNombre(txtBuscarAreasT.getText());
                } else {
                    if (txtBuscarAreasT.getText().equals("activo") || txtBuscarAreasT.getText().equals("Activo")) {
                        res = areasService.getByEstado(true);
                    } else if (txtBuscarAreasT.getText().equals("inactivo") || txtBuscarAreasT.getText().equals("Inactivo")) {
                        res = areasService.getByEstado(false);
                    } else {
                        res = areasService.getByNombre("");
                    }
                }
                if (res.getEstado()) {
                    tablaAreasTrabajo.getItems().addAll((List<AreasTrabajosDTO>) res.getResultado("Areas_Trabajos"));
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Areas de Trabajos", res.getMensaje());
                }
            }
        }
    }

    public void cargarVista(AreasTrabajosDTO area) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("MantAreasTrabajo.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        MantAreasTrabajoController editar = loader.getController();
        editar.cargarDatos(area);
    }

    public void clickTablas() {
        tablaAreasTrabajo.setRowFactory(tv -> {
            TableRow<AreasTrabajosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    areaSelec = true;
                    areaSeleccionada = row.getItem();
                }
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    try {
                        areaSeleccionada = row.getItem();
                        cargarVista(areaSeleccionada);
                        areaSelec = false;
                    } catch (IOException ex) {
                        Logger.getLogger(AreasTrabajosController.class.getName()).log(Level.SEVERE, null, ex);
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

    @FXML
    private void actInactivarAreaT(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            if (areaSeleccionada != null) {
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "";
                String codigo = "";
                if (UserAuthenticated.getInstance().isRol("GERENTE")) {
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                } else if (UserAuthenticated.getInstance().isRol("GESTOR")) {
                    Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Area de trabajo");
                    if (result.isPresent()) {
                        cedula = result.get().getKey();
                        codigo = result.get().getValue();
                        puedeInactivar = Boolean.TRUE;
                    }
                }
                if (puedeInactivar) {
                    Respuesta res = areasService.inactivar(areaSeleccionada, areaSeleccionada.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", "La área de trabajo: " + areaSeleccionada.getNombre() + " ha sido inactivada");
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", res.getMensaje());
                    }
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Inactivar Área", "No ha seleccionado ninguna área de trabajo");
            }
        }
    }

    @Override
    public void cargarTema() {
    }

    private void addListener() {
        contenedor.widthProperty().addListener(W -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener(H -> {
            adjustHeigth(contenedor.getHeight());
        });
    }

    private void adjustWidth(double ancho) {
        bpRoot.setPrefWidth(ancho);
        vpRoot.setPrefWidth(ancho);
        txtBuscarAreasT.setPrefWidth((ancho / 2) - 209);
        tablaAreasTrabajo.setPrefWidth(ancho - 400);
        gpCont.setPrefWidth(ancho);
    }

    private void adjustHeigth(double altura) {
        bpRoot.setPrefHeight(altura);
        vpRoot.setPrefHeight(altura);
        gpCont.setPrefHeight(altura - 50);
        tablaAreasTrabajo.setPrefHeight(altura - 272);

    }

    @FXML
    private void actMantAreasTrabajo(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {

        } else {
            FlowController.getInstance().goViewInNoResizableWindow("MantAreasTrabajo", false, StageStyle.UTILITY);
        }
    }
}
