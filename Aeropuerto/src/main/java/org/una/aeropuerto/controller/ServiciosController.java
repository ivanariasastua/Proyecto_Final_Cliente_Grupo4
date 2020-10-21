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
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Pair;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.dto.ServiciosPreciosDTO;
import org.una.aeropuerto.service.ServiciosPreciosService;
import org.una.aeropuerto.service.ServiciosService;
import org.una.aeropuerto.util.AppContext;
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
    private JFXTextField txtNombreServicio;
    @FXML
    private JFXTextArea txtDescripcionServicio;
    @FXML
    private JFXTextField txtBuscarServicio;
    private ServiciosDTO servicioDTO;
    private ServiciosService servService;
    @FXML
    private TableView tablaServicios;

    private List<ServiciosDTO> listServic;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroServicios.setItems(filtro);
        clickTabla();
        cargarColumnasPrecios();
        cargarColumnas();
    }

    @Override
    public void initialize() {
        servService = new ServiciosService();
        precioService = new ServiciosPreciosService();
        servicioDTO = new ServiciosDTO();
        listServic = new ArrayList<>();
        limpiarCampos();
        limpiarPrecios();
    }

    public void clickTabla() {
        tablaServicios.setRowFactory(tv -> {
            TableRow<ServiciosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    servSelec = true;
                    servicSeleccionado = row.getItem();
                    txtNombreServicio.setText(servicSeleccionado.getNombre());
                    txtDescripcionServicio.setText(servicSeleccionado.getDescripcion());
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
        TableColumn<ServiciosDTO, String> colDescrip = new TableColumn<>("Descripcion");
        colDescrip.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        TableColumn<ServiciosDTO, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory((p) -> new SimpleStringProperty(estado(p.getValue().isEstado())));
        tablaServicios.getColumns().addAll(colNomb, colDescrip, colEst);
    }

    public boolean validarActivos() {
        if (servicSeleccionado.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarServicio(ActionEvent event) {
        if (servSelec == true) {
            if (validarActivos()) {
                servicSeleccionado.setId(servicSeleccionado.getId());
                servicSeleccionado.setDescripcion(txtDescripcionServicio.getText());
                servicSeleccionado.setNombre(txtNombreServicio.getText());
                Respuesta res = servService.modificarServicio(servicSeleccionado.getId(), servicSeleccionado);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Servicio editado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                }
            }
        } else {
            if (txtNombreServicio.getText() != null) {
                servicioDTO = new ServiciosDTO();
                servicioDTO.setDescripcion(txtDescripcionServicio.getText());
                servicioDTO.setNombre(txtNombreServicio.getText());
                Respuesta res = servService.guardarServicio(servicioDTO);
                if (res.getEstado()) {
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Servicio guardado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Nombre es obligatorio");
            }
        }
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
        cargarColumnas();
        tablaServicios.getItems().clear();
        if (cbxFiltroServicios.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar el tipo de filtro", "Debe seleccionar por cual tipo desea filtrar la informacion");
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

    public void limpiarCampos() {
        txtNombreServicio.setText(null);
        txtDescripcionServicio.setText(null);
        servSelec = false;

    }

    @FXML
    private void actLimpiarCamposServicio(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void actInactivarServicio(ActionEvent event) {
        if(servicSeleccionado != null){
            Boolean puedeInactivar = Boolean.FALSE;
            String cedula = "", codigo = "";
            if(UserAuthenticated.getInstance().isRol("GERENTE")){
                cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                codigo = (String) AppContext.getInstance().get("CodigoGerente");
                puedeInactivar = Boolean.TRUE;
            }else if(UserAuthenticated.getInstance().isRol("GESTOR")){
                Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Servicio");
                if(result.isPresent()){
                    cedula = result.get().getKey();
                    codigo = result.get().getValue();
                    puedeInactivar = Boolean.TRUE;
                }
            }
            if(puedeInactivar){
                Respuesta res = servService.inactivar(servicSeleccionado, servicSeleccionado.getId(), cedula, codigo);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar servicio", "El servicio ha sido inactivado");
                }else{
                    Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar servicio", res.getMensaje());
                }
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Inactivar servicio", "No ha seleccionado ningun servicio");
        }
        /*
        if (servSelec == true) {
            if (Mensaje.showConfirmation("Inactivar", null, "Seguro que desea inactivar la información?")) {
                if (validarActivos()) {
                    servicSeleccionado.setEstado(false);
                    Respuesta res = servService.modificarServicio(servicSeleccionado.getId(), servicSeleccionado);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Se ha inactivado correctamente el servicio");
                        servSelec = false;
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            } else {
                servSelec = false;
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        }
*/
    }

    @FXML
    private void actGuardarPrecio(ActionEvent event) {
        if (precioSelec == true) {
            if (validarPreciosActivos()) {
                System.out.println("editando " + precioSelect);
                precioSelect.setId(precioSelect.getId());
                precioSelect.setCosto(Float.valueOf(txtCostoServico.getText()));
                precioSelect.setServicio(servicSeleccionado);
                Respuesta resp = precioService.modificarPrecioServicio(precioSelect.getId(), precioSelect);
                if (resp.getEstado()) {
                    cargarPrecios();
                    Mensaje.show(Alert.AlertType.INFORMATION, "Editado Correctamente", "Precio editado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", resp.getMensaje());
                }
            }
        } else {
            if (txtCostoServico.getText() != null) {
                preciosDto = new ServiciosPreciosDTO();
                preciosDto.setCosto(Float.valueOf(txtCostoServico.getText()));
                preciosDto.setServicio(servicSeleccionado);
                Respuesta resp = precioService.guardarPrecioServicio(preciosDto);
                if (resp.getEstado()) {
                    cargarPrecios();
                    Mensaje.show(Alert.AlertType.INFORMATION, "Guardado Correctamente", "Precio guardado correctamente");
                } else {
                    Mensaje.show(Alert.AlertType.ERROR, "Error", resp.getMensaje());
                }
            } else {
                Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Precio es obligatorio");
            }
        }
    }

    @FXML
    private void actPane(MouseEvent event) {
        if (tabPrecios.isSelected() && servSelec == false) {
            SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
            selectionModel.select(tabServicios);
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        } else if (tabPrecios.isSelected() && servSelec == true) {
            txtServicioSelec.setText(servicSeleccionado.getNombre());
            cargarPrecios();
            limpiarPrecios();
        }
        servSelec = false;
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
        Respuesta res = servService.findById(servicSeleccionado.getId());
        if (res.getEstado()) {
            ServiciosDTO servic = (ServiciosDTO) res.getResultado("Servicios");
            tablaPrecios.getItems().clear();
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
        if (precioSelec == true) {
            if (Mensaje.showConfirmation("Inactivar", null, "Seguro que desea inactivar la información?")) {
                if (validarPreciosActivos()) {
                    precioSelect.setEstado(false);
                    precioSelect.setServicio(servicSeleccionado);
                    Respuesta res = precioService.modificarPrecioServicio(precioSelect.getId(), precioSelect);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Inactivado", "Se ha inactivado correctamente el servicio");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error ", res.getMensaje());
                    }
                }
            }
        } else {
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Servicio", "Debe seleccionar un servicio");
        }
        precioSelec = false;
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
        limpiarPrecios();
    }

    @Override
    public void cargarTema() {
    }
}
