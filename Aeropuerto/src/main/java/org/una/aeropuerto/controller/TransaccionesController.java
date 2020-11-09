/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.stage.StageStyle;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.TransaccionesDTO;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.service.TransaccionesService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
/**
 * FXML Controller class
 *
 * @author cordo
 */
public class TransaccionesController extends Controller implements Initializable {

    @FXML
    private TableView  tablaTransac;
    @FXML
    private JFXTextField txtBuscarTransacciones;
    private Map<String,String> modoDesarrollo;
    @FXML
    private DatePicker dpDesde;
    @FXML
    private DatePicker dpHasta;
    private final TransaccionesService service = new TransaccionesService();
    private String empleado;
    private ListView<String> lv;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
        txtBuscarTransacciones.setMouseTransparent(true);
        lv = (ListView<String>) AppContext.getInstance().get("ListView");
    }    

    @Override
    public void initialize() {
    }
    
    private void asignarModoDesarrollor(){
        lv.getItems().clear();
        for(String elemento : modoDesarrollo.keySet()){
            lv.getItems().add(modoDesarrollo.get(elemento));
        }
    }

    @FXML
    private void actBuscar(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lv.getSelectionModel().select(modoDesarrollo.get("Buscar"));
        }else{
            if(validarCampos()){
                Respuesta res = service.getByFiltro(Date.from(dpDesde.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant()), Date.from(dpHasta.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant()), empleado);
                if(res.getEstado()){
                    List<TransaccionesDTO> lista = (List<TransaccionesDTO>) res.getResultado("Transacciones");
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Transacciones", res.getMensaje());
                }
            }
        }
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista Transacciones");
        modoDesarrollo.put("Buscar", "Responde al método actBuscar");
        modoDesarrollo.put("Generar", "Responde al método actGenerarReporte");
        modoDesarrollo.put("Empleado", "Responde al método atBuscarEmpleado");   
    }
    
    private Boolean validarCampos(){
        if(dpDesde.getValue() != null && dpHasta.getValue() != null){
            if(dpDesde.getValue().isBefore(dpHasta.getValue()) && dpHasta.getValue().equals(LocalDate.now()) || dpHasta.getValue().isBefore(LocalDate.now())){
                if(txtBuscarTransacciones.getText() == null || txtBuscarTransacciones.getText().isEmpty())
                    empleado = "nulo";
                else
                    empleado = txtBuscarTransacciones.getText();
                return true;
            }else{
                Mensaje.show(Alert.AlertType.WARNING, "Buscar Transacciones", "El rango de fechas especificado, no es valido\nVerifique el orden de la fechas, o si la fecha final es mayor a hoy");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Buscar Transacciones", "No se especifico un rango de fechas");
        }
        return false;
    }
    
    @Override
    public void cargarTema() {
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            
        }
    }

    @FXML
    private void actBuscarEmpleado(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            AppContext.getInstance().set("empSelect", null);
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            EmpleadosDTO emplSeleccionado = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if(emplSeleccionado != null)
                txtBuscarTransacciones.setText(emplSeleccionado.getCedula());
        }
    }
    
}
