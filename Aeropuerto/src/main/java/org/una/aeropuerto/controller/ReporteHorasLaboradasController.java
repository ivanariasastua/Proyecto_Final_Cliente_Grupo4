package org.una.aeropuerto.controller;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.jfoenix.controls.JFXTextField;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.service.ReporteService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.UserAuthenticated;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.dto.EmpleadosDTO;

public class ReporteHorasLaboradasController extends Controller implements Initializable{

    @FXML
    private JFXTextField txtCedula;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFinal;
    @FXML
    private GridPane gpRoot;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    
    ReporteService reporteService;
    Map<String,String> modoDesarrollo;
    private ListView<String> lvDesarrollo;

    @Override
    public void initialize() {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            asignarInfoModoDesarrollo();
        }
        ajustarAlto(contenedor.getHeight());
        ajustarAncho(contenedor.getWidth());
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        reporteService = new ReporteService();
        datosModoDesarrollo();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        ajustarPantalla();
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista es ReporteHorasLaboradas");
        modoDesarrollo.put("Generar Reporte", "Generar Reporte responde al método generarReporte");
        modoDesarrollo.put("Seleccionar Empleado", "Seleccionar empleado responde al método actSeleccionarEmpleado");
    }
    
    private void asignarInfoModoDesarrollo(){
        lvDesarrollo.getItems().clear();
        for(String info : modoDesarrollo.keySet()){
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }
    
    public boolean validarDatos(){
        if(dpInicio.getValue() == null || dpFinal.getValue() == null){
            Mensaje.show(Alert.AlertType.WARNING, "Datos Faltantes", "Falta una fecha para generar el reporte\n"
                                                + "Seleccione el rango de fechas para generar el reporte");
            return false;
        }
        
        if(dpInicio.getValue().isAfter(dpFinal.getValue())){
            Mensaje.show(Alert.AlertType.WARNING, "Error de fechas", "El orden de las fechas esta invertido");
            return false;
        }
        
        if(dpInicio.getValue().isAfter(LocalDate.now()) || dpFinal.getValue().isAfter(LocalDate.now())){
            Mensaje.show(Alert.AlertType.WARNING, "Error de fechas", "No se puede seleccionar fecha porteriores a la actual");
            return false;
        }
        return true;
    }
    
    @FXML
    private void generarReporte(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Generar Reporte"));
        }else{
            AppContext.getInstance().set("Task", reporteTask());
            FlowController.getInstance().goViewCargar();
        }
        
    }
    
    private Task reporteTask(){
        return new Task(){
            @Override
            protected Object call() throws Exception {
                if(validarDatos()){
                    Date fecha1 = Date.from(dpInicio.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    Date fecha2 = Date.from(dpFinal.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
                    Respuesta respuesta;
                    if(txtCedula.getText().isBlank()){
                        respuesta = reporteService.reporteHorasLaboradas("null", fecha1, fecha2);
                    }else{
                        respuesta = reporteService.reporteHorasLaboradas(txtCedula.getText(), fecha1, fecha2);
                    }

                    if(respuesta.getEstado()){
                        String res = (String) respuesta.getResultado("Reporte");
                        byte[] bytes = Base64.getDecoder().decode(res);
                        try{
                            ByteArrayInputStream array = new ByteArrayInputStream(bytes);
                            ObjectInputStream bytesArray = new ObjectInputStream(array);
                            JasperPrint jp = (JasperPrint) bytesArray.readObject();
                            JasperViewer viewer = new JasperViewer(jp, false);
                            viewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
                            Platform.runLater(() -> {
                                viewer.setVisible(true);
                            });
                        }catch(IOException | ClassNotFoundException ex){
                            System.out.println(ex);
                        }
                    }else{
                        System.out.println(respuesta.getMensajeInterno());
                    }
                }
                return true;
            }
        };
    }
    
    private void ajustarPantalla(){
        contenedor.widthProperty().addListener( w -> {
            ajustarAncho(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener( h -> {
            ajustarAlto(Double.NaN);
        });
    }
    
    private void ajustarAncho(Double ancho){
        gpRoot.setPrefWidth(ancho);
    }
    
    private void ajustarAlto(Double Alto){
        gpRoot.setPrefHeight(Alto);
    }

    @FXML
    private void actSeleccionarEmpleado(MouseEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Seleccionar Empleado"));
        }else{
            AppContext.getInstance().set("empSelect", null);
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", Boolean.FALSE, StageStyle.DECORATED);
            EmpleadosDTO emplSeleccionado = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if(emplSeleccionado != null){
                txtCedula.setText(emplSeleccionado.getCedula());
            }  
        }
    }
}
