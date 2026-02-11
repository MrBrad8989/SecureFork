package com.refork.client;

import javax.net.ssl.SSLSocket;
import java.io.*;

public class GestorCliente extends Thread { // O implements Runnable si usas ExecutorService puro

    private SSLSocket socketCliente;
    private File directorioActual;

    public GestorCliente(SSLSocket socketCliente) {
        this.socketCliente = socketCliente;
        // Iniciamos en el directorio del proyecto
        this.directorioActual = new File(System.getProperty("user.dir"));
    }

    @Override
    public void run() {
        try {
            // ---------------------------------------------------------
            // 1. FASE DE AUTENTICACIÓN (Requisito: Envío de Objetos)
            // ---------------------------------------------------------
            // IMPORTANTE: ObjectInputStream debe crearse antes que los DataStreams
            ObjectInputStream entradaObjetos = new ObjectInputStream(socketCliente.getInputStream());

            // Leemos el objeto PeticionLogin enviado por el cliente
            PeticionLogin login = (PeticionLogin) entradaObjetos.readObject();

            // Validación simple (Hardcoded para la práctica)
            if (!"admin".equals(login.getUsuario()) || !"1234".equals(login.getPassword())) {
                System.out.println("❌ Intento de login fallido: " + login.getUsuario());
                // Podríamos enviar un mensaje de error antes de cerrar, pero por seguridad cerramos.
                socketCliente.close();
                return;
            }

            System.out.println("✅ Usuario " + login.getUsuario() + " logueado correctamente.");

            // ---------------------------------------------------------
            // 2. FASE DE COMANDOS (Shell Remota)
            // ---------------------------------------------------------
            // Ahora levantamos los flujos de datos primitivos para el chat/comandos
            InputStream entrada = socketCliente.getInputStream();
            OutputStream salida = socketCliente.getOutputStream();
            DataInputStream flujoEntrada = new DataInputStream(entrada);
            DataOutputStream flujoSalida = new DataOutputStream(salida);

            flujoSalida.writeUTF("LOGIN CORRECTO. Bienvenido a la Shell Segura de PSP.\nDirectorio: " + directorioActual.getAbsolutePath());

            boolean conectado = true;
            while (conectado) {
                // Leemos el comando del cliente
                String comando = flujoEntrada.readUTF().trim();

                if (comando.equalsIgnoreCase("exit")) {
                    flujoSalida.writeUTF("Cerrando sesión...");
                    conectado = false;
                }
                // COMANDO CD (Navegación)
                else if (comando.startsWith("cd ")) {
                    String respuestaCd = cambiarDirectorio(comando.substring(3));
                    flujoSalida.writeUTF(respuestaCd);
                }
                else if (comando.equals("cd..")) {
                    String respuestaCd = cambiarDirectorio("..");
                    flujoSalida.writeUTF(respuestaCd);
                }
                // COMANDO GET (Descarga de Archivos - Requisito PDF)
                else if (comando.startsWith("get ")) {
                    String nombreArchivo = comando.substring(4);
                    enviarArchivo(nombreArchivo, flujoSalida);
                }
                // COMANDOS DEL SISTEMA (ProcessBuilder)
                else {
                    String resultado = ejecutarComando(comando);
                    flujoSalida.writeUTF(resultado);
                }
            }

            // Cierre limpio de recursos
            socketCliente.close();
            System.out.println("Cliente desconectado.");

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error en gestor de cliente: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // MÉTODO PARA TRANSFERENCIA DE FICHEROS (Binario)
    // ---------------------------------------------------------
    private void enviarArchivo(String nombre, DataOutputStream salidaSocket) {
        try {
            File archivo = new File(directorioActual, nombre);

            if (archivo.exists() && archivo.isFile()) {
                // Protocolo: AVISO::NOMBRE::TAMAÑO
                long tamano = archivo.length();
                salidaSocket.writeUTF("ARCHIVO_VA::" + nombre + "::" + tamano);

                FileInputStream fileIn = new FileInputStream(archivo);
                byte[] buffer = new byte[4096]; // Buffer de 4KB
                int bytesLeidos;

                while ((bytesLeidos = fileIn.read(buffer)) != -1) {
                    salidaSocket.write(buffer, 0, bytesLeidos);
                }

                salidaSocket.flush(); // Forzar envío
                fileIn.close();
                System.out.println("Archivo enviado: " + nombre);

            } else {
                salidaSocket.writeUTF("Error: El archivo no existe o es un directorio.");
            }
        } catch (IOException e) {
            System.err.println("Error enviando archivo: " + e.getMessage());
        }
    }

    // ---------------------------------------------------------
    // MÉTODO PARA CAMBIAR DIRECTORIO (Estado)
    // ---------------------------------------------------------
    private String cambiarDirectorio(String rutaDestino) {
        File nuevoDir;

        if (rutaDestino.equals("..")) {
            nuevoDir = directorioActual.getParentFile();
            if (nuevoDir == null) return "Error: Ya estás en la raíz.";
        } else {
            File prueba = new File(rutaDestino);
            if (prueba.isAbsolute()) {
                nuevoDir = prueba;
            } else {
                nuevoDir = new File(directorioActual, rutaDestino);
            }
        }

        if (nuevoDir.exists() && nuevoDir.isDirectory()) {
            try {
                directorioActual = nuevoDir.getCanonicalFile();
                return "Directorio cambiado a: \n" + directorioActual.getAbsolutePath();
            } catch (IOException e) {
                return "Error ruta: " + e.getMessage();
            }
        } else {
            return "Error: Ruta no válida.";
        }
    }

    // ---------------------------------------------------------
    // MÉTODO PARA EJECUTAR COMANDOS (ProcessBuilder)
    // ---------------------------------------------------------
    private String ejecutarComando(String comando) {
        StringBuilder salidaComando = new StringBuilder();
        ProcessBuilder pb;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");

        if (isWindows) {
            pb = new ProcessBuilder("cmd.exe", "/c", comando);
        } else {
            pb = new ProcessBuilder("bash", "-c", comando);
        }

        pb.directory(directorioActual); // Mantiene la persistencia del directorio

        try {
            pb.redirectErrorStream(true);
            Process proceso = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
            String linea;
            while ((linea = reader.readLine()) != null) {
                salidaComando.append(linea).append("\n");
            }
            proceso.waitFor();

        } catch (Exception e) {
            return "Error ejecución: " + e.getMessage();
        }

        if (salidaComando.length() == 0) return "(Comando ejecutado sin salida visual)";
        return salidaComando.toString();
    }
}