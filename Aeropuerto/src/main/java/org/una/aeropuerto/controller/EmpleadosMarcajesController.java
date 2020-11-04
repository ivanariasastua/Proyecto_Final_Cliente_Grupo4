
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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
    private final SimpleDateFormat formatoHora = new SimpleDateFormat(hora);
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat(fecha);
    /*Datos para el marcaje*/
    private Date horaEntrada;
    private Date horaSalida;
    private Date fechaRegistro;
    private Integer horasLaboradas = 0;
    
    private EmpleadosHorariosDTO horarioSelect = null;
    private EmpleadosMarcajesDTO marcaje = null;
    private final EmpleadosMarcajesService service = new EmpleadosMarcajesService();
    private ZonedDateTime actual;
    private Map<String,String> modoDesarrollo;
    @FXML
    private JFXButton btnHacerMarcaje;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initTablaHorarios();
        datosModoDesarrollo();
    }    

    @Override
    public void initialize() {
        btnHacerMarcaje.setVisible(true);
        actual = ZonedDateTime.now(ZoneId.of("America/Costa_Rica"));
        System.out.println(actual);
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
            Respuesta res;
            if(marcaje.getId() == 0L)
                res = service.guardarEmpleadoMarcaje(marcaje);
            else
                res = service.modificarEmpleadoMarcaje(marcaje.getId(), marcaje);
            if(res.getEstado()){
                Mensaje.show(Alert.AlertType.INFORMATION, "Hacer Marcaje", "Marcaje exitoso");
            }else{
                Mensaje.show(Alert.AlertType.ERROR, "Hacer Marcaje", "No se pudo efectuar el marcaje");
            }
            marcaje = null;
            cleanLabel();
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Hacer Marcaje", "No ha seleccionado ningún horario");
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
        btnHacerMarcaje.setVisible(true);
        if(horarioSelect != null){
            Respuesta res = service.getLastByHorarioId(horarioSelect.getId());
            if(res.getEstado()){
                marcaje = (EmpleadosMarcajesDTO) res.getResultado("Empleados_Marcajes");
                if(marcaje == null){
                    marcaje = new EmpleadosMarcajesDTO(0L, horarioSelect, new Date(), null, new Date(), 0);
                }else if(marcaje.getHoraSalida() == null && marcaje.getHorasLaboradas() == 0){
                    marcaje.setEmpleadoHorario(horarioSelect);
                    marcaje.setHoraSalida(new Date());
                    horaEntrada = marcaje.getHoraEntrada();
                    horaSalida = marcaje.getHoraSalida();
                    calcularHorasLaboradas();
                    marcaje.setHorasLaboradas(horasLaboradas);
                }else if(marcaje.getHoraSalida() != null && marcaje.getHorasLaboradas() != 0){
                    marcaje.setEmpleadoHorario(horarioSelect);
                    LocalDateTime ldt = marcaje.getFechaRegistro().toInstant().atZone(ZoneId.of("UTC-6")).toLocalDateTime();
                    if(!compararFechas(ldt)){
                        marcaje = new EmpleadosMarcajesDTO(0L, horarioSelect, new Date(), null, new Date(), 0);
                    }else{
                        Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Horario", "Este horrio ya posee un marcaje reciente");
                        btnHacerMarcaje.setVisible(false);
                    }
                }
                cargarDatos();
            }else{
                Mensaje.show(Alert.AlertType.INFORMATION, "Seleccionar Horario", "Hubo un problema buscando el último marcaje hecho");
            }
        }else{
            Mensaje.show(Alert.AlertType.WARNING, "Seleccionar Horario", "No hay seleccionado un marcaje");
        }
    }
    
    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista EmpleadosMarcajes");
        modoDesarrollo.put("Seleccionar", "Responde al método accionSeleccionar");
        modoDesarrollo.put("Hacer Marcaje", "Responde al metodo accionHacerMarcaje");
    }
    
    public void cargarDatos(){
        if(horarioSelect != null){
            lblDetalleHorario.setText("Días: "+horarioSelect.getDiaEntrada()+" - "+horarioSelect.getDiaSalida()+" Horarios: "+formatoHora.format(horarioSelect.getHoraEntrada())+" - "+formatoHora.format(horarioSelect.getHoraSalida()));
            lblFechaRegistro.setText(formatoFecha.format(marcaje.getFechaRegistro()));
            if(marcaje.getHoraEntrada() != null)
                lblHoraEntrada.setText(formatoHora.format(marcaje.getHoraEntrada()));
            if(marcaje.getHoraSalida() != null)
                lblHoraSalida.setText(formatoHora.format(marcaje.getHoraSalida()));
            lblHorasLaboradas.setText(String.valueOf(horasLaboradas));
        }
    }
    
    public List<EmpleadosHorariosDTO> selectHorarios(List<EmpleadosHorariosDTO> horarios){
        List<EmpleadosHorariosDTO> horariosSeleccionados = new ArrayList<>();
        System.out.println(actual.getDayOfWeek().getValue());
        for(EmpleadosHorariosDTO h : horarios){
            if(dayToNumber(h.getDiaEntrada()) == actual.getDayOfWeek().getValue() || dayToNumber(h.getDiaSalida()) == actual.getDayOfWeek().getValue()){
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
            int hora_entrada = horaEntrada.toInstant().atZone(ZoneId.of("UTC-6")).toLocalDateTime().getHour();
            int hora_salida = horaSalida.toInstant().atZone(ZoneId.of("UTC-6")).toLocalDateTime().getHour();
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
    
    private void cleanLabel(){
        lblDetalleHorario.setText("");
        lblFechaRegistro.setText("");
        lblHoraEntrada.setText("");
        lblHorasLaboradas.setText("");
        lblHoraSalida.setText("");
    }
    
    private Boolean compararFechas(LocalDateTime compare){
        return compare.getYear() == actual.getYear() && compare.getMonth() == actual.getMonth() && compare.getDayOfYear() == actual.getDayOfYear();
    }
}
