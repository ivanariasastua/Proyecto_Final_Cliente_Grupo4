/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.exit;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.App;
import org.una.aeropuerto.dto.AuthenticationRequest;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.UserAuthenticated;
import org.una.aeropuerto.service.ParametrosSistemaService;
import org.una.aeropuerto.dto.ParametrosSistemaDTO;
import org.una.aeropuerto.util.Respuesta;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class PrincipalController extends Controller implements Initializable {

    @FXML private JFXHamburger hamMenu;
    @FXML private MenuButton smUser;
    @FXML private Label lblCedula1;
    @FXML private Label lblRol1;
    @FXML private MenuItem miCodigo1;
    @FXML private ImageView imvDark;
    @FXML private JFXToggleButton tbTema;
    @FXML private ImageView imvLight;
    @FXML private ImageView imvMaximizarRestaurar;
    @FXML private Label lblTitulo;
    @FXML private ScrollPane spMenu;
    @FXML private VBox vbMenu;
    @FXML private BorderPane bpPrincipal;

    private HamburgerBackArrowBasicTransition deslizar;
    private Boolean isShow = false;
    private TranslateTransition tt;
    private AuthenticationRequest authetication;
    private final ParametrosSistemaService service = new ParametrosSistemaService();
    private Map<String,String> modoDesarrollo;
    
    @FXML
    private TitledPane tpReportes;
    @FXML
    private TitledPane tpAdministracion;
    @FXML
    private TitledPane tpTransacciones;
    @FXML
    private TitledPane tpInicio;
    @FXML
    private TitledPane tpEmpleados;
    @FXML
    private TitledPane tpAreas;
    @FXML
    private TitledPane tpGastos;
    @FXML
    private TitledPane tpIncidentes;
    @FXML
    private VBox vbDesarrollo;
    @FXML
    private ListView<String> lvDesarrollo;
    @FXML
    private Pane paneContenerdor;
    
    private ObservableList<TitledPane> menu;
    @FXML
    private Accordion acMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppContext.getInstance().set("Contenedor", paneContenerdor);
        deslizar = new HamburgerBackArrowBasicTransition(hamMenu);
        tt = new TranslateTransition(Duration.seconds(0.6));
        deslizar.setRate(1);
        deslizar.play();
        isShow = true;
        trasladar();
        addListener();
        if(UserAuthenticated.getInstance().isRol("GERENTE"))
            crearCodigoGerente();
        AppContext.getInstance().set("ListView", lvDesarrollo);
        datosModoDesarrollo();
        menu = FXCollections.observableArrayList(acMenu.getPanes());
    }

    @FXML
    private void accionDeslizarMenu(MouseEvent event) {
        deslizar.setRate(deslizar.getRate() * -1);
        deslizar.play();
        if (isShow) {
            tt.setByX(0);
            tt.setToX(-300);
        } else {
            tt.setByX(-300);
            tt.setToX(0);
            paneContenerdor.setPrefWidth(paneContenerdor.getWidth() - 300);
            vbMenu.setPrefWidth(300);
            vbMenu.getChildren().add(spMenu);
        }
        tt.play();
    }

    @FXML
    private void accionGenerarCodigo(ActionEvent event) {
        crearCodigoGerente();
    }
    
    public void datosModoDesarrollo(){
        modoDesarrollo = new HashMap();
        modoDesarrollo.put("Vista", "Nombre de la vista es Principal");
        modoDesarrollo.put("hamMenu", "hamMenu responde al método accionDeslizarMenu");
        modoDesarrollo.put("tpInicio", "tpInicio responde al método accionInicio");
        modoDesarrollo.put("tpEmpleados", "tpEmpleados responde al método accionEmpleados");
        modoDesarrollo.put("tpAreas", "tpAreas responde al método accionAreasTrabajos");
        modoDesarrollo.put("Servicios", "Servicios responde al método accionServicios");
        modoDesarrollo.put("Registro de Gastos", "Registro de Gastos responde al método accionRegistrarGasto");
        modoDesarrollo.put("Categorias", "Categorias responde al método accionCategorias");
        modoDesarrollo.put("Registro de Incidentes", "Registro de Incidentes responde al método accionRegistrarIncidente");
        modoDesarrollo.put("Gastos de Servicios", "Gastos de Servicios responde al método accionReporteGastos");
        modoDesarrollo.put("Incidentes Registrados", "Incidentes Registrados responde al método accionReporteIncidentes");
        modoDesarrollo.put("Horas Laboradas", "Horas Laboradas responde al método accionHoraLaboradas");
        modoDesarrollo.put("Párametros del Sistema", "Párametros del Sistema responde al método actParametros");
        modoDesarrollo.put("Autorizar Roles", "Autorizar Roles responde al método actAutorizarRoles");
        modoDesarrollo.put("tpTransacciones", "tpTransacciones responde al método accionTransacciones");
    }
    
    public void crearCodigoGerente(){
        if(UserAuthenticated.getInstance().isRol("GERENTE")){
            String codigo = generarCodigo();
            Respuesta res = service.getByCodigoIdentificador(UserAuthenticated.getInstance().getUsuario().getCedula());
            ParametrosSistemaDTO param = null;
            if(!res.getEstado()){
                param = new ParametrosSistemaDTO(0L, codigo, "Codigo para autorizar gestores del gerente "+UserAuthenticated.getInstance().getUsuario().getCedula(), true, UserAuthenticated.getInstance().getUsuario().getCedula(), new Date(), new Date());
                res = service.guardarParametro(param);
                if(res.getEstado()){
                    param = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                }else{
                    Mensaje.show(Alert.AlertType.ERROR, "Generar Código", "Hubo un error al generar el código: "+res.getMensaje());
                }
            }else{
                param = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                if(compararFechas(param.getFechaModificacion())){
                    param.setValor(codigo);
                    res = service.modificarParametro(param.getId(), param);
                    if(res.getEstado()){
                        param = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                    }else{
                        Mensaje.show(Alert.AlertType.ERROR, "Generar Código", "Hubo un error al generar el código: "+res.getMensaje());
                    }
                }
            }
            if(param != null){
                if(param.getId() != null || param.getId() > 0L){
                    if(AppContext.getInstance().get("CodigoGerente") != null)
                        Mensaje.show(Alert.AlertType.INFORMATION, "Generar Código", "Este es su código:\n"+param.getValor());
                    AppContext.getInstance().set("CodigoGerente", param.getValor());
                }
            }
        }
    }

    @FXML
    private void accionCerrarSecion(ActionEvent event) {
        if (Mensaje.showConfirmation("Cerrar Sesion", this.getStage(), "¿Seguro desea cerrar la sesión?")) {
            FlowController.getInstance().goViewInNoResizableWindow("LogIn", Boolean.TRUE, StageStyle.UNDECORATED);
            exit(1);
        }
    }

    @FXML
    private void accionTema(ActionEvent event) {
        PrintWriter pw = null;
        try {
            String tema = tbTema.isSelected() ? "Tema_Claro.css" : "Tema_Oscuro.css";
            FileWriter file = new FileWriter(App.class.getResource("resources/config.txt").getFile());
            file.write(tema);
            file.flush();
            file.close();
            AppContext.getInstance().set("Tema", tema);
        } catch (FileNotFoundException ex) {
            System.out.println("Error leyendo el archivo: [ " + ex + " ]");
        } catch (IOException ex) {
            System.out.println("Error leyendo el archivo: [ " + ex + " ]");
        }
    }

    @FXML
    private void accionMinimizar(MouseEvent event) {
        this.minimizeWindow();
    }

    @FXML
    private void accionMaximizarRestaurar(MouseEvent event) {
        this.adjustWindow();
    }

    @FXML
    private void accionCerrar(MouseEvent event) {
        if (Mensaje.showConfirmation("Cerrar Ventana", this.getStage(), "¿Seguro desea cerrar la ventana?")) {
            this.getStage().close();
            Timer t = (Timer) AppContext.getInstance().get("Timer");
            t.cancel();
        }
    }

    @FXML
    private void accionServicios(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "Servicios");
    }

    @FXML
    private void accionRegistrarGasto(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "GastosServicios");
    }


    @FXML
    private void accionCategorias(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "CategoriasIncidentes");

    }

    @FXML
    private void accionRegistrarIncidente(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "IncidentesRegistrados");
    }

    @FXML
    private void accionReporteGastos(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "ReporteGastos");
    }

    @FXML
    private void accionReporteIncidentes(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "ReporteIncidentes");
    }

    @FXML
    private void accionHoraLaboradas(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "ReporteHorasLaboradas");
    }

    @Override
    public void initialize() {
        FlowController.getInstance().goViewPanel(paneContenerdor, "Inicio");
        try {
            miCodigo1.setVisible(UserAuthenticated.getInstance().getRol().getNombre().equals("GERENTE") || UserAuthenticated.getInstance().isRol("ADMINISTRADOR"));
        } catch (Exception ex) {
        }
        smUser.setText(UserAuthenticated.getInstance().getUsuario().getNombre());
        lblCedula1.setText(UserAuthenticated.getInstance().getUsuario().getCedula());
        lblRol1.setText(UserAuthenticated.getInstance().getRol().getNombre());
        visualizarTitledPane();
    }

    @FXML
    private void accionInicio(MouseEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "Inicio");
    }

    @FXML
    private void accionEmpleados(MouseEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "Empleados");
    }

    @FXML
    private void accionAreasTrabajos(MouseEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "AreasTrabajos");
    }

    @FXML
    private void accionTransacciones(MouseEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "Transacciones");
    }

    @FXML
    private void accionParametros(MouseEvent event) {
    }

    public void addListener() {
        bpPrincipal.widthProperty().addListener(w -> {
            adjustWidth(bpPrincipal.getWidth());
        });
        bpPrincipal.heightProperty().addListener(h -> {
            adjustHeigth(bpPrincipal.getHeight());
        });
    }

    public void adjustWidth(double witdh) {
        lblTitulo.setPrefWidth(witdh - 599);
        if (isShow) {
            paneContenerdor.setPrefWidth(witdh - 300);
        } else {
            paneContenerdor.setPrefWidth(witdh);
        }
    }

    public void adjustHeigth(double height) {
        spMenu.setPrefHeight(height - 50);
        paneContenerdor.setPrefHeight(height - 50);
    }

    private void trasladar() {
        tt.setAutoReverse(false);
        tt.setCycleCount(1);
        tt.setDuration(Duration.seconds(0.6));
        tt.setNode(vbMenu);
        tt.setOnFinished(ev -> {
            if (isShow) {
                vbMenu.getChildren().clear();
                vbMenu.setPrefWidth(0);
                paneContenerdor.setPrefWidth(paneContenerdor.getWidth() + 300);
            }
            isShow = !isShow;
        });
    }

    @FXML
    private void actParametros(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "ParametrosSistema");
    }

    @FXML
    private void actAutorizarRoles(ActionEvent event) {
        FlowController.getInstance().goViewPanel(paneContenerdor, "HabilitarUsuarios");
    }

    @FXML
    private void accionHacerMarcaje(ActionEvent event) {
        FlowController.getInstance().goViewInNoResizableWindow("EmpleadosMarcajes", false, StageStyle.DECORATED);
    }

    private String generarCodigo(){
        String codigo = "";
        int cont = 0, aux = 0;
        char caracter;
        while(cont < 25){
            caracter = (char) (Math.floor(Math.random()*74) + 48);
            aux = caracter;
            System.out.println(aux+" : "+caracter);
            if((aux >= 65 && aux <= 90) || (aux >= 97 && aux <= 122) || (aux >= 48 && aux <= 57)){
                codigo += caracter;
                cont++;
            }
        }
        return codigo;
    }
    
    public Boolean compararFechas(Date date){
        LocalDateTime fecha = date.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime hoy = LocalDateTime.now();
        int diferencia = hoy.getDayOfYear() - fecha.getDayOfYear();
        if(diferencia > 1){
            return true;
        }else if(diferencia == 1){
            return hoy.getHour() - fecha.getHour() <= 0;
        }
        return false;
    }

    @Override
    public void cargarTema() {
    }
    
    private void visualizarTitledPane(){
        acMenu.getPanes().clear();
        acMenu.getPanes().addAll(menu);
        if(UserAuthenticated.getInstance().isRol("AUDITOR")){
            acMenu.getPanes().remove(tpEmpleados);
            acMenu.getPanes().remove(tpAreas);
            acMenu.getPanes().remove(tpGastos);
            acMenu.getPanes().remove(tpIncidentes);
        }
        if(UserAuthenticated.getInstance().isRol("GESTOR")){
            acMenu.getPanes().remove(tpReportes);
        }
        if(!UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            acMenu.getPanes().remove(tpAdministracion);
        }
        if(!UserAuthenticated.getInstance().isRol("AUDITOR") && !UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            acMenu.getPanes().remove(tpTransacciones);
        }
        if(UserAuthenticated.getInstance().isRol("ADMINISTRADOR")){
            lvDesarrollo.setVisible(true);
            vbDesarrollo.setVisible(true);
            lvDesarrollo.setPrefWidth(250);
            vbDesarrollo.setPrefWidth(250);
        }else{
            lvDesarrollo.setVisible(false);
            vbDesarrollo.setVisible(false);
            lvDesarrollo.setPrefWidth(0);
            vbDesarrollo.setPrefWidth(0);
        }
    }
    
    public void closeWindow(){
        exit(1);
    }
}
