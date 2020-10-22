
package org.una.aeropuerto.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.una.aeropuerto.dto.EmpleadosMarcajesDTO;

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
    /*Formato fechas*/
    private final String hora = "HH:mm:ss";
    private final String fecha = "yyyy-MM-dd";
    private final TimeZone timeZone = TimeZone.getTimeZone("UTC");
    private final SimpleDateFormat formatoHora = new SimpleDateFormat(hora);
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat(fecha);
    /*Datos para el marcaje*/
    private Date horaEntrada;
    private Date horaSalida;
    private Date fechaRegistro;
    private Integer horasLaboradas;
    
    private EmpleadosHorariosDTO horarioSelect = null;
    private EmpleadosMarcajesDTO marcaje = null;
    private final EmpleadosMarcajesService service = new EmpleadosMarcajesService();
    private LocalDateTime actual;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        formatoHora.setTimeZone(timeZone);
        initTablaHorarios();
    }    

    @Override
    public void initialize() {
        actual = LocalDateTime.now();
        tvHorarios.getItems().clear();
        horarioSelect = null;
        if(!selectHorarios(UserAuthenticated.getInstance().getUsuario().getHorarios()).isEmpty()) {
            tvHorarios.getItems().addAll(selectHorarios(UserAuthenticated.getInstance().getUsuario().getHorarios()));
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Hacer Marcaje", "Usted no tiene horario para hoy");
        }
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
        colDiaEntrada.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaEntrada()));
        colDiaSalida.setCellValueFactory((p) -> new SimpleStringProperty(p.getValue().getDiaSalida()));
        colHoraEntrada.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoHora.format(p.getValue().getHoraEntrada()))));
        colHoraSalida.setCellValueFactory((p) -> new SimpleStringProperty(String.valueOf(formatoHora.format(p.getValue().getHoraSalida()))));
    }

    @Override
    public void cargarTema() {
    }

    @FXML
    private void accionSeleccionar(ActionEvent event) {
        if(horarioSelect != null){
            Respuesta res = service.getLastByHorarioId(horarioSelect.getId());
            if(res.getEstado()){
                marcaje = (EmpleadosMarcajesDTO) res.getResultado("Empleados_Marcajes");
                if(marcaje == null){
                    
                }else if(marcaje.getHoraSalida() == null || marcaje.getHorasLaboradas() == null){
                    
                }else{
                    
                }
            }else{
                Mensaje.show(Alert.AlertType.INFORMATION, "Seleccionar Horario", "Hubo un problema buscando el ultimo marcaje hecho");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Horario", "No hay seleccionado un marcaje");
        }
    }
    
    public void cargarDatos(){
        if(horarioSelect != null){
            lblDetalleHorario.setText(horarioSelect.getDiaEntrada()+" - "+horarioSelect.getDiaSalida()+"Horarios: "+formatoHora.format(horarioSelect.getHoraEntrada())+" - "+formatoHora.format(horarioSelect.getHoraSalida()));
            lblFechaRegistro.setText(formatoFecha.format(fechaRegistro));
            if(horaEntrada != null)
                lblHoraEntrada.setText(formatoHora.format(horaEntrada));
            if(horaSalida != null)
                lblHoraSalida.setText(formatoHora.format(horaSalida));
            lblHorasLaboradas.setText(String.valueOf(horasLaboradas));
        }
    }
    
    public List<EmpleadosHorariosDTO> selectHorarios(List<EmpleadosHorariosDTO> horarios){
        List<EmpleadosHorariosDTO> horariosSeleccionados = new ArrayList<>();
        for(EmpleadosHorariosDTO h : horarios){
            if(dayToNumber(h.getDiaEntrada()) >= actual.getDayOfWeek().getValue() && dayToNumber(h.getDiaSalida()) <= actual.getDayOfWeek().getValue()){
                horariosSeleccionados.add(h);
            }
        }
        return horariosSeleccionados;
    }
    
    private int dayToNumber(String day){
        if(day.equals("Lunes"))
            return 1;
        else if(day.equals("Martes"))
            return 2;
        else if(day.equals("Miércoles"))
            return 3;
        else if(day.equals("Jueves"))
            return 4;
        else if(day.equals("Viernes"))
            return 5;
        else if(day.equals("Sábado"))
            return 6;
        else 
            return 7;
    }
    
    private Integer calcularHorasLaboradas(){
        if(horaEntrada != null && horaSalida != null){
            int hora_entrada = horaEntrada.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime().getHour();
            int hora_salida = horaSalida.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime().getHour();
            if(hora_entrada < hora_salida){
                return getCantidadHoras(hora_entrada, hora_salida);
            }else{
                return ((24-hora_entrada)+hora_salida);
            }
        }
        return 0;
    }
    
    private Integer getCantidadHoras(int hora1, int hora2){
        int cant = 0, aux = hora1;
        while(aux < hora2){
            cant++;
            aux++;
        }
        return cant;
    }
}
