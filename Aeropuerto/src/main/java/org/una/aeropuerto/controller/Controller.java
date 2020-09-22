/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.una.aeropuerto.controller;

import java.util.HashMap;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * 
 * @author Ivan Josué Arias Astúa
 */
public abstract class Controller {
    private Stage stage;
    private String accion;
    private Scene scene;
    private final HashMap<Cursor, EventHandler<MouseEvent>> listener = new HashMap();
    private double clickSceneX, clickSceneY, valueW, valueH, clickScreenX, clickScreenY, moveX, moveY;
            
    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }
    
    public void minimizeWindow(){
        this.stage.setIconified(true);
    }
    
    public Boolean adjustWindow(){
        if(this.stage.isMaximized()){
            this.stage.setMaximized(false);
        }else{
            this.stage.setMaximized(true);
        }
        return stage.isMaximized();
    }
    
    public void closeWindow(){
        this.stage.close();
    }
    
    public void initEvents(){
        scene = stage.getScene();
        dragEvents();
        initListener();
        resizableEvents();
    }
    
    public void initListener(){
        if(stage.isResizable()){
            listener.put(Cursor.N_RESIZE, event -> {
                double newHeight = valueH - (event.getScreenY() - clickScreenY);
                if(newHeight > stage.getMinHeight()){
                    stage.setY(event.getScreenY() - clickSceneY);
                    stage.setHeight(newHeight);
                }
            });
            listener.put(Cursor.S_RESIZE, event -> {
                double newHeigth = valueH + (event.getScreenY() - clickScreenY);
                if(newHeigth > stage.getMinHeight())
                    stage.setHeight(newHeigth);
            });
            listener.put(Cursor.E_RESIZE, event -> {
                double newWidth = valueW + (event.getScreenX() - clickScreenX);
                if(newWidth > stage.getMinWidth())
                    stage.setWidth(newWidth);
            });
            listener.put(Cursor.W_RESIZE, event -> {
                double newWidth = valueW - (event.getScreenX() - clickScreenX);
                if(newWidth > stage.getMinWidth()){
                    stage.setX(event.getScreenX() - clickSceneX);
                    stage.setWidth(newWidth);
                }
            });
            listener.put(Cursor.NW_RESIZE, event -> {
                double newWidth = valueW - (event.getScreenX() - clickScreenX);
                double newHeight = valueH - (event.getScreenY() - clickScreenY);
                if(newHeight > stage.getMinHeight()){
                    stage.setY(event.getScreenY() - clickSceneY);
                    stage.setHeight(newHeight);
                }
                if(newWidth > stage.getMinWidth()){
                    stage.setX(event.getScreenX() - clickSceneX);
                    stage.setWidth(newWidth);
                }
            });
            listener.put(Cursor.NE_RESIZE, event -> {
                double newWidth = valueW + (event.getScreenX() - clickScreenX);
                double newHeight = valueH - (event.getScreenY() - clickScreenY);
                if(newHeight > stage.getMinHeight()){
                    stage.setY(event.getScreenY() - clickSceneY);
                    stage.setHeight(newHeight);
                }
                if(newWidth > stage.getMinWidth())
                    stage.setWidth(newWidth);
            });
            listener.put(Cursor.SW_RESIZE, event -> {
                double newWidth = valueW - (event.getScreenX() - clickScreenX);
                double newHeight = valueH + (event.getScreenY() - clickScreenY);
                if(newHeight > stage.getMinHeight())
                    stage.setHeight(newHeight);
                if(newWidth > stage.getMinWidth()){
                    stage.setX(event.getScreenX() - clickSceneX);
                    stage.setWidth(newWidth);
                }
            });
            listener.put(Cursor.SE_RESIZE, event -> {
                double newWidth = valueW + (event.getScreenX() - clickScreenX);
                double newHeight = valueH + (event.getScreenY() - clickScreenY);
                if(newHeight > stage.getMinHeight())
                    stage.setHeight(newHeight);
                if(newWidth > stage.getMinWidth())
                    stage.setWidth(newWidth);
            });
        }
        listener.put(Cursor.DEFAULT, event -> {
            stage.setX(event.getScreenX() + moveX);
            stage.setY(event.getScreenY() + moveY);
        });
    }
    
    public void dragEvents(){
        scene.setOnMousePressed( MousePressed -> {
            clickSceneX = MousePressed.getSceneX();
            clickSceneY = MousePressed.getSceneY();
            clickScreenX = MousePressed.getScreenX();
            clickScreenY = MousePressed.getScreenY();
            moveX = stage.getX() - clickScreenX;
            moveY = stage.getY() - clickScreenY;
            valueW = stage.getWidth();
            valueH = stage.getHeight();
        });
        scene.setOnMouseMoved( MouseMoved -> {
            double posX = MouseMoved.getSceneX(), posY = MouseMoved.getSceneY();
            boolean isUp = posY >= 0 && posY < 10, 
                    isDown = posY <= scene.getHeight() && posY > scene.getHeight() - 10, 
                    isLeft = posX >= 0 && posX < 10, 
                    isRight = posX <= scene.getWidth() && posX > scene.getWidth() - 10;
            if(isUp){
                if(isLeft){
                    ejecutarAccion(Cursor.NW_RESIZE);
                }else if(isRight){
                    ejecutarAccion(Cursor.NE_RESIZE);
                }else{
                    ejecutarAccion(Cursor.N_RESIZE);
                }
            }else if(isDown){
                if(isLeft){
                    ejecutarAccion(Cursor.SW_RESIZE);
                }else if(isRight){
                    ejecutarAccion(Cursor.SE_RESIZE);
                }else{
                    ejecutarAccion(Cursor.S_RESIZE);
                }
            }else if(isLeft){
                ejecutarAccion(Cursor.W_RESIZE);
            }else if(isRight){
                ejecutarAccion(Cursor.E_RESIZE);
            }else{
                ejecutarAccion(Cursor.DEFAULT);
            }
        });
    }
    
    public void resizableEvents(){
        scene.widthProperty().addListener( width -> {
            adjustWidth(scene.getWidth());
        });
        scene.heightProperty().addListener( height -> {
            adjustHeigth(scene.getHeight());
        });
        
    }
    
    private void ejecutarAccion(Cursor cursor){
        scene.setCursor(cursor);
        scene.setOnMouseDragged(listener.get(cursor));
    }
    
    public abstract void initialize();
    
    public abstract void adjustWidth(double witdh);
    
    public abstract void adjustHeigth(double height);
}

