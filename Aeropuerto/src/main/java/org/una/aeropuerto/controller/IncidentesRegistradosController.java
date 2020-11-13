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
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
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
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.IncidentesCategoriasDTO;
import org.una.aeropuerto.dto.IncidentesRegistradosDTO;
import org.una.aeropuerto.service.IncidentesRegistradosService;
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
public class IncidentesRegistradosController extends Controller implements Initializable {

    @FXML
    private JFXTextField txtCategoriaSelec;
    @FXML
    private JFXTextField txtEmisorSelec;
    @FXML
    private JFXTextField txtResponsableSelec;
    @FXML
    private JFXTextField txtAreaSelec;
    @FXML
    private JFXTextArea txtDescripcionIncident;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    private IncidentesRegistradosService incidentService;
    private IncidentesRegistradosDTO incidentDTO;
    private AreasTrabajosDTO areaSelec = new AreasTrabajosDTO();
    private EmpleadosDTO emisorSelec = new EmpleadosDTO();
    private EmpleadosDTO responsableSelec = new EmpleadosDTO();
    private IncidentesCategoriasDTO categoriaSelec = new IncidentesCategoriasDTO();
    @FXML
    private TableView tablaIncident;
    @FXML
    private JFXTextField txtBuscarIncident;
    @FXML
    private JFXComboBox<String> cbxFiltro;
    boolean incidentSelec;
    IncidentesRegistradosDTO incidentSeleccionado = new IncidentesRegistradosDTO();
    List<EmpleadosDTO> listEmpl = new ArrayList<>();
    List<AreasTrabajosDTO> listAreas = new ArrayList<>();
    private Map<String, String> modoDesarrollo;
    @FXML
    private Tab tabCrear;
    @FXML
    private Tab tabIncidentes;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXButton btnGuardar;
    @FXML
    private BorderPane bpRoot;
    @FXML
    private VBox vbRoot;
    @FXML
    private GridPane gpCrearEditar;
    @FXML
    private GridPane gpTabla;
    private ListView<String> lvDesarrollo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList<String> item = FXCollections.observableArrayList("Responsable", "Emisor", "Categoria", "Area");
        cbxFiltro.setItems(item);
        clickTabla();
        addListener();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        datosModoDesarrollo();
    }

    @Override
    public void initialize() {
        incidentService = new IncidentesRegistradosService();
        incidentDTO = new IncidentesRegistradosDTO();
        incidentSelec = false;
        limpiar();
        llenarColumnas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        adjustWidth(contenedor.getWidth());
        adjustHeight(contenedor.getHeight());
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            asignarInfoModoDesarrollo();
        }
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista IncidentesRegistrados");
        modoDesarrollo.put("Buscar Categoria", "Buscar Categoria responde al método actBuscarCategoria");
        modoDesarrollo.put("Buscar Emisor", "Buscar Emisor responde al método actBuscarEmisor");
        modoDesarrollo.put("Buscar Responsable", "Buscar Responsable responde al método actBuscarResponsable");
        modoDesarrollo.put("Buscar Area", "Buscar Área responde al método actBuscarArea");
        modoDesarrollo.put("Limpiar", "Limpiar responde al método actLimpiarCamposIncidentes");
        modoDesarrollo.put("Guardar", "Guardar responde al método actGuardarIncidenteRegistrado");
        modoDesarrollo.put("Buscar", "Buscar responde al método actBuscarIncidente");
        modoDesarrollo.put("Seguimiento", "Seguimiento responde al método actSeguimientoIncidenteEstados");
        modoDesarrollo.put("Editar", "Editar responde al método actEditarIncidente");
        modoDesarrollo.put("Inactivar", "Inactivar responde al método actInactivarIncidente");
    }
    
    public void asignarInfoModoDesarrollo(){
        lvDesarrollo.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }

    public void clickTabla() {
        tablaIncident.setRowFactory(tv -> {
            TableRow<IncidentesRegistradosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    incidentSelec = true;
                    incidentSeleccionado = row.getItem();
                }
            });
            return row;
        });
    }

    @FXML
    private void actBuscarCategoria(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Categoria"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarCategorias", false, StageStyle.DECORATED);
            categoriaSelec = (IncidentesCategoriasDTO) AppContext.getInstance().get("CategoriaSup");
            if (categoriaSelec != null) {
                txtCategoriaSelec.setText(categoriaSelec.getNombre());
            }
        }
    }

    @FXML
    private void actBuscarEmisor(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Emisor"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            emisorSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if (emisorSelec != null) {
                txtEmisorSelec.setText(emisorSelec.getNombre());
            }
        }
    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Responsable"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            responsableSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if (responsableSelec != null) {
                txtResponsableSelec.setText(responsableSelec.getNombre());
            }
        }
    }

    @FXML
    private void actBuscarArea(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Area"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarArea", false, StageStyle.DECORATED);
        } else {
            FlowController.getInstance().goViewInNoResizableWindow("BuscarArea", false, StageStyle.DECORATED);
            areaSelec = (AreasTrabajosDTO) AppContext.getInstance().get("Area");
            if (areaSelec != null) {
                txtAreaSelec.setText(areaSelec.getNombre());
            }
        }
    }

    public boolean validarCampos() {
        if (txtAreaSelec.getText() == null || txtCategoriaSelec.getText() == null || txtEmisorSelec.getText() == null || txtResponsableSelec.getText() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos requeridos", "Los campos son obligatorios");
            return false;
        }
        return true;
    }

    public boolean validarActivos() {
        if (incidentSeleccionado.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarIncidenteRegistrado(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Guardar"));
        } else {
            if (incidentSelec == true) {
                if (validarActivos()) {
                    incidentSeleccionado.setId(incidentSeleccionado.getId());
                    incidentSeleccionado.setAreaTrabajo(areaSelec);
                    incidentSeleccionado.setCategoria(categoriaSelec);
                    incidentSeleccionado.setDescripcion(txtDescripcionIncident.getText());
                    incidentSeleccionado.setEmisor(emisorSelec);
                    incidentSeleccionado.setResponsable(responsableSelec);
                    Respuesta res = incidentService.modificarIncidenteRegistrado(incidentSeleccionado.getId(), incidentSeleccionado);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Incidente editado correctamente");
                        IncidentesRegistradosDTO act = (IncidentesRegistradosDTO) res.getResultado("Incidentes_Registrados");
                        actualizarDatos(tablaIncident.getItems(), act);
                        Platform.runLater(() -> {
                            tablaIncident.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            } else {
                if (validarCampos()) {
                    incidentDTO = new IncidentesRegistradosDTO();
                    incidentDTO.setAreaTrabajo(areaSelec);
                    incidentDTO.setCategoria(categoriaSelec);
                    incidentDTO.setDescripcion(txtDescripcionIncident.getText());
                    incidentDTO.setEmisor(emisorSelec);
                    incidentDTO.setResponsable(responsableSelec);
                    Respuesta res = incidentService.guardarIncidenteRegistrado(incidentDTO);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Incidente guardado correctamente");
                        IncidentesRegistradosDTO act = (IncidentesRegistradosDTO) res.getResultado("Incidentes_Registrados");
                        tablaIncident.getItems().add(act);
                        Platform.runLater(() -> {
                            tablaIncident.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            }
        }
    }

    public void limpiar() {
        txtAreaSelec.setText(null);
        txtCategoriaSelec.setText(null);
        txtDescripcionIncident.setText(null);
        txtEmisorSelec.setText(null);
        txtResponsableSelec.setText(null);
        incidentSelec = false;
    }

    @FXML
    private void actLimpiarCamposIncidentes(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Limpiar"));
        }else{
            limpiar(); 
        }
    }

    @FXML
    private void actInactivarIncidente(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar"));
        } else {
            if (incidentSelec == true) {
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
                    Respuesta res = incidentService.inactivar(incidentSeleccionado, incidentSeleccionado.getId(), cedula, codigo);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Precio de Servicio", "El Precio de Servicio ha sido inactivado");
                        incidentSelec = false;
                        IncidentesRegistradosDTO act = (IncidentesRegistradosDTO) res.getResultado("Incidentes_Registrados");
                        actualizarDatos(tablaIncident.getItems(), act);
                        Platform.runLater(() -> {
                            tablaIncident.refresh();
                        });
                    } else {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Precio de Servicio", res.getMensaje());
                    }
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar incidente", "Debe seleccionar un incidente");
            }
            incidentSelec = false;
        }
    }

    private void actualizarDatos(List<IncidentesRegistradosDTO> list, IncidentesRegistradosDTO inc){
        for(IncidentesRegistradosDTO i : list){
            if(i.getId().equals(inc.getId())){
                i.setEstado(inc.isEstado());
                i=inc;
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

    public void llenarColumnas() {
        tablaIncident.getColumns().clear();
        TableColumn<IncidentesRegistradosDTO, String> colCatag = new TableColumn<>("Categoría");
        colCatag.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getCategoria())));
        TableColumn<IncidentesRegistradosDTO, String> colEmisor = new TableColumn<>("Emisor");
        colEmisor.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getEmisor())));
        TableColumn<IncidentesRegistradosDTO, String> colRespons = new TableColumn<>("Responable");
        colRespons.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getResponsable())));
        TableColumn<IncidentesRegistradosDTO, String> colArea = new TableColumn<>("Área");
        colArea.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(p.getValue().getAreaTrabajo())));
        TableColumn<IncidentesRegistradosDTO, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<IncidentesRegistradosDTO, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaIncident.getColumns().addAll(colCatag, colEmisor, colRespons, colArea, colEstado, colDesc);
    }

    @FXML
    private void actBuscarIncidente(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        } else {
            AppContext.getInstance().set("Task", buscarIncidenteTask());
            FlowController.getInstance().goViewCargar();
        }
    }
    
    private Task buscarIncidenteTask(){
        return new Task(){
            @Override
            protected Object call() throws Exception {
                Platform.runLater(() -> {
                    llenarColumnas();
                });
                tablaIncident.getItems().clear();
                if (cbxFiltro.getValue() != null) {
                    Respuesta res;
                    if (cbxFiltro.getValue().equals("Emisor")) {
                        res = incidentService.findByEmisor(txtBuscarIncident.getText());
                    } else if (cbxFiltro.getValue().equals("Responsable")) {
                        res = incidentService.findByResponsable(txtBuscarIncident.getText());
                    } else if (cbxFiltro.getValue().equals("Area")) {
                        res = incidentService.findByArea(txtBuscarIncident.getText());
                    } else {
                        res = incidentService.findByCategoria(txtBuscarIncident.getText());
                    }
                    if (res.getEstado()) {
                        tablaIncident.getItems().addAll((List<IncidentesRegistradosDTO>) res.getResultado("Incidentes_Registrados"));
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Incidentes Registrados", res.getMensaje());
                    }
                } else {
                    Mensaje.show(Alert.AlertType.WARNING, "Seleccionar tipo de filtro", "Debe seleccionar por cúal tipo desea filtrar");
                }
                return true;
            }
            
        };
    }

    public void cargarDatos() {
        txtAreaSelec.setText(incidentSeleccionado.getAreaTrabajo().getNombre());
        areaSelec = incidentSeleccionado.getAreaTrabajo();
        txtCategoriaSelec.setText(incidentSeleccionado.getCategoria().getNombre());
        categoriaSelec = incidentSeleccionado.getCategoria();
        txtDescripcionIncident.setText(incidentSeleccionado.getDescripcion());
        txtEmisorSelec.setText(incidentSeleccionado.getEmisor().getNombre());
        emisorSelec = incidentSeleccionado.getEmisor();
        txtResponsableSelec.setText(incidentSeleccionado.getResponsable().getNombre());
        responsableSelec = incidentSeleccionado.getResponsable();
    }

    @FXML
    private void actEditarIncidente(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Editar"));
        } else {
            if (incidentSelec == true) {
                if (Mensaje.showConfirmation("Editar ", null, "Seguro que desea editar la información?")) {
                    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
                    selectionModel.select(tabCrear);
                    cargarDatos();
                } else {
                    incidentSelec = false;
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar incidente", "Debe seleccionar un incidente");
            }
        }
    }

    @FXML
    private void actSeguimientoIncidenteEstados(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Seguimiento"));
        } else {
            if (incidentSelec == true) {
                AppContext.getInstance().set("EstadosIncidentes", incidentSeleccionado);
                FlowController.getInstance().goViewInNoResizableWindow("EstadosIncidentes", false, StageStyle.DECORATED);
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Seleccionar incidente", "Debe seleccionar un incidente");
            }
            incidentSelec = false;
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
        gpCrearEditar.setPrefWidth(ancho);
        gpTabla.setPrefWidth(ancho);
        tabPane.setPrefWidth(ancho);
        txtBuscarIncident.setPrefWidth(ancho - (145 + 115 + 50));
        tablaIncident.setPrefWidth(ancho - 60);
    }

    private void adjustHeight(double alto) {
        bpRoot.setPrefHeight(alto);
        vbRoot.setPrefHeight(alto);
        gpCrearEditar.setPrefHeight(alto - 100);
        gpTabla.setPrefHeight(alto - 100);
        tabPane.setPrefHeight(alto - 40);
        tablaIncident.setPrefHeight(alto - 276);
    }
}
