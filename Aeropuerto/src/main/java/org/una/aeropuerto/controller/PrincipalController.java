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
import java.util.ResourceBundle;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.App;

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
    private HamburgerBackArrowBasicTransition deslizar;
    private Boolean isShow;
    private TranslateTransition tt;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        deslizar = new HamburgerBackArrowBasicTransition(hamMenu);
        tt =  new TranslateTransition(Duration.seconds(0.6));
        deslizar.setRate(1);
        deslizar.play();
        isShow = true;
        trasladar();
    }    

    @FXML
    private void accionDeslizarMenu(MouseEvent event) {
        deslizar.setRate(deslizar.getRate() * -1);
        deslizar.play();
        if(isShow){
            tt.setByX(0);
            tt.setToX(-300);
        }else{
            tt.setByX(-300);
            tt.setToX(0);
            vbMenu.getChildren().add(spMenu);
            vbMenu.setPrefWidth(300);
            vbContenedor.setPrefWidth(vbContenedor.getWidth() - 300);
        }
        tt.play();
    }

    @FXML
    private void accionGenerarCodigo(ActionEvent event) {
    }

    @FXML
    private void accionCerrarSecion(ActionEvent event) {
        if(Mensaje.showConfirmation("Cerrar Sesion", this.getStage(), "¿Seguro desea cerrar la sesion?")){
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
        } catch (FileNotFoundException ex) {
            System.out.println("Error leyendo el archivo: [ " + ex +" ]");
        } catch (IOException ex) {
            System.out.println("Error leyendo el archivo: [ " + ex +" ]");
        } 
    }

    @FXML
    private void accionMinimizar(MouseEvent event) {
        this.minimizeWindow();
    }

    @FXML
    private void accionMaximizarRestaurar(MouseEvent event) {
    }

    @FXML
    private void accionCerrar(MouseEvent event) {
        this.closeWindow();
    }

    @FXML
    private void accionServicios(ActionEvent event) {
    }

    @FXML
    private void accionRegistrarGasto(ActionEvent event) {
    }

    @FXML
    private void accionSeguimientoGasto(ActionEvent event) {
    }

    @FXML
    private void accionCategorias(ActionEvent event) {
    }

    @FXML
    private void accionRegistrarIncidente(ActionEvent event) {
    }

    @FXML
    private void accionSeguimientoIncidentes(ActionEvent event) {
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
    }

    @Override
    public void adjustWidth(double witdh) {
        lblTitulo.setPrefWidth(witdh - 599);
    }

    @Override
    public void adjustHeigth(double height) {
        spMenu.setPrefHeight(height - 50);
    }
    
    private void trasladar(){
        tt.setAutoReverse(false);
        tt.setCycleCount(1);
        tt.setDuration(Duration.seconds(1));
        tt.setNode(vbMenu);
        tt.setOnFinished(ev -> {
            if(isShow){
                vbMenu.getChildren().clear();
                vbMenu.setPrefWidth(0);
                vbContenedor.setPrefWidth(vbContenedor.getWidth() + 300);
            }
            isShow = !isShow;
        });
    }
    
}
