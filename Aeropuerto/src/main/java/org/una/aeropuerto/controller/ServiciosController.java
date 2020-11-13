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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import org.una.aeropuerto.App;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.dto.ServiciosPreciosDTO;
import org.una.aeropuerto.service.ServiciosPreciosService;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Formato;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author cordo
 */
public class ServiciosController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtBuscarServicio;
    private ServiciosService servService;
    @FXML
    private TableView tablaServicios;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    ServiciosDTO servicSeleccionado = new ServiciosDTO();
    boolean servSelec = false;
    boolean precioSelec = false;
    @FXML
    private JFXComboBox<String> cbxFiltroServicios;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabServicios;
    @FXML
    private Tab tabPrecios;
    @FXML
    private TableView tablaPrecios;
    @FXML
    private Label txtServicioSelec;
    @FXML
    private JFXTextField txtCostoServico;

    ServiciosPreciosService precioService;
    ServiciosPreciosDTO preciosDto = new ServiciosPreciosDTO();
    ServiciosPreciosDTO precioSelect = new ServiciosPreciosDTO();
    Map<String, String> modoDesarrollo;
    private ListView<String> lvDesarrollo;

    @FXML
    private JFXButton btnGuardar;
    @FXML
    private BorderPane bpRoot;
    @FXML
    private VBox vbRoot;
    @FXML
    private GridPane gpTabla;
    @FXML
    private GridPane gpServicio;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtCostoServico.setTextFormatter(Formato.getInstance().twoDecimalFormat());
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroServicios.setItems(filtro);
        clickTabla();
        cargarColumnasPrecios();
        cargarColumnas();
        addListener();
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        tablaServicios.getItems().clear();
        servService = new ServiciosService();
        precioService = new ServiciosPreciosService();
        limpiarPrecios();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeight(contenedor.getHeight());
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            asignarInfoModoDesarrollo();
        }
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista Servicios");
        modoDesarrollo.put("Buscar Servicio", "Buscar responde al método actBuscarServicio");
        modoDesarrollo.put("Inactivar Servicio", "Inactivar responde al método actInactivarServicio");
        modoDesarrollo.put("Crear Servicio", "Crear responde al método actCrearServicios");
        modoDesarrollo.put("Inactivar Precio", "Inactivar responde al método actInactivarPrecios");
        modoDesarrollo.put("Limpiar Precio", "Limpiar responde al método actLimpiarCamposPrecio");
        modoDesarrollo.put("Guardar Precio", "Guardar responde al método actGuardarPrecio");
    }

    private void asignarInfoModoDesarrollo() {
        lvDesarrollo.getItems().clear();
        for (String info : modoDesarrollo.keySet()) {
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }

    public void cargarVista(ServiciosDTO servicio) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("MantServicios.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();
        MantServiciosController editar = loader.getController();
        editar.cargarDatos(servicio);
    }

    public void clickTabla() {
        tablaServicios.setRowFactory(tv -> {
            TableRow<ServiciosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    servSelec = true;
                    servicSeleccionado = row.getItem();
                }
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                    try {
                        servicSeleccionado = row.getItem();
                        cargarVista(servicSeleccionado);
                        servSelec = false;
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
        tablaServicios.getColumns().clear();
        TableColumn<ServiciosDTO, String> colNomb = new TableColumn<>("Nombre");
        colNomb.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getNombre()));
        TableColumn<ServiciosDTO, String> colDescrip = new TableColumn<>("Descripción");
        colDescrip.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<ServiciosDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaServicios.getColumns().addAll(colNomb, colDescrip, colEst);
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Servicio"));
        } else {
            AppContext.getInstance().set("Task", buscarServicioTask());
            FlowController.getInstance().goViewCargar();
        }
    }
    
    private Task buscarServicioTask(){
        return new Task(){
            @Override
            protected Object call() throws Exception {
                if (!txtBuscarServicio.getText().isEmpty()) {
                    Platform.runLater(() -> {
                        cargarColumnas();
                    });
                    tablaServicios.getItems().clear();
                    if (cbxFiltroServicios.getValue() == null) {
                        Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cúal tipo desea filtrar la información");
                    } else {
                        Respuesta res;
                        if (cbxFiltroServicios.getValue().equals("Nombre")) {
                            res = servService.getByNombre(txtBuscarServicio.getText());
                        } else {
                            if (txtBuscarServicio.getText().equals("activo") || txtBuscarServicio.getText().equals("Activo")) {
                                res = servService.getByEstado(true);
                            } else if (txtBuscarServicio.getText().equals("inactivo") || txtBuscarServicio.getText().equals("Inactivo")) {
                                res = servService.getByEstado(false);
                            } else {
                                res = servService.getByNombre("");
                            }
                        }
                        if (res.getEstado()) {
                            tablaServicios.getItems().addAll((List<ServiciosDTO>) res.getResultado("Servicios"));
                        } else {
                            Mensaje.show(Alert.AlertType.ERROR, "Buscar Servicios", res.getMensaje());
                        }
                    }
                }
                return true;
            }
            
        };
    }

    @FXML
    private void actInactivarServicio(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar Servicio"));
        } else {
            if (servicSeleccionado != null) {
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "", codigo = "";
                if (UserAuthenticated.getInstance().isRol("GERENTE")) {
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                } else if (UserAuthenticated.getInstance().isRol("GESTOR")) {
                    Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Servicio");
                    if (result.isPresent()) {
                        cedula = result.get().getKey();
                        codigo = result.get().getValue();
                        puedeInactivar = Boolean.TRUE;
                    }
                }
                if (puedeInactivar) {
                    Respuesta res = servService.inactivar(servicSeleccionado, servicSeleccionado.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar servicio", "El servicio ha sido inactivado");
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar servicio", res.getMensaje());
                    }
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Inactivar servicio", "No ha seleccionado ningún servicio");
            }
        }
    }

    @FXML
    private void actCrearServicios(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Crear Servicio"));
            FlowController.getInstance().goViewInNoResizableWindow("MantServicios", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("MantServicios", false, StageStyle.DECORATED);
        }
    }

    @FXML
    private void actGuardarPrecio(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Guardar Precio"));
        } else {
            if (precioSelec == true) {
                if (validarPreciosActivos()) {
                    precioSelect.setId(precioSelect.getId());
                    precioSelect.setCosto(Float.valueOf(txtCostoServico.getText()));
                    precioSelect.setServicio(servicSeleccionado);
                    Respuesta resp = precioService.modificarPrecioServicio(precioSelect.getId(), precioSelect);
                    respuesta(resp);
                }
            } else {
                if (txtCostoServico.getText() != null || !txtCostoServico.getText().isEmpty()) {
                    preciosDto = new ServiciosPreciosDTO();
                    preciosDto.setCosto(Float.valueOf(txtCostoServico.getText()));
                    preciosDto.setServicio(servicSeleccionado);
                    Respuesta resp = precioService.guardarPrecioServicio(preciosDto);
                    respuesta(resp);
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Precio es obligatorio");
                }
            }
        }
    }

    private void respuesta(Respuesta resp) {
        if (resp.getEstado()) {
            cargarPrecios();
            Mensaje.show(Alert.AlertType.INFORMATION, "Guardado Correctamente", "Precio guardado correctamente");
        } else {
            Mensaje.show(Alert.AlertType.ERROR, "Error", resp.getMensaje());
        }
    }

    @FXML
    private void actPane(MouseEvent event) {
        if (!UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            if (tabPrecios.isSelected() && servSelec == false) {
                SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                selectionModel.select(tabServicios);
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
            } else if (tabPrecios.isSelected() && servSelec == true) {
                tablaPrecios.getItems().clear();
                txtServicioSelec.setText(servicSeleccionado.getNombre());
                cargarPrecios();
                limpiarPrecios();
            }
            servSelec = false;
        }
    }

    public void cargarColumnasPrecios() {
        tablaPrecios.getColumns().clear();
        TableColumn<ServiciosPreciosDTO, String> colCosto = new TableColumn<>("Costo");
        colCosto.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCosto())));
        TableColumn<ServiciosPreciosDTO, String> colFecha = new TableColumn<>("Fecha de registro");
        colFecha.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getFechaRegistro())));
        TableColumn<ServiciosPreciosDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaPrecios.getColumns().addAll(colCosto, colFecha, colEst);
    }

    public void cargarPrecios() {
        tablaPrecios.getItems().clear();
        Respuesta res = servService.findById(servicSeleccionado.getId());
        if (res.getEstado()) {
            ServiciosDTO servic = (ServiciosDTO) res.getResultado("Servicios");
            if (servic.getServiciosPrecios() != null) {
                tablaPrecios.getItems().addAll((List<ServiciosPreciosDTO>) servic.getServiciosPrecios());
            }
        }
    }

    @FXML
    private void actClickTablaPrecios(MouseEvent event) {
        precioSelect = (ServiciosPreciosDTO) tablaPrecios.getSelectionModel().getSelectedItem();
        precioSelec = true;
        txtCostoServico.setText(String.valueOf(precioSelect.getCosto()));
    }

    public void limpiarPrecios() {
        txtCostoServico.setText(null);
        precioSelec = false;
        precioSelect = new ServiciosPreciosDTO();
    }

    @FXML
    private void actInactivarPrecios(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar Precio"));
        } else {
            if (precioSelec) {
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "", codigo = "";
                if (UserAuthenticated.getInstance().isRol("GERENTE")) {
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                } else if (UserAuthenticated.getInstance().isRol("GESTOR")) {
                    Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Precio de Servicio");
                    if (result.isPresent()) {
                        cedula = result.get().getKey();
                        codigo = result.get().getValue();
                        puedeInactivar = Boolean.TRUE;
                    }
                }
                if (puedeInactivar) {
                    Respuesta res = precioService.inactivar(precioSelect, precioSelect.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Precio de Servicio", "El Precio de Servicio ha sido inactivado");
                        precioSelec = false;
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Precio de Servicio", res.getMensaje());
                    }
                }
            }
        }
    }

    public boolean validarPreciosActivos() {
        if (precioSelect.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actLimpiarCamposPrecio(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Limpiar Precio"));
        } else {
            limpiarPrecios();
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
            adjustHeight(contenedor.getHeight());
        });
    }

    private void adjustWidth(double ancho) {
        bpRoot.setPrefWidth(ancho);
        vbRoot.setPrefWidth(ancho);
        tabPane.setPrefWidth(ancho);
        gpTabla.setPrefWidth(ancho);
        gpServicio.setPrefWidth(ancho);
        tablaServicios.setPrefWidth((ancho / 2) - 25);
        txtBuscarServicio.setPrefWidth((ancho / 2) - (25 + 130 + 88));
        tablaPrecios.setPrefWidth((ancho / 2) - 25);
    }

    private void adjustHeight(double alto) {
        bpRoot.setPrefHeight(alto);
        vbRoot.setPrefHeight(alto);
        tabPane.setPrefHeight(alto - 48);
        gpTabla.setPrefHeight(alto - 87);
        gpServicio.setPrefHeight(alto - 87);
        tablaServicios.setPrefHeight(alto - 241);
        tablaPrecios.setPrefHeight(alto - 207);
    }

}
