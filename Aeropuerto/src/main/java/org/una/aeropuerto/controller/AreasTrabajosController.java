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
import javafx.util.Pair;
import org.una.aeropuerto.dto.AreasTrabajosDTO;
import org.una.aeropuerto.service.AreasTrabajosService;
import org.una.aeropuerto.util.AppContext;
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
    @FXML
    private JFXTextField txtNombreArea;
    @FXML
    private JFXTextArea txtDescripcionArea;

    private AreasTrabajosService areasService = new AreasTrabajosService();
    private AreasTrabajosDTO areaDto = new AreasTrabajosDTO();
    private AreasTrabajosDTO areaSeleccionada = new AreasTrabajosDTO();
    boolean areaSelec = false;
    @FXML
    private JFXTextField txtBuscarAreasT;
    @FXML
    private JFXComboBox<String> cbxFiltroAreas;
    @FXML
    private JFXButton btnGuardar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ObservableList filtro = FXCollections.observableArrayList("Nombre", "Estado");
        cbxFiltroAreas.setItems(filtro);
        clickTablas();
    }

    @Override
    public void initialize() {
        llenarColumnas();
        limpiarAreas();
        btnGuardar.setVisible(UserAuthenticated.getInstance().isRol("GESTOR") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
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
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
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

    public void clickTablas() {
        tablaAreasTrabajo.setRowFactory(tv -> {
            TableRow<AreasTrabajosDTO> row = new TableRow();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty() && e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
                    areaSelec = true;
                    areaSeleccionada = row.getItem();
                    txtDescripcionArea.setText(areaSeleccionada.getDescripcion());
                    txtNombreArea.setText(areaSeleccionada.getNombre());
                }
            });
            return row;
        });
    }

    public boolean validarActivos() {
        if (areaSeleccionada.isEstado() != true) {
            Mensaje.show(Alert.AlertType.WARNING, "Inactivado", "El dato se encuentra inactivo, no puede realizar más acciones con dicha información");
            return false;
        }
        return true;
    }

    @FXML
    private void actGuardarAreasTrabajo(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            if (areaSelec == true) {
                if (validarActivos()) {
                    areaSeleccionada.setId(areaSeleccionada.getId());
                    areaSeleccionada.setDescripcion(txtDescripcionArea.getText());
                    areaSeleccionada.setNombre(txtNombreArea.getText());
                    Respuesta res = areasService.modificarAreaTrabajo(areaSeleccionada.getId(), areaSeleccionada);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Editado", "Area de trabajo editada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            } else {
                if (txtNombreArea.getText() == null) {
                    Mensaje.show(Alert.AlertType.WARNING, "Campo requerido", "El campo Nombre es obligatorio");
                } else {
                    areaDto = new AreasTrabajosDTO();
                    areaDto.setDescripcion(txtDescripcionArea.getText());
                    areaDto.setNombre(txtNombreArea.getText());
                    Respuesta res = areasService.guardarAreaTrabajo(areaDto);
                    if (res.getEstado()) {
                        Mensaje.show(Alert.AlertType.INFORMATION, "Guardado", "Area de trabajo guardada correctamente");
                    } else {
                        Mensaje.show(Alert.AlertType.ERROR, "Error", res.getMensaje());
                    }
                }
            }
        }
    }

    public void limpiarAreas() {
        txtDescripcionArea.setText(null);
        txtNombreArea.setText(null);
        areaSelec = false;
        areaSeleccionada = new AreasTrabajosDTO();
    }

    @FXML
    private void actLimpiarCamposAreasTrabajo(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }
        limpiarAreas();
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
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            if(areaSeleccionada != null){
                Boolean puedeInactivar = Boolean.FALSE;
                String cedula = "";
                String codigo = "";
                if(UserAuthenticated.getInstance().isRol("GERENTE")){
                    cedula = UserAuthenticated.getInstance().getUsuario().getCedula();
                    codigo = (String) AppContext.getInstance().get("CodigoGerente");
                    puedeInactivar = Boolean.TRUE;
                }else if(UserAuthenticated.getInstance().isRol("GESTOR")){
                        Optional<Pair<String, String>> result = Mensaje.showDialogoParaCodigoGerente("Inactivar Area de trabajo");
                        if(result.isPresent()){
                            cedula = result.get().getKey();
                            codigo = result.get().getValue();
                            puedeInactivar = Boolean.TRUE;
                        }
                }
                    if(puedeInactivar){
                        Respuesta res = areasService.inactivar(areaSeleccionada, areaSeleccionada.getId(), cedula, codigo);
                        if(res.getEstado()){
                            Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", "La área de trabajo: "+areaSeleccionada.getNombre()+" ha sido inactivada");
                        }else{
                            Mensaje.show(Alert.AlertType.INFORMATION, "Inactivar Áreas", res.getMensaje());
                        }
                    }
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Inactivar Área", "No ha seleccionado ninguna área de trabajo");
            }
        }
    }

    @Override
    public void cargarTema() {
    }

}
