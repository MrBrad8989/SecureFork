package com.refork.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

public class TerminalView extends BorderPane {

    private ConexionSSL conexion;
    private TextArea areaTerminal;
    private TextField campoComando;
    private Label lblEstado;
    private StringBuilder historial;

    public TerminalView(ConexionSSL conexion) {
        this.conexion = conexion;
        this.historial = new StringBuilder();

        inicializarUI();
    }

    private void inicializarUI() {
        // Barra superior
        HBox barraTop = new HBox(10);
        barraTop.setPadding(new Insets(10));
        barraTop.getStyleClass().add("barra-top");

        Label titulo = new Label("🔐 SecureFork Terminal");
        titulo.getStyleClass().add("titulo-terminal");

        Region espaciador = new Region();
        HBox.setHgrow(espaciador, Priority.ALWAYS);

        lblEstado = new Label("✅ Conectado");
        lblEstado.getStyleClass().add("estado-conectado");

        Button btnDesconectar = new Button("Desconectar");
        btnDesconectar.getStyleClass().add("btn-danger");
        btnDesconectar.setOnAction(e -> desconectar());

        barraTop.getChildren().addAll(titulo, espaciador, lblEstado, btnDesconectar);
        setTop(barraTop);

        // Área de terminal
        areaTerminal = new TextArea();
        areaTerminal.setEditable(false);
        areaTerminal.setWrapText(true);
        areaTerminal.getStyleClass().add("terminal-area");
        areaTerminal.setText("SecureFork SSH Terminal v1.0\n");
        areaTerminal.appendText("Conexión establecida. Escribe 'help' para ver comandos disponibles.\n");
        areaTerminal.appendText("=====================================\n\n");

        ScrollPane scrollTerminal = new ScrollPane(areaTerminal);
        scrollTerminal.setFitToWidth(true);
        scrollTerminal.setFitToHeight(true);
        setCenter(scrollTerminal);

        // Panel de comandos rápidos
        VBox panelComandos = new VBox(10);
        panelComandos.setPadding(new Insets(10));
        panelComandos.getStyleClass().add("panel-comandos");
        panelComandos.setPrefWidth(200);

        Label lblComandos = new Label("Comandos Rápidos:");
        lblComandos.getStyleClass().add("label-comandos");

        Button btnLs = crearBotonComando("📁 Listar (ls)", "ls");
        Button btnPwd = crearBotonComando("📍 Directorio actual", "pwd");
        Button btnWhoami = crearBotonComando("👤 Usuario", "whoami");
        Button btnDate = crearBotonComando("📅 Fecha", "date");
        Button btnHelp = crearBotonComando("❓ Ayuda", "help");

        Separator separador = new Separator();

        Label lblInfo = new Label("Comandos disponibles:\n• ls - Listar archivos\n• cd [dir] - Cambiar directorio\n• pwd - Directorio actual\n• get [archivo] - Descargar\n• exit - Salir");
        lblInfo.setWrapText(true);
        lblInfo.getStyleClass().add("label-info");

        panelComandos.getChildren().addAll(lblComandos, btnLs, btnPwd, btnWhoami, btnDate, btnHelp, separador, lblInfo);
        setRight(panelComandos);

        // Barra de entrada inferior
        HBox barraEntrada = new HBox(10);
        barraEntrada.setPadding(new Insets(10));
        barraEntrada.getStyleClass().add("barra-entrada");

        Label prompt = new Label("$");
        prompt.getStyleClass().add("prompt");

        campoComando = new TextField();
        campoComando.setPromptText("Escribe un comando y presiona Enter...");
        campoComando.getStyleClass().add("campo-comando");
        HBox.setHgrow(campoComando, Priority.ALWAYS);

        Button btnEnviar = new Button("Enviar");
        btnEnviar.getStyleClass().add("btn-enviar");
        btnEnviar.setDefaultButton(true);

        barraEntrada.getChildren().addAll(prompt, campoComando, btnEnviar);
        setBottom(barraEntrada);

        // Eventos
        btnEnviar.setOnAction(e -> enviarComando());
        campoComando.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                enviarComando();
            }
        });

        campoComando.requestFocus();
    }

    private Button crearBotonComando(String texto, String comando) {
        Button btn = new Button(texto);
        btn.getStyleClass().add("btn-comando");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            campoComando.setText(comando);
            enviarComando();
        });
        return btn;
    }

    private void enviarComando() {
        String comando = campoComando.getText().trim();

        if (comando.isEmpty()) {
            return;
        }

        // Mostrar comando en terminal
        areaTerminal.appendText("$ " + comando + "\n");
        campoComando.clear();
        campoComando.setDisable(true);

        // Enviar en segundo plano
        new Thread(() -> {
            try {
                String respuesta = conexion.enviarComando(comando);

                Platform.runLater(() -> {
                    areaTerminal.appendText(respuesta + "\n\n");
                    areaTerminal.setScrollTop(Double.MAX_VALUE);

                    if (comando.equalsIgnoreCase("exit")) {
                        lblEstado.setText("❌ Desconectado");
                        lblEstado.getStyleClass().remove("estado-conectado");
                        lblEstado.getStyleClass().add("estado-desconectado");
                        campoComando.setDisable(true);
                    } else {
                        campoComando.setDisable(false);
                        campoComando.requestFocus();
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    areaTerminal.appendText("❌ Error: " + e.getMessage() + "\n\n");
                    lblEstado.setText("❌ Error de conexión");
                    lblEstado.getStyleClass().remove("estado-conectado");
                    lblEstado.getStyleClass().add("estado-desconectado");
                    campoComando.setDisable(true);
                });
            }
        }).start();
    }

    private void desconectar() {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar desconexión");
        confirmacion.setHeaderText("¿Deseas cerrar la conexión?");
        confirmacion.setContentText("Se perderá la sesión actual.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    conexion.enviarComando("exit");
                } catch (Exception e) {
                    // Ignorar
                }
                conexion.desconectar();
                lblEstado.setText("❌ Desconectado");
                lblEstado.getStyleClass().remove("estado-conectado");
                lblEstado.getStyleClass().add("estado-desconectado");
                campoComando.setDisable(true);
                areaTerminal.appendText("\n[Sesión cerrada]\n");
            }
        });
    }
}

