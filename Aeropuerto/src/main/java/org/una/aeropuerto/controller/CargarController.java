/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressIndicator;
import org.una.aeropuerto.util.AppContext;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class CargarController extends Controller implements Initializable {

    @FXML
    private ProgressIndicator piCargar;


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
        Task tarea = (Task) AppContext.getInstance().get("Task");
        tarea.setOnSucceeded( t -> {
            System.out.println("Fin");
            Platform.runLater(() -> {
                this.closeWindow();
            });
        });
        Thread hilo = new Thread(tarea);
        hilo.start();
    }
    
}
