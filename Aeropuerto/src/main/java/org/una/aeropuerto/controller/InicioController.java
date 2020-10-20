/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.una.aeropuerto.App;
import org.una.aeropuerto.util.AppContext;

/**
 * FXML Controller class
 *
 * @author Ivan Josué Arias Astua
 */
public class InicioController extends Controller implements Initializable {

    @FXML private ImageView imvHome;
    @FXML private Label lblHome;
    private List<Image> imagenes;
    private Timer hilo;
    private int index;
    private VBox contenedor;
    private FadeTransition fade, fade2;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        index = 0;
        imagenes = new ArrayList<>();
        try{
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto1.jpg").toString()));
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto2.jpg").toString()));
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto3.jpg").toString()));
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto4.jpg").toString()));
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto5.jpg").toString()));
            imagenes.add(new Image(App.class.getResource("resources/aeropuerto6.jpg").toString()));
            imvHome.setImage(imagenes.get(0));
        }catch(Exception ex){
        }
        contenedor = (VBox) AppContext.getInstance().get("Contenedor");
        addListener();
        initFades();
        initTimer();
    }    

    @Override
    public void initialize() {
    }
    
    public void addListener(){
        contenedor.widthProperty().addListener( w -> {
            adjustWidth(contenedor.getWidth());
        });
        contenedor.heightProperty().addListener( h -> {
            adjustHeigth(contenedor.getHeight());
        });
    }

    public void adjustWidth(double witdh) {
        imvHome.setFitWidth(witdh);
        lblHome.setPrefWidth(witdh);
    }

    public void adjustHeigth(double height) {
        imvHome.setFitHeight(height);
        lblHome.setPrefHeight(height);
    }
    
    public void initTimer(){
        hilo = new Timer();
        AppContext.getInstance().set("Timer", hilo);
        hilo.schedule(new TimerTask() {
            Boolean seguir = true;
            @Override
            public void run() {
                index++;
                if(index == 6){
                    index = 0;
                }
                Platform.runLater(() -> {
                    fade.play();
                });
            }
        }, 10000, 10000);
    }
    
    public void initFades(){
        fade = new FadeTransition(Duration.seconds(1));
        fade.setFromValue(10);
        fade.setToValue(0.0);
        fade.setAutoReverse(false);
        fade.setNode(imvHome);
        fade.setOnFinished( fd -> {
            imvHome.setImage(imagenes.get(index));
            fade2.play();
        });
        fade2 = new FadeTransition(Duration.seconds(1));
        fade2.setFromValue(0.0);
        fade2.setToValue(10);
        fade2.setAutoReverse(false);
        fade2.setNode(imvHome);
    }

    @Override
    public void cargarTema() {
    }
}
