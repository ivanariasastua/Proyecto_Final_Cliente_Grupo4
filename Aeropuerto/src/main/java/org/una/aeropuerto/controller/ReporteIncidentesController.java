/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import org.una.aeropuerto.util.DateUtils;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.StageStyle;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.dto.EmpleadosDTO;
import org.una.aeropuerto.service.ReporteService;
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
public class ReporteIncidentesController extends Controller implements Initializable {

    @FXML
    private DatePicker dpIni;
    @FXML
    private DatePicker dpFin;
    @FXML
    private JFXRadioButton rbActivo;
    @FXML
    private JFXRadioButton rbInactivo;
    @FXML
    private JFXRadioButton rbAmbos;
    @FXML
    private JFXTextField txtResponsable;
    @FXML
    private JFXTextField txtEmisor;

    Map<String, String> modoDesarrollo;
    private final Pane contenedor = (Pane) AppContext.getInstance().get("Contenedor");

    private final ReporteService service = new ReporteService();
    EmpleadosDTO emisorSelec;
    EmpleadosDTO responsableSelec;
    private ListView<String> lvDesarrollo;
    @FXML
    private GridPane gpRoot;
    String responsable, emisor;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datosModoDesarrollo();
        lvDesarrollo = (ListView) AppContext.getInstance().get("ListView");
        datosModoDesarrollo();
        ajustarPantalla();
    }

    public void datosModoDesarrollo() {
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista es ReporteIncidentes");
        modoDesarrollo.put("Buscar Responsable", "Buscar Responsable responde al método actBuscarResponsable");
        modoDesarrollo.put("Buscar Emisor", "Buscar Emisor responde al método actBuscarEmisor");
        modoDesarrollo.put("Generar Reporte", "Generar Reporte responde al método actGenerarReporte");
    }

    private void asignarInfoModoDesarrollo() {
        lvDesarrollo.getItems().clear();
        for (String info : modoDesarrollo.keySet()) {
            lvDesarrollo.getItems().add(modoDesarrollo.get(info));
        }
    }

    @Override
    public void initialize() {
        responsableSelec = new EmpleadosDTO();
        emisorSelec = new EmpleadosDTO();
        txtResponsable.setText(null);
        txtEmisor.setText(null);
        dpFin.setValue(null);
        dpIni.setValue(null);
        rbActivo.setSelected(true);
        rbInactivo.setSelected(false);
        rbAmbos.setSelected(false);
        asignarInfoModoDesarrollo();
        ajustarAlto(contenedor.getHeight());
        ajustarAncho(contenedor.getWidth());

    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Responsable"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        } else {
            AppContext.getInstance().set("permisoFiltrar", true);
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            responsableSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            txtResponsable.clear();
            if (responsableSelec != null) {
                txtResponsable.setText(responsableSelec.getNombre());
            }
        }
    }

    @FXML
    private void actBuscarEmisor(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Buscar Emisor"));
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
        } else {
            AppContext.getInstance().set("permisoFiltrar", true);
            FlowController.getInstance().goViewInNoResizableWindow("BuscarEmpleado", false, StageStyle.DECORATED);
            emisorSelec = (EmpleadosDTO) AppContext.getInstance().get("empSelect");
            txtEmisor.clear();
            if (emisorSelec != null) {
                txtEmisor.setText(emisorSelec.getNombre());
            }
        }
    }

    public boolean validarCampos() {
        if (dpFin.getValue() == null || dpIni.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Campos obligatorios", "Los siguientes campos son obligatorios para generar el reporte\n*Fechas\n*Estado");
            return false;
        }
        if (dpFin.getValue().isBefore(dpIni.getValue())) {
            Mensaje.show(Alert.AlertType.WARNING, "Fechas Incorrectas", "Las fechas no son correctas");
            return false;
        }
        return true;
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if (UserAuthenticated.getInstance().isRol("ADMINISTRADOR")) {
            lvDesarrollo.getSelectionModel().select(modoDesarrollo.get("Generar Reporte"));
        } else {
            AppContext.getInstance().set("Task", reporteTask());
            FlowController.getInstance().goViewCargar();
            txtEmisor.clear();
            txtResponsable.clear();
        }
    }
    
    private Task reporteTask(){
        return new Task(){
            @Override
            protected Object call() throws Exception {
                if (validarCampos()) {
                    Date ini = DateUtils.asDate(dpIni.getValue());
                    Date fin = DateUtils.asDate(dpFin.getValue());
                    Respuesta res;
                    if (rbActivo.isSelected()) {
                        res = validarBusqueda(ini, fin,true,true);
                    } else if (rbInactivo.isSelected()) {
                        res = validarBusqueda(ini, fin,false,true);
                    } else {
                        res = validarBusqueda(ini, fin,false,false);
                    }
                    if (res.getEstado()) {
                        String resp = (String) res.getResultado("Reporte");
                        byte[] bytes = Base64.getDecoder().decode(resp);
                        try {
                            ByteArrayInputStream array = new ByteArrayInputStream(bytes);
                            ObjectInputStream bytesArray = new ObjectInputStream(array);
                            JasperPrint jp = (JasperPrint) bytesArray.readObject();
                            JasperViewer viewer = new JasperViewer(jp, false);
                            viewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                            Platform.runLater(() -> {
                                viewer.setVisible(true);
                            });
                        } catch (IOException | ClassNotFoundException ex) {
                            System.out.println(ex);
                        }
                    } else {
                        System.out.println("error " + res.getMensaje());
                    }
                }
                return true;
            }
            
        };
    }

    public Respuesta validarBusqueda(Date ini, Date fin,boolean estado,boolean est) {
        Respuesta res;
        if ((txtResponsable.getText() == null && txtEmisor.getText() == null)) {
            return res = service.reporteIncident(ini, fin, estado, "null", "null", est);
        } else if ((txtResponsable.getText() != null && txtEmisor.getText() == null)) {
            return res = service.reporteIncident(ini, fin, estado, txtResponsable.getText(), "null", est);
        } else if ((txtResponsable.getText() == null && txtEmisor.getText() != null)) {
            return res = service.reporteIncident(ini, fin, estado, "null", txtEmisor.getText(), est);
        } else {
            return res = service.reporteIncident(ini, fin, estado, txtResponsable.getText(), txtEmisor.getText(), est);
        }
    }

    @FXML
    private void actActivo(MouseEvent event) {
        rbAmbos.setSelected(false);
        rbInactivo.setSelected(false);
    }

    @FXML
    private void actInactivo(MouseEvent event) {
        rbAmbos.setSelected(false);
        rbActivo.setSelected(false);
    }

    @FXML
    private void actAmbos(MouseEvent event) {
        rbActivo.setSelected(false);
        rbInactivo.setSelected(false);
    }

    private void ajustarPantalla() {
        contenedor.widthProperty().addListener(w -> {
            ajustarAncho(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener(h -> {
            ajustarAlto(contenedor.getHeight());
        });
    }

    private void ajustarAncho(Double ancho) {
        gpRoot.setPrefWidth(ancho);
    }

    private void ajustarAlto(Double Alto) {
        gpRoot.setPrefHeight(Alto);
    }

}
