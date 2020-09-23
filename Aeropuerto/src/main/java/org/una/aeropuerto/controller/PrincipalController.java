/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXToggleButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.una.aeropuerto.util.FlowController;
import org.una.aeropuerto.util.Mensaje;
import org.una.aeropuerto.App;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class PrincipalController extends Controller implements Initializable {

    @FXML
    private JFXHamburger hamMenu;
    @FXML
    private MenuButton smUser;
    @FXML
    private Label lblCedula1;
    @FXML
    private Label lblRol1;
    @FXML
    private MenuItem miCodigo1;
    @FXML
    private ImageView imvDark;
    @FXML
    private JFXToggleButton tbTema;
    @FXML
    private ImageView imvLight;
    @FXML
    private ImageView imvMaximizarRestaurar;
    @FXML
    private VBox vbContenedor;
    @FXML
    private Label lblTitulo;
    @FXML
    private ScrollPane spMenu;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    @FXML
    private void accionDeslizarMenu(MouseEvent event) {
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
    
}
