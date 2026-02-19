package com.refork.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class ClienteGUIApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SecureFork - Cliente GUI (mínimo)");

        // Formulario de conexión
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        TextField hostField = new TextField("localhost");
        TextField portField = new TextField("5555");
        TextField userField = new TextField("admin");
        PasswordField passField = new PasswordField();
        passField.setText("1234");

        form.add(new Label("Host:"), 0, 0);
        form.add(hostField, 1, 0);
        form.add(new Label("Puerto:"), 0, 1);
        form.add(portField, 1, 1);
        form.add(new Label("Usuario:"), 0, 2);
        form.add(userField, 1, 2);
        form.add(new Label("Contraseña:"), 0, 3);
        form.add(passField, 1, 3);

        Button connectBtn = new Button("Conectar");

        HBox actions = new HBox(8, connectBtn);
        actions.setPadding(new Insets(0, 0, 10, 10));

        // Área de terminal (solo demo)
        TextArea terminalArea = new TextArea();
        terminalArea.setEditable(false);
        terminalArea.setFont(Font.font("Monospaced", 12));
        terminalArea.setPrefRowCount(20);

        TextField inputField = new TextField();
        inputField.setPromptText("Escribe un comando y pulsa Enter (demo)");

        inputField.setOnAction(evt -> {
            String cmd = inputField.getText();
            if (cmd != null && !cmd.isBlank()) {
                terminalArea.appendText("$ " + cmd + "\n");
                terminalArea.appendText("(respuesta demo) Ejecutado: " + cmd + "\n\n");
                inputField.clear();
            }
        });

        connectBtn.setOnAction(evt -> {
            terminalArea.appendText("Intentando conectar a " + hostField.getText() + ":" + portField.getText() + " como " + userField.getText() + "...\n");
            terminalArea.appendText("(demo) Conexión simulada OK\n\n");
        });

        BorderPane root = new BorderPane();
        root.setTop(form);
        root.setCenter(terminalArea);
        root.setBottom(inputField);
        BorderPane.setMargin(form, new Insets(10));
        BorderPane.setMargin(terminalArea, new Insets(10));
        BorderPane.setMargin(inputField, new Insets(10));

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

