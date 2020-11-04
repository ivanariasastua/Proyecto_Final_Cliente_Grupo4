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
import javafx.scene.control.DatePicker;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.service.ReporteService;
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

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        Respuesta res = service.reporteIncident(new Date(), true,"a","a");
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
            System.out.println("error "+res.getMensaje());
            System.out.println("Error: "+res.getMensajeInterno());
        }
    }
    
}
