/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.App;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.dto.TransaccionesDTO;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.service.TransaccionesService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.ReporteTransacciones;
/**
 * FXML Controller class
 *
 * @author cordo
 */
public class TransaccionesController extends Controller implements Initializable {

    @FXML private TableView  tablaTransac;
    @FXML private JFXTextField txtBuscarTransacciones;
    @FXML private DatePicker dpDesde;
    @FXML private DatePicker dpHasta;
    private final TransaccionesService service = new TransaccionesService();
    private String empleado;
    private ListView<String> lv;
    private List<ReporteTransacciones> listaReporte;
    private Map<String,String> modoDesarrollo;
    @FXML
    private GridPane gpRoot;
    
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTabla();
        datosModoDesarrollo();
        txtBuscarTransacciones.setEditable(false);
        lv = (ListView<String>) AppContext.getInstance().get("ListView");
        ajustarPantalla();
    }    

    private void initTabla(){
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        tablaTransac.getColumns().clear();
        TableColumn<TransaccionesDTO, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory((t) -> new SimpleStringProperty(t.getValue().getId().toString()));
        TableColumn<TransaccionesDTO, String> colAccion = new TableColumn<>("Acción");
        colAccion.setCellValueFactory((t) -> new SimpleStringProperty(t.getValue().getAccion()));
        TableColumn<TransaccionesDTO, String> colEmpleado = new TableColumn<>("Empleado");
        colEmpleado.setCellValueFactory((t) -> new SimpleStringProperty(t.getValue().getEmpleado().getNombre()+" "+t.getValue().getEmpleado().getCedula()));
        TableColumn<TransaccionesDTO, String> colFecha = new TableColumn<>("Fecha de Registro");
        colFecha.setCellValueFactory((t) -> new SimpleStringProperty(formato.format(t.getValue().getFechaRegistro())));
        tablaTransac.getColumns().addAll(colId, colAccion, colEmpleado, colFecha);
    }
    
    @Override
    public void initialize() {
        ajustarAlto(contenedor.getHeight());
        ajustarAncho(contenedor.getWidth());
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
                Respuesta res = service.getByFiltro(Date.from(dpDesde.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), Date.from(dpHasta.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()), empleado);
                if(res.getEstado()){
                    List<TransaccionesDTO> lista = (List<TransaccionesDTO>) res.getResultado("Transacciones");
                    tranformarDatos(lista);
                    tablaTransac.getItems().clear();
                    tablaTransac.getItems().addAll(lista);
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Buscar Transacciones", res.getMensaje());
                }
            }
        }
    }
    
    private void tranformarDatos(List<TransaccionesDTO> lista){
        listaReporte = new ArrayList<>();
        lista.forEach( transaction -> {
            listaReporte.add(new ReporteTransacciones(transaction));
        });
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
                    empleado = "null";
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
    
    private JasperPrint crearJasperPrint(){
        if(listaReporte != null && !listaReporte.isEmpty()){
            try {
                HashMap<String, Object> parametros = new HashMap<>();
                parametros.put("total", String.valueOf(listaReporte.size()));
                File file = new File (App.class.getResource("resources/rep_transacciones.jrxml").getFile());
                JasperReport report = JasperCompileManager.compileReport(file.getAbsolutePath());
                return JasperFillManager.fillReport(report, parametros, new JRBeanCollectionDataSource(listaReporte));
            } catch (JRException ex) {
                System.out.println("Error al cargar el reporte [ " + ex + " ]");
                Mensaje.show(Alert.AlertType.ERROR, "Generar Reporte", "Hubo un error generar el reporte");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Generar Reporte", "No hay datos para generar el reporte");
        }
        return null;
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            JasperPrint jp = crearJasperPrint();
            if(jp != null){
                JasperViewer viewer = new JasperViewer(jp, false);
                viewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE); 
                viewer.setVisible(true);
            }
        }
    }
    
    @FXML
    private void actBuscarEmpleado(MouseEvent event) {
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            
        }else{
            AppContext.getInstance().set("empSelect", null);
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            EmpleadosDTO emplSeleccionado = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            if(emplSeleccionado != null)
                txtBuscarTransacciones.setText(emplSeleccionado.getCedula());
        }
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
