/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.una.aeropuerto.util;

import java.util.Optional;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Dialog;
import javafx.stage.Window;
import javafx.util.Pair;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.dialog.ProgressDialog;
/**
 *
 * @author ivana
 */
public class Mensaje {
    

    public static void show(AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }

    public static void showModal(AlertType tipo, String titulo, Window padre, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.initOwner(padre);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
    
    public static Boolean showConfirmation(String titulo, Window padre, String mensaje) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> result = alert.showAndWait();

        return result.get() == ButtonType.OK;
    }
   
    public static Optional<Pair<String, String>> showDialogoParaCodigoGerente(String titulo){
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        ButtonType aceptar = new ButtonType("Aceptar", ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(aceptar, cancelar);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        TextField cedula = new TextField();
        TextField codigo = new TextField();
        grid.add(new Label("Ingrese la cedula del gerente"), 0, 0);
        grid.add(cedula, 1, 0);
        grid.add(new Label("Codigo del gerente"), 0, 1);
        grid.add(codigo, 1, 1);
        Node nodeAceptar = dialog.getDialogPane().lookupButton(aceptar);
        nodeAceptar.setDisable(true);
        cedula.textProperty().addListener( t -> {
            nodeAceptar.setDisable(cedula.getText().isEmpty() || codigo.getText().isEmpty());
        });
        codigo.textProperty().addListener( t -> {
            nodeAceptar.setDisable(cedula.getText().isEmpty() || codigo.getText().isEmpty());
        });
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if(dialogButton == aceptar) {
                return new Pair<>(cedula.getText(), codigo.getText());
            }
            return null;
        });
        return dialog.showAndWait();
    }
    
    public static void showProgressDialog(Task tarea, String titulo, String mensaje){
        ProgressDialog dialog = new ProgressDialog(tarea);
        dialog.setTitle(titulo);
        dialog.setHeaderText(null);
        dialog.setContentText(mensaje);
        new Thread(tarea).start();
        dialog.showAndWait();
    }
}
