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
import java.util.Base64;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.service.ReporteService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;

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

    private final ReporteService service = new ReporteService();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @Override
    public void cargarTema() {
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
    }

    @FXML
    private void actBuscarEmisor(ActionEvent event) {
    }

    public boolean validarCampos() {
        if (dpFin.getValue() == null || dpIni.getValue() == null) {
            Mensaje.show(Alert.AlertType.WARNING, "Debe seleccionar las fechas", "Debe seleccionar el rango de fechas");
            return false;
        }
        return true;
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        if (validarCampos()) {
            Date ini = DateUtils.asDate(dpIni.getValue());
            Date fin = DateUtils.asDate(dpFin.getValue());
            Respuesta res = service.reporteIncident(ini, fin, true, txtResponsable.getText(), txtEmisor.getText());
            if (res.getEstado()) {
                String resp = (String) res.getResultado("Reporte");
                byte[] bytes = Base64.getDecoder().decode(resp);
                try {
                    ByteArrayInputStream array = new ByteArrayInputStream(bytes);
                    ObjectInputStream bytesArray = new ObjectInputStream(array);
                    JasperPrint jp = (JasperPrint) bytesArray.readObject();
                    JasperViewer viewer = new JasperViewer(jp, false);
                    viewer.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    viewer.setVisible(true);
                } catch (IOException | ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            } else {
                System.out.println("error " + res.getMensaje());
            }
        }
    }

    public boolean estadoSeleccionado(boolean estado) {
        if (estado) {

        }
        return false;
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

}
