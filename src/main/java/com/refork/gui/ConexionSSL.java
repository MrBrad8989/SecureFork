package com.refork.gui;

import com.refork.client.PeticionLogin;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class ConexionSSL {
    private String host;
    private int puerto;
    private String usuario;
    private String password;

    private SSLSocket socket;
    private DataInputStream flujoEntrada;
    private DataOutputStream flujoSalida;
    private boolean conectado = false;

    public ConexionSSL(String host, int puerto, String usuario, String password) {
        this.host = host;
        this.puerto = puerto;
        this.usuario = usuario;
        this.password = password;
    }

    public boolean conectar() {
        try {
            // Configurar truststore
            System.setProperty("javax.net.ssl.trustStore", "servidor_keystore.p12");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");

            SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) clientFactory.createSocket(host, puerto);

            // Enviar credenciales
            ObjectOutputStream salidaObjetos = new ObjectOutputStream(socket.getOutputStream());
            salidaObjetos.writeObject(new PeticionLogin(usuario, password));

            // Preparar flujos
            flujoEntrada = new DataInputStream(socket.getInputStream());
            flujoSalida = new DataOutputStream(socket.getOutputStream());

            // Leer mensaje de bienvenida
            try {
                String bienvenida = flujoEntrada.readUTF();
                System.out.println("SERVIDOR: " + bienvenida);
                conectado = true;
                return true;
            } catch (EOFException e) {
                System.err.println("Login rechazado");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error de conexión: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public String enviarComando(String comando) throws IOException {
        if (!conectado) {
            throw new IOException("No conectado al servidor");
        }

        flujoSalida.writeUTF(comando);

        if (comando.equalsIgnoreCase("exit")) {
            conectado = false;
            return "Sesión cerrada";
        }

        String respuesta = flujoEntrada.readUTF();

        // Detectar transferencia de archivo
        if (respuesta.startsWith("ARCHIVO_VA::")) {
            return descargarArchivo(respuesta);
        }

        return respuesta;
    }

    private String descargarArchivo(String cabecera) {
        try {
            String[] partes = cabecera.split("::");
            String nombreOriginal = partes[1];
            long tamano = Long.parseLong(partes[2]);

            File archivoDestino = new File("DESCARGADO_" + nombreOriginal);
            FileOutputStream fos = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[4096];
            long totalLeido = 0;
            int leido;

            while (totalLeido < tamano) {
                int aLeer = (int) Math.min(buffer.length, tamano - totalLeido);
                leido = flujoEntrada.read(buffer, 0, aLeer);

                if (leido == -1) break;

                fos.write(buffer, 0, leido);
                totalLeido += leido;
            }

            fos.close();
            return "✅ Archivo descargado: " + archivoDestino.getAbsolutePath() + " (" + tamano + " bytes)";

        } catch (IOException e) {
            return "❌ Error al descargar archivo: " + e.getMessage();
        }
    }

    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            conectado = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConectado() {
        return conectado;
    }
}

