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
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
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
    @FXML private VBox vbContenedor;
    @FXML private Label lblTitulo;
    @FXML private ScrollPane spMenu;
    @FXML private VBox vbMenu;
    @FXML private BorderPane bpPrincipal;

    private HamburgerBackArrowBasicTransition deslizar;
    private Boolean isShow = false;
    private TranslateTransition tt;
    private AuthenticationRequest authetication;
    private final ParametrosSistemaService service = new ParametrosSistemaService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppContext.getInstance().set("Contenedor", vbContenedor);
        FlowController.getInstance().goViewPanel(vbContenedor, "Inicio");
        deslizar = new HamburgerBackArrowBasicTransition(hamMenu);
        tt = new TranslateTransition(Duration.seconds(0.6));
        deslizar.setRate(1);
        deslizar.play();
        isShow = true;
        trasladar();
        addListener();
        if(UserAuthenticated.getInstance().isRol("GERENTE"))
            crearCodigoGerente();
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
            vbContenedor.setPrefWidth(vbContenedor.getWidth() - 300);
            vbMenu.setPrefWidth(300);
            vbMenu.getChildren().add(spMenu);
        }
        tt.play();
    }

    @FXML
    private void accionGenerarCodigo(ActionEvent event) {
        crearCodigoGerente();
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
                    Mensaje.show(Alert.AlertType.ERROR, "Generar Condigo", "Hubo un error al generar el codigo: "+res.getMensaje());
                }
            }else{
                param = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                if(compararFechas(param.getFechaModificacion())){
                    param.setValor(codigo);
                    res = service.modificarParametro(param.getId(), param);
                    if(res.getEstado()){
                        param = (ParametrosSistemaDTO) res.getResultado("Parametros_Sistema");
                    }else{
                        Mensaje.show(Alert.AlertType.ERROR, "Generar Condigo", "Hubo un error al generar el codigo: "+res.getMensaje());
                    }
                }
            }
            if(param != null){
                if(param.getId() != null || param.getId() > 0L){
                    if(AppContext.getInstance().get("CodigoGerente") != null)
                        Mensaje.show(Alert.AlertType.INFORMATION, "Generar Codigo", "Este es su codigo:\n"+param.getValor());
                    AppContext.getInstance().set("CodigoGerente", param.getValor());
                }
            }
        }
    }

    @FXML
    private void accionCerrarSecion(ActionEvent event) {
        if (Mensaje.showConfirmation("Cerrar Sesion", this.getStage(), "¿Seguro desea cerrar la sesion?")) {
            FlowController.getInstance().goViewInNoResizableWindow("LogIn", Boolean.TRUE, StageStyle.UNDECORATED);
            this.closeWindow();
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
        FlowController.getInstance().goViewPanel(vbContenedor, "Servicios");
    }

    @FXML
    private void accionRegistrarGasto(ActionEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "GastosServicios");
    }


    @FXML
    private void accionCategorias(ActionEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "CategoriasIncidentes");

    }

    @FXML
    private void accionRegistrarIncidente(ActionEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "IncidentesRegistrados");
    }

    @FXML
    private void accionReporteGastos(ActionEvent event) {
    }

    @FXML
    private void accionReporteIncidentes(ActionEvent event) {
    }

    @FXML
    private void accionHoraLaboradas(ActionEvent event) {
    }

    @Override
    public void initialize() {
        try {
            miCodigo1.setVisible(UserAuthenticated.getInstance().getRol().getNombre().equals("GERENTE"));
        } catch (Exception ex) {
        }
        smUser.setText(UserAuthenticated.getInstance().getUsuario().getNombre());
        lblCedula1.setText(UserAuthenticated.getInstance().getUsuario().getCedula());
        lblRol1.setText(UserAuthenticated.getInstance().getRol().getNombre());
    }

    @FXML
    private void accionInicio(MouseEvent event) {
    }

    @FXML
    private void accionEmpleados(MouseEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "Empleados");
    }

    @FXML
    private void accionAreasTrabajos(MouseEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "AreasTrabajos");
    }

    @FXML
    private void accionRegistrarGastos(MouseEvent event) {

    }

    @FXML
    private void accionTransacciones(MouseEvent event) {
        FlowController.getInstance().goViewPanel(vbContenedor, "Transacciones");
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
            vbContenedor.setPrefWidth(witdh - 300);
        } else {
            vbContenedor.setPrefWidth(witdh);
        }
    }

    public void adjustHeigth(double height) {
        spMenu.setPrefHeight(height - 50);
        vbContenedor.setPrefHeight(height - 50);
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
                vbContenedor.setPrefWidth(vbContenedor.getWidth() + 300);
            }
            isShow = !isShow;
        });
    }

    @FXML
    private void actParametros(ActionEvent event) {
    }

    @FXML
    private void actAutorizarRoles(ActionEvent event) {
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
}
