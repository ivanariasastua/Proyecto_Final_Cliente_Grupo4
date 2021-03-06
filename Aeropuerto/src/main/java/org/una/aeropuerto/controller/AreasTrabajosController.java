/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
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

    private Map<String, String> modoDesarrollo;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    private AreasTrabajosService areasService = new AreasTrabajosService();
    private AreasTrabajosDTO areaSeleccionada = new AreasTrabajosDTO();
    boolean areaSelec = false;
    @FXML
    private TableView tablaAreasTrabajo;
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
    private ListView<String> lvDesarrollo;
    private Controller controller;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroAreas.setItems(filtro);
        clickTablas();
        addListener();
        datosModoDesarrollo();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
    }

    @Override
    public void initialize() {
        tablaAreasTrabajo.getItems().clear();
        llenarColumnas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeigth(contenedor.getHeight());
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            asignarInfoModoDesarrollo();
        }
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista AreasTrabajos");
        modoDesarrollo.put("Inactivar", "Inactivar responde al método actInactivarAreaT");
        modoDesarrollo.put("Buscar", "Buscar responde al método actBuscarAreasTrabajos");
        modoDesarrollo.put("Crear", "Crear responde al método actMantAreasTrabajo");
    }

    public void asignarInfoModoDesarrollo() {
        lvDesarrollo.getItems().clear();
        for (String info : modoDesarrollo.keySet()) {
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }

    public void llenarColumnas() {
        tablaAreasTrabajo.getColumns().clear();
        TableColumn<AreasTrabajosDTO, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<AreasTrabajosDTO, String> colDesc = new TableColumn<>("Descripcion");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<AreasTrabajosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        TableColumn<AreasTrabajosDTO, String> colJefe = new TableColumn<>("Jefe del área");
        colJefe.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getJefe() == null ? "Sin jefe" : p.getValue().getJefe().getNombre()));
        tablaAreasTrabajo.getColumns().addAll(colNombre, colDesc, colEstado,colJefe);
    }

    @FXML
    private void actBuscarAreasTrabajos(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        } else {
            AppContext.getInstance().set("Task", buscarAreaTask());
            FlowController.getInstance().goViewCargar();
        }
    }

    private Task buscarAreaTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                if (cbxFiltroAreas.getValue() == null) {
                    Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cúal tipo desea filtrar la información");
                } else {
                    if (!txtBuscarAreasT.getText().isEmpty()) {
                        Platform.runLater(() -> {
                            llenarColumnas();
                        });
                        tablaAreasTrabajo.getItems().clear();
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
                return true;
            }
        };
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
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar"));
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
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", "El área de trabajo: " + areaSeleccionada.getNombre() + " ha sido inactivada");
                        AreasTrabajosDTO act = (AreasTrabajosDTO) res.getResultado("Areas_Trabajos");
                        actualizarDatos(tablaAreasTrabajo.getItems(), act);
                        Platform.runLater(() -> {
                            tablaAreasTrabajo.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", res.getMensaje());
                    }
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Inactivar Área", "No ha seleccionado ninguna área de trabajo");
            }
        }
    }

    private void actualizarDatos(List<AreasTrabajosDTO> list, AreasTrabajosDTO area) {
        for (AreasTrabajosDTO a : list) {
            if (a.getId().equals(area.getId())) {
                a.setDescripcion(area.getDescripcion());
                a.setEstado(area.isEstado());
                a.setNombre(area.getNombre());
            }
        }
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
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Crear"));
            FlowController.getInstance().goViewInNoResizableWindow("MantAreasTrabajo", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("MantAreasTrabajo", false, StageStyle.DECORATED);
        }
    }

}
