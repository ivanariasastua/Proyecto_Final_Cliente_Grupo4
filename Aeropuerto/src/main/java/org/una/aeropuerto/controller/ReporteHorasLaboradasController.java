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

public class ReporteHorasLaboradasController extends Controller implements Initializable{

    @FXML
    private JFXTextField txtCedula;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFinal;
    
    ReporteService reporteService;

    @Override
    public void initialize() {
        
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        reporteService = new ReporteService();
    }

    public boolean validarDatos(){
        if(dpInicio.getValue() == null || dpFinal.getValue() == null){
            Mensaje.show(Alert.AlertType.WARNING, "Datos Faltantes", "Falta una fecha para generar el reporte\n"
                                                + "Seleccione el rango de fechas para generar el reporte");
            return false;
        }
        return true;
    }
    
    @FXML
    private void generarReporte(ActionEvent event) {
        if(validarDatos()){
            Date fecha1 = Date.from(dpInicio.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Date fecha2 = Date.from(dpFinal.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Respuesta respuesta = reporteService.reporteHorasLaboradas(txtCedula.getText(), fecha1, fecha2);
            if(respuesta.getEstado()){
                String res = (String) respuesta.getResultado("Reporte");
                byte[] bytes = Base64.getDecoder().decode(res);
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
                System.out.println(respuesta.getMensajeInterno());
            }
        }
    }
    
    @Override
    public void cargarTema() {
        
    }
}
