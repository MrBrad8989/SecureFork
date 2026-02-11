package com.refork.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ClienteGUIApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("SecureFork - Cliente SSH Seguro");

        // Panel de conexión
        VBox panelConexion = crearPanelConexion(primaryStage);

        Scene scene = new Scene(panelConexion, 500, 400);

        // Cargar CSS si existe
        try {
            var cssResource = getClass().getResource("/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        } catch (Exception ex) {
            System.out.println("⚠️ No se pudo cargar styles.css, usando estilos por defecto");
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox crearPanelConexion(Stage stage) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(30));
        panel.setAlignment(Pos.CENTER);
        panel.getStyleClass().add("panel-conexion");

        Label titulo = new Label("🔐 Conexión Segura SSH");
        titulo.getStyleClass().add("titulo");

        // Campos de entrada
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        Label lblHost = new Label("Host:");
        TextField txtHost = new TextField("localhost");
        txtHost.setPromptText("Dirección del servidor");

        Label lblPuerto = new Label("Puerto:");
        TextField txtPuerto = new TextField("5555");
        txtPuerto.setPromptText("Puerto SSL");

        Label lblUsuario = new Label("Usuario:");
        TextField txtUsuario = new TextField("admin");
        txtUsuario.setPromptText("Nombre de usuario");

        Label lblPassword = new Label("Contraseña:");
        PasswordField txtPassword = new PasswordField();
        txtPassword.setText("1234");
        txtPassword.setPromptText("Contraseña");

        grid.add(lblHost, 0, 0);
        grid.add(txtHost, 1, 0);
        grid.add(lblPuerto, 0, 1);
        grid.add(txtPuerto, 1, 1);
        grid.add(lblUsuario, 0, 2);
        grid.add(txtUsuario, 1, 2);
        grid.add(lblPassword, 0, 3);
        grid.add(txtPassword, 1, 3);

        Button btnConectar = new Button("Conectar");
        btnConectar.getStyleClass().add("btn-primary");
        btnConectar.setDefaultButton(true);

        Label lblEstado = new Label("");
        lblEstado.getStyleClass().add("estado");

        btnConectar.setOnAction(e -> {
            String host = txtHost.getText().trim();
            int puerto;
            try {
                puerto = Integer.parseInt(txtPuerto.getText().trim());
            } catch (NumberFormatException ex) {
                lblEstado.setText("❌ Puerto inválido");
                lblEstado.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }
            String usuario = txtUsuario.getText().trim();
            String password = txtPassword.getText();

            if (host.isEmpty() || usuario.isEmpty() || password.isEmpty()) {
                lblEstado.setText("❌ Todos los campos son obligatorios");
                lblEstado.setStyle("-fx-text-fill: #e74c3c;");
                return;
            }

            lblEstado.setText("⏳ Conectando...");
            lblEstado.setStyle("-fx-text-fill: #3498db;");
            btnConectar.setDisable(true);

            // Conectar en segundo plano
            new Thread(() -> {
                ConexionSSL conexion = new ConexionSSL(host, puerto, usuario, password);
                boolean conectado = conexion.conectar();

                javafx.application.Platform.runLater(() -> {
                    if (conectado) {
                        abrirTerminal(stage, conexion);
                    } else {
                        lblEstado.setText("❌ Error de conexión o credenciales incorrectas");
                        lblEstado.setStyle("-fx-text-fill: #e74c3c;");
                        btnConectar.setDisable(false);
                    }
                });
            }).start();
        });

        panel.getChildren().addAll(titulo, grid, btnConectar, lblEstado);
        return panel;
    }

    private void abrirTerminal(Stage stage, ConexionSSL conexion) {
        TerminalView terminal = new TerminalView(conexion);
        Scene scene = new Scene(terminal, 900, 650);

        // Cargar CSS si existe
        try {
            var cssResource = getClass().getResource("/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
            }
        } catch (Exception ex) {
            System.out.println("⚠️ No se pudo cargar styles.css en terminal");
        }

        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

