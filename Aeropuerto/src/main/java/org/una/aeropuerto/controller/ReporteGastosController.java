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
import javafx.scene.control.ToggleGroup;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.service.ReporteService;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class ReporteGastosController extends Controller implements Initializable {

    @FXML
    private JFXRadioButton rbAntesDe;
    @FXML
    private ToggleGroup tgFechas;
    @FXML
    private DatePicker dpFecha;
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

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @Override
    public void cargarTema() {
    }

    @Override
    public void initialize() {
    }

    @FXML
    private void actGenerarReporte(ActionEvent event) {
        Respuesta res = service.reporteGastosFechaAntesDe(new Date(), "a", "a", true, true, "a");
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

    @FXML
    private void actBuscarServicio(ActionEvent event) {
    }

    @FXML
    private void actBuscarResponsable(ActionEvent event) {
    }
    
}
