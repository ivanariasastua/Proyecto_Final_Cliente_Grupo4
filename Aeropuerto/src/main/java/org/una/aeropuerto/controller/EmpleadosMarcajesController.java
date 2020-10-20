
package org.una.aeropuerto.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.una.aeropuerto.dto.EmpleadosHorariosDTO;
import org.una.aeropuerto.service.EmpleadosMarcajesService;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.util.Respuesta;
import org.una.aeropuerto.util.UserAuthenticated;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class EmpleadosMarcajesController extends Controller implements Initializable {

    @FXML private TableView<EmpleadosHorariosDTO> tvHorarios;
    @FXML private TableColumn<EmpleadosHorariosDTO, String> colDiaEntrada;
    @FXML private TableColumn<EmpleadosHorariosDTO, String> colDiaSalida;
    @FXML private TableColumn<EmpleadosHorariosDTO, String> colHoraEntrada;
    @FXML private TableColumn<EmpleadosHorariosDTO, String> colHoraSalida;
    @FXML private Label lblDetalleHorario;
    @FXML private Label lblFechaRegistro;
    @FXML private Label lblHoraEntrada;
    @FXML private Label lblHoraSalida;
    @FXML private Label lblHorasLaboradas;
    
    private EmpleadosHorariosDTO horarioSelect;
    private final EmpleadosMarcajesService service = new EmpleadosMarcajesService();

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTablaHorarios();
    }    

    @Override
    public void initialize() {
        tvHorarios.getItems().clear();
        horarioSelect = null;
    }

    @FXML
    private void accionTabla(MouseEvent event) {
        if(tvHorarios.getSelectionModel().getSelectedItem() != null){
            horarioSelect = tvHorarios.getSelectionModel().getSelectedItem();
        }
    }

    @FXML
    private void accionHacerMarcaje(ActionEvent event) {
        if(horarioSelect != null){
            
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Hacer Marcaje", "No ha seleccionado ningun horario");
        }
    }
    
    public void initTablaHorarios(){
        String pattern = "HH:mm:ss";
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        simpleDateFormat.setTimeZone(timeZone);
        colDiaEntrada.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaEntrada()));
        colDiaSalida.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaSalida()));
        colHoraEntrada.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(simpleDateFormat.format(p.getValue().getHoraEntrada()))));
        colHoraSalida.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(simpleDateFormat.format(p.getValue().getHoraSalida()))));
    }

    @Override
    public void cargarTema() {
    }
    
}
