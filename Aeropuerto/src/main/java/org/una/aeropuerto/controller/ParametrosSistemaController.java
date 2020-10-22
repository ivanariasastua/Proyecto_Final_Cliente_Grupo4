/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.dto.ParametrosSistemaDTO;
import org.una.aeropuerto.service.ParametrosSistemaService;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.Mensaje;

public class ParametrosSistemaController extends Controller implements Initializable{

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
    
    ParametrosSistemaDTO parametroSeleccionado;
    ParametrosSistemaService paramService;
    
    private final String fecha = "yyyy-MM-dd";
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat(fecha);

    @Override
    public void cargarTema() {
        
    }

    @Override
    public void initialize() {
        
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initTableView();
        paramService = new ParametrosSistemaService();
    }

    public void initTableView(){
        tcDescripcion.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDescripcion()));
        tcValor.setCellValueFactory((p)-> new SimpleStringProperty(p.getValue().getValor()));
        tcCodIdentificador.setCellValueFactory((p)-> new SimpleStringProperty(p.getValue().getCodigoIdentificador()));
        tcFechaRegistro.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoFecha.format(p.getValue().getFechaRegistro()))));
        tcFechaModificacion.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoFecha.format(p.getValue().getFechaModificacion()))));
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
                        Mensaje.show(Alert.AlertType.ERROR, "Buscar Parametros del Sistema", res.getMensaje());
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
    
    @FXML
    private void filtrarParametros(ActionEvent event) {
        if(dpInicio.getValue() != null && dpFinal.getValue() != null){
            if(dpInicio.getValue().isBefore(dpFinal.getValue()) ){
                tvParametros.getItems().clear();
                Mensaje.showProgressDialog(TaskFiltrarParametros(), "Filtrando Parametros", "Obteniendo los parametros de la base de datos");
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Fechas Incorrectas", "El orden de las fechas esta invertido");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Datos Faltantes", "El rango de fechas para filtrar no esta seleccionado");
        } 
    }

    @FXML
    private void inactivarParametro(ActionEvent event) {
        if(parametroSeleccionado != null){
            parametroSeleccionado.setEstado(Boolean.FALSE);
            Respuesta res = paramService.update(parametroSeleccionado, parametroSeleccionado.getId());
            if(res.getEstado()){
                Mensaje.show(Alert.AlertType.CONFIRMATION, "Éxito", "El parametro del sistema fue inactivado con éxito");
                tvParametros.getItems().remove(parametroSeleccionado);
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Error", "No se pudo inactivar el parametro del sistema");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Datos Insuficientes", "No se ha seleccionado un parametro para inactivar");
        }
    }

    @FXML
    private void clickTableView(MouseEvent event) {
        if(tvParametros.getSelectionModel().getSelectedItem() != null){
            parametroSeleccionado = tvParametros.getSelectionModel().getSelectedItem();
            System.out.println(parametroSeleccionado.isEstado());
        }
    }
    
}
