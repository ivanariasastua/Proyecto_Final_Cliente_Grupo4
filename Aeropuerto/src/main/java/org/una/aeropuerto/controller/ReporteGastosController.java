/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.ServiciosDTO;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.service.ReporteService;
import org.una.aeropuerto.util.Mensaje;
/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class ReporteGastosController extends Controller implements Initializable {

    @FXML
    private JFXRadioButton rbP;
    @FXML
    private ToggleGroup tgEstPago;
    @FXML
    private JFXRadioButton rbNP;
    @FXML
    private JFXRadioButton rbA;
    @FXML
    private ToggleGroup tgEstGasto;
    @FXML
    private JFXRadioButton rbI;
    @FXML
    private JFXTextField txtServicio;
    @FXML
    private JFXTextField txtResponsable;
    
    private final ReporteService service = new ReporteService();

    @FXML
    private DatePicker dpFechaI;
    @FXML
    private DatePicker dpFechaF;
    
    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    @FXML
    private JFXTextField txtEmpresa;

    private String empresa, responsable, servicio;

    private Map<String,String> modoDesarrollo;
    @FXML
    private GridPane gpRoot;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
        txtResponsable.setEditable(false);
        txtServicio.setEditable(false);
        ajustarPantalla();
    }    

    @Override
    public void cargarTema() {
    }

    @Override
    public void initialize() {
        rbA.setSelected(true);
        rbP.setSelected(true);
        ajustarAlto(contenedor.getHeight());
        ajustarAncho(contenedor.getWidth());
    }

    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista ReporteGastos");
        modoDesarrollo.put("Generar Reporte", "Generar Reporte Responde al método actGenerarReporte");
    }
    
    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if(validarCampos()){
            Date fi = Date.from(dpFechaI.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant());
            Date ff = Date.from(dpFechaF.getValue().atStartOfDay(ZoneId.of("UTC")).toInstant());
            Boolean estP = obtenerValorRadioButton(Boolean.TRUE), estG = obtenerValorRadioButton(Boolean.FALSE);
            Respuesta res;
            if(estP == null && estG == null){
                res = service.reporteGastos(fi, ff, empresa, servicio, responsable);
            }else if(estP != null && estG == null){
                res = service.reporteGastos(fi, ff, empresa, servicio, estP, responsable);
            }else if(estP == null && estG != null){
                res = service.reporteGastos(fi, ff, empresa, servicio, responsable, estG);
            }else{
                res = service.reporteGastos(fi, ff, empresa, servicio, estP, estG, responsable);
            }
            if(res.getEstado()){
                String resp = (String) res.getResultado("Reporte");
                System.out.println("Exito: "+resp);
                byte[] bytes = Base64.getDecoder().decode(resp);
                try{
                    ByteArrayInputStream array = new ByteArrayInputStream(bytes);
                    ObjectInputStream bytesArray = new ObjectInputStream(array);
                    JasperPrint jp = (JasperPrint) bytesArray.readObject();
                    JasperViewer viewer = new JasperViewer(jp, false);
                    viewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
                    viewer.setVisible(true);
                }catch(IOException | ClassNotFoundException ex){
                    System.out.println(ex);
                }
            }else{
                System.out.println("Error: "+res.getMensajeInterno());
            }
        }
    }

    @FXML
    private void actBuscarServicio(ActionEvent event) {
        AppContext.getInstance().set("servSelect", null);
        FlowController.getInstance().goViewInNoResizableWindow("BuscarServicio", false, StageStyle.DECORATED);
        ServiciosDTO servicioDto = (ServiciosDTO) AppContext.getInstance().get("servSelect");
        if(servicioDto != null){
            txtServicio.setText(servicioDto.getNombre());
        }
    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        AppContext.getInstance().set("empSelect", null);
        FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        EmpleadosDTO emplSeleccionado = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
        if(emplSeleccionado != null)
            txtServicio.setText(emplSeleccionado.getCedula());
    }
    
    private Boolean validarCampos(){
        if(dpFechaI.getValue() != null && dpFechaF.getValue() != null){
            if(dpFechaI.getValue().isBefore(dpFechaF.getValue()) && (dpFechaF.getValue().isBefore(LocalDate.now())) || dpFechaF.getValue().equals(LocalDate.now())){
                if(txtEmpresa.getText() == null || txtEmpresa.getText().isEmpty())
                    empresa = "null";
                else
                    empresa = txtEmpresa.getText();
                if(txtServicio.getText() == null || txtEmpresa.getText().isEmpty())
                    servicio = "null";
                else
                    servicio = txtServicio.getText();
                if(txtResponsable.getText() == null || txtResponsable.getText().isEmpty())
                    responsable = "null";
                else
                    responsable = txtResponsable.getText();
                return true;
            }
            Mensaje.show(Alert.AlertType.WARNING, "Generar reporte", "Las fechas no son correctas");
            return false;
        }
        Mensaje.show(Alert.AlertType.WARNING, "Generar reporte", "Se requiere especificar un rango de fechas");
        return false;
    }
    
    
    
    private Boolean obtenerValorRadioButton(Boolean estadoPago){
        if(estadoPago){
            if(rbP.isSelected())
                return true;
            else if(rbNP.isSelected())
                return false;
        }else{
            if(rbA.isSelected())
                return true;
            else if(rbI.isSelected())
                return false;
        }
        return null;
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
}
