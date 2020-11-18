/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.una.aeropuerto.dto.ParametrosSistemaDTO;
import org.una.aeropuerto.service.ParametrosSistemaService;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.util.Formato;
import org.una.aeropuerto.util.FlowController;

public class ParametrosSistemaController extends Controller implements Initializable{

    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFinal;
    @FXML
    private TableView<ParametrosSistemaDTO> tvParametros;
    @FXML
    private TableColumn<ParametrosSistemaDTO, String> tcDescripcion;
    @FXML
    private TableColumn<ParametrosSistemaDTO, String> tcValor;
    @FXML
    private TableColumn<ParametrosSistemaDTO, String> tcCodIdentificador;
    @FXML
    private TableColumn<ParametrosSistemaDTO, String> tcFechaRegistro;
    @FXML
    private TableColumn<ParametrosSistemaDTO, String> tcFechaModificacion;
    @FXML
    private JFXTextField txtValor;
    @FXML
    private JFXTextField txtCodigo;
    @FXML
    private JFXTextArea txtDescripcion;
    
    ParametrosSistemaDTO parametroSeleccionado;
    ParametrosSistemaService paramService;
    Map<String,String> modoDesarrollo;
    private ListView<String> lvDesarrollo;
    
    private final String fecha = "yyyy-MM-dd";
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat(fecha);
    @FXML
    private BorderPane bpRoot;
    @FXML
    private TabPane tabPane;
    @FXML
    private BorderPane vbRoot;
    @FXML
    private GridPane gpDatos;


    @Override
    public void initialize() {
        adjustHeight(contenedor.getHeight());
        adjustWidth(contenedor.getWidth());
        asignarInfoModoDesarrollo();
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initTableView();
        paramService = new ParametrosSistemaService();
        addListener();
        datosModoDesarrollo();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        limitarCamposDeTexto();
    }

    public void initTableView(){
        tcDescripcion.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        tcValor.setCellValueFactory((p)-> new SimpleStringProperty(p.getValue().getValor()));
        tcCodIdentificador.setCellValueFactory((p)-> new SimpleStringProperty(p.getValue().getCodigoIdentificador()));
        tcFechaRegistro.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoFecha.format(p.getValue().getFechaRegistro()))));
        tcFechaModificacion.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoFecha.format(p.getValue().getFechaModificacion()))));
    }
    
    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista ParametrosSistema");
        modoDesarrollo.put("Inactivar", "Inactivar responde al método inactivarParametro");
        modoDesarrollo.put("Filtrar", "Filtrar responde al método filtrarParametros");
        modoDesarrollo.put("Agregar", "Agregar responde al método agregarEditarParametro");
        modoDesarrollo.put("Limpiar Campos", "Limpiar Campos responde al método limpiarCampos");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDesarrollo.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    private void limitarCamposDeTexto(){
        txtCodigo.setTextFormatter(Formato.getInstance().maxLengthFormat(25));
        txtValor.setTextFormatter(Formato.getInstance().maxLengthFormat(50));
        txtDescripcion.setTextFormatter(Formato.getInstance().maxLengthFormat(100));
    }
    
    public Task TaskFiltrarParametros(){
        return new Task() {
            @Override
            protected Object call() throws Exception {
                Respuesta res;
                updateMessage("Filtrando empleados.");
                updateProgress(1, 3);
                res = paramService.getByFechaRegistro(convertirFechaAString(dpInicio.getValue()), convertirFechaAString(dpFinal.getValue()));
                updateMessage("Filtrando empleados..");
                updateProgress(2, 3);
                Platform.runLater( () -> {
                    if(res.getEstado()){
                        tvParametros.getItems().addAll((List<ParametrosSistemaDTO>)res.getResultado("Parametros_Sistema"));
                    }else{
                        System.out.println(res.getMensajeInterno());
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Parámetros del Sistema", res.getMensaje());
                    }
                });
                updateMessage("Filtrando empleados...");
                updateProgress(3, 3);
                return true;
            }
        };
    }
    
    public String convertirFechaAString(LocalDate lDate){
        String fecha = "";
        fecha += String.valueOf(lDate.getYear())+"-";
        if(lDate.getMonthValue() < 10){
            fecha+= "0"+String.valueOf(lDate.getMonthValue()+"-");
        }else{
            fecha+= String.valueOf(lDate.getMonthValue()+"-");
        }
        if(lDate.getDayOfMonth() < 10){
            fecha+= "0"+String.valueOf(lDate.getDayOfMonth());
        }else{
            fecha+= String.valueOf(lDate.getDayOfMonth());
        }
        
        return fecha;
    }
    
    public boolean validarCampos(){
        return !(txtCodigo.getText().isEmpty() && txtDescripcion.getText().isEmpty() && txtValor.getText().isEmpty());
    }
    
    public void resetearCampos(){
        parametroSeleccionado = null;
        txtCodigo.clear();
        txtDescripcion.clear();
        txtValor.clear();
    }
    
    @FXML
    private void filtrarParametros(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Filtrar"));
        }else{
            if(dpInicio.getValue() != null && dpFinal.getValue() != null){
                if(dpInicio.getValue().isBefore(dpFinal.getValue()) ){
                    tvParametros.getItems().clear();
                    AppContext.getInstance().set("Task", TaskFiltrarParametros());
                    FlowController.getInstance().goViewCargar();
                }else{
                    Mensaje.show(Alert.AlertType.WARNING, "Fechas Incorrectas", "El orden de las fechas esta invertido");
                }
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Datos Faltantes", "El rango de fechas para filtrar no esta seleccionado");
            } 
        }
    }

    @FXML
    private void inactivarParametro(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Inactivar"));
        }else{
            if(parametroSeleccionado != null){
                parametroSeleccionado.setEstado(Boolean.FALSE);
                Respuesta res = paramService.update(parametroSeleccionado, parametroSeleccionado.getId());
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.CONFIRMATION, "Éxito", "El parámetro del sistema fue inactivado con éxito");
                    tvParametros.getItems().remove(parametroSeleccionado);
                }else{
                    Mensaje.show(Alert.AlertType.WARNING, "Error", "No se pudo inactivar el parámetro del sistema");
                }
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Datos Insuficientes", "No se ha seleccionado un parámetro para inactivar");
            }
        }
        
    }

    @FXML
    private void clickTableView(MouseEvent event) {
        if(tvParametros.getSelectionModel().getSelectedItem() != null){
            parametroSeleccionado = tvParametros.getSelectionModel().getSelectedItem();
            txtCodigo.setText(parametroSeleccionado.getCodigoIdentificador());
            txtDescripcion.setText(parametroSeleccionado.getDescripcion());
            txtValor.setText(parametroSeleccionado.getValor());
            txtCodigo.setEditable(false);
        }
    }

    @FXML
    private void agregarEditarParametro(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Agregar"));
        }else{
            Respuesta res;
            ParametrosSistemaDTO parametro = new ParametrosSistemaDTO();
            if(parametroSeleccionado != null){
                parametro.setCodigoIdentificador(txtCodigo.getText());
                parametro.setDescripcion(txtDescripcion.getText());
                parametro.setEstado(parametroSeleccionado.isEstado());
                parametro.setFechaModificacion(parametroSeleccionado.getFechaModificacion());
                parametro.setFechaRegistro(parametroSeleccionado.getFechaRegistro());
                parametro.setId(parametroSeleccionado.getId());
                parametro.setValor(txtValor.getText());
                res = paramService.modificarParametro(parametroSeleccionado.getId(), parametro);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.CONFIRMATION, "Actualización Éxitosa", "El parámetro del sistema se actualizó con éxito");
                    parametro = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                    tvParametros.getItems().remove(parametroSeleccionado);
                    tvParametros.getItems().add(parametro);
                    resetearCampos();
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Error", "Falló la acualización del parámetro del sistema");
                }
            }else{
                parametro.setCodigoIdentificador(txtCodigo.getText());
                parametro.setDescripcion(txtDescripcion.getText());
                parametro.setValor(txtValor.getText());
                parametro.setEstado(Boolean.TRUE);
                res = paramService.guardarParametro(parametro);
                if(res.getEstado()){
                    Mensaje.show(Alert.AlertType.CONFIRMATION, "Guardado Éxitosa", "El parámetro del sistema se guardó con éxito");
                    parametro = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                    tvParametros.getItems().add(parametro);
                    resetearCampos();
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Error", "Falló el guardado del parámetro del sistema");
                }
            }
        }
    }

    @FXML
    private void limpiarCampos(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Limpiar Campos"));
        }else{
            parametroSeleccionado = null;
            txtCodigo.clear();
            txtDescripcion.clear();
            txtValor.clear();
            txtCodigo.setEditable(true);
        }
    }
    
    private void addListener(){
        contenedor.widthProperty().addListener( w -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener( h -> {
            adjustHeight(contenedor.getHeight());
        });
    }
    
    private void adjustWidth(double ancho){
        bpRoot.setPrefWidth(ancho);
        vbRoot.setPrefWidth(ancho);
        tabPane.setPrefWidth(ancho);
        tvParametros.setPrefWidth(ancho-30);
        gpDatos.setPrefWidth(ancho);
    }

    private void adjustHeight(double alto){
        bpRoot.setPrefHeight(alto);
        vbRoot.setPrefHeight(alto);
        tabPane.setPrefHeight(alto-113);
        gpDatos.setPrefHeight(alto-207);
    }
}
