package org.una.aeropuerto;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.stage.StageStyle;
import org.una.aeropuerto.util.AppContext;
import org.una.aeropuerto.util.FlowController;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        AppContext.getInstance();
        FlowController.getInstance().goViewInNoResizableWindow("LogIn", Boolean.TRUE, StageStyle.UNDECORATED);
    }

    public static void main(String[] args) {
        launch();
    }

}