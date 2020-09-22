/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.una.aeropuerto.util;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.una.aeropuerto.App;
import org.una.aeropuerto.controller.Controller;

/**
 * 
 * @author Ivan Josué Arias Astúa
 */
public class FlowController {
    
    private static FlowController INSTANCE = null;
    private static Stage mainStage;
    
    private static HashMap<String, FXMLLoader> loaders = new HashMap<>();

    private FlowController() {
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (FlowController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlowController();
                }
            }
        }
    }
    
    public static FlowController getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    private FXMLLoader getLoader(String name) {
        FXMLLoader loader = loaders.get(name);
        if (loader == null) {
            synchronized (FlowController.class) {
                loader = getFXMLLoader(name);
            }
        }
        return loader;
    }
    
    private FXMLLoader getFXMLLoader(String name){
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(App.class.getResource(name + ".fxml"));
            loader.load();
            loaders.put(name, loader);
        } catch (IOException ex) {
            loader = null;
            System.out.println("Error al obtener FXML: "+ex);
        }
        return loader;
    }
    
    public void cargarFXMLLoader(String name){
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(App.class.getResource(name + ".fxml"));
            loader.load();
            loaders.put(name, loader);
        } catch (IOException ex) {
            loader = null;
            System.out.println("Error al cargar FXML: "+ex);
        }
    }

    public void goMain() {
        try {
            AppContext.getInstance().set("mainStage", mainStage);
            FXMLLoader loader = getLoader("Principal");
            Controller controller = loader.getController();
            mainStage.setScene(new Scene(loader.getRoot()));
            controller.initialize();
            controller.setStage(mainStage);
            mainStage.setOnCloseRequest(E -> { E.consume(); });
            mainStage.setMaximized(false);
            mainStage.setResizable(true);
            mainStage.setTitle("");
            mainStage.centerOnScreen();
            mainStage.show();
            
        } catch (Exception ex) {
            System.out.println("Error en vista base: "+ex);
        }
    }

    public void goViewPanel(VBox panel, String viewName){
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setAccion(null);
        controller.initialize();
        Stage stage = controller.getStage();
        panel.getChildren().clear();
        panel.getChildren().add(loader.getRoot());
    }
    
    public void goViewInStage(String viewName, Stage stage) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setStage(stage);
        stage.getScene().setRoot(loader.getRoot());
    }

    public void goViewInNoResizableWindow(String viewName, Boolean show, StageStyle style, double minWidth, double minHeight){
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        Stage stage = new Stage();/*
        stage.setTitle("");
        stage.getIcons().add(new Image(""));
        */
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(Boolean.FALSE);
        stage.initStyle(style);
        stage.setMinWidth(minWidth);
        stage.setMinHeight(minHeight);
        stage.sizeToScene();
        controller.setStage(stage);
        controller.initialize();
        controller.initEvents();
        System.out.println("Medidas: "+stage.getWidth()+" - "+stage.getHeight());
        if(show)
            stage.show();
        else
            stage.showAndWait();
    }
    
    public void goViewInResizableWindow(String viewName, double maxWidth, double minWidth, double maxHeight, double minHeight, Boolean show, StageStyle style){
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        Stage stage = new Stage();
        /*
        stage.getIcons().add(new Image(""));
        stage.setTitle("");
        */
        if(maxHeight >= minHeight)
            stage.setMaxHeight(maxHeight+40);
        stage.setMinHeight(minHeight+40);
        if(maxWidth >= minWidth)
            stage.setMaxWidth(maxWidth);
        stage.setMinWidth(minWidth);
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        if(viewName.equals("Principal")){
            stage.setOnCloseRequest( c -> {
                c.consume();
            });
        }
        controller.setStage(stage);
        controller.initialize();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setResizable(true);
        stage.sizeToScene();
        if(show)
            stage.show();
        else
            stage.showAndWait();
    }

    public Controller getController(String viewName) {
        return getLoader(viewName).getController();
    }
    
    public void initialize() {
        loaders.clear();
    }

    public void salir() {
        mainStage.close();
    }

    public FXMLLoader getNewLoader(String name){
        return getFXMLLoader(name);
    }
    
    public Stage getMainStage(){
        return mainStage;
    }
    
    public Boolean hayLoader(String nombre){
        FXMLLoader load = loaders.get(nombre);
        return load != null;
    }
    
    public void eliminarLoader(String name){
        if(hayLoader(name)){
            loaders.remove(name);
        }
    }
    
    public void clear(){
        loaders.clear();
    }
}
