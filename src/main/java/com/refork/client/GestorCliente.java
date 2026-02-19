package com.refork.client;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

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

            // ---------------------------------------------------------
            // 2. PREPARAR CANALES DE TEXTO
            // ---------------------------------------------------------
            InputStream entrada = socketCliente.getInputStream();
            OutputStream salida = socketCliente.getOutputStream();
            DataInputStream flujoEntrada = new DataInputStream(entrada);
            DataOutputStream flujoSalida = new DataOutputStream(salida);

            // Validación simple (Hardcoded para la práctica)
            if (!"admin".equals(login.getUsuario()) || !"1234".equals(login.getPassword())) {
                System.out.println("❌ Intento de login fallido: " + login.getUsuario());
                flujoSalida.writeUTF("LOGIN_ERROR::Credenciales incorrectas");
                flujoSalida.flush();
                socketCliente.close();
                return;
            }

            System.out.println("✅ Usuario " + login.getUsuario() + " logueado correctamente.");

            flujoSalida.writeUTF("LOGIN_OK::Bienvenido a la Shell Segura de PSP.\nDirectorio: " + directorioActual.getAbsolutePath());

            // ---------------------------------------------------------
            // 3. FASE DE COMANDOS (Shell Remota)
            // ---------------------------------------------------------
            boolean conectado = true;
            while (conectado) {
                // Leemos el comando del cliente
                String comando = flujoEntrada.readUTF().trim();
                if (comando.isEmpty()) {
                    flujoSalida.writeUTF("Comando vacío. Escribe 'help' para ayuda.");
                    continue;
                }

                String[] partes = comando.split("\\s+", 2);
                String cmd = partes[0].toLowerCase();
                String args = partes.length > 1 ? partes[1].trim() : "";

                if (cmd.equals("exit")) {
                    flujoSalida.writeUTF("Cerrando sesión...");
                    conectado = false;
                }
                // COMANDO HELP
                else if (cmd.equals("help")) {
                    flujoSalida.writeUTF(obtenerAyuda());
                }
                // COMANDO CD (Navegación)
                else if (cmd.equals("cd")) {
                    if (args.isEmpty()) {
                        flujoSalida.writeUTF("Uso: cd [directorio]");
                    } else {
                        String respuestaCd = cambiarDirectorio(args);
                        flujoSalida.writeUTF(respuestaCd);
                    }
                }
                else if (cmd.equals("cd..")) {
                    String respuestaCd = cambiarDirectorio("..");
                    flujoSalida.writeUTF(respuestaCd);
                }
                // COMANDO PWD
                else if (cmd.equals("pwd")) {
                    flujoSalida.writeUTF("Directorio actual: " + directorioActual.getAbsolutePath());
                }
                // COMANDO LS/DIR
                else if (cmd.equals("ls") || cmd.equals("dir")) {
                    flujoSalida.writeUTF(listarDirectorio());
                }
                // COMANDO WHOAMI
                else if (cmd.equals("whoami")) {
                    flujoSalida.writeUTF("Usuario del sistema: " + System.getProperty("user.name"));
                }
                // COMANDO DATE
                else if (cmd.equals("date")) {
                    flujoSalida.writeUTF("Fecha y hora: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                }
                // COMANDO CLEAR/CLS
                else if (cmd.equals("clear") || cmd.equals("cls")) {
                    flujoSalida.writeUTF("CLEAR_SCREEN");
                }
                // COMANDO MKDIR
                else if (cmd.equals("mkdir")) {
                    flujoSalida.writeUTF(crearDirectorio(args));
                }
                // COMANDO RMDIR
                else if (cmd.equals("rmdir")) {
                    flujoSalida.writeUTF(eliminarDirectorio(args));
                }
                // COMANDO RM
                else if (cmd.equals("rm")) {
                    flujoSalida.writeUTF(eliminarArchivo(args));
                }
                // COMANDO TOUCH
                else if (cmd.equals("touch")) {
                    flujoSalida.writeUTF(crearArchivoVacio(args));
                }
                // COMANDO CAT
                else if (cmd.equals("cat")) {
                    flujoSalida.writeUTF(leerArchivoTexto(args));
                }
                // COMANDO ECHO
                else if (cmd.equals("echo")) {
                    flujoSalida.writeUTF(args);
                }
                // COMANDO CP
                else if (cmd.equals("cp")) {
                    flujoSalida.writeUTF(copiarArchivo(args));
                }
                // COMANDO MV
                else if (cmd.equals("mv")) {
                    flujoSalida.writeUTF(moverArchivo(args));
                }
                // COMANDO HEAD
                else if (cmd.equals("head")) {
                    flujoSalida.writeUTF(leerLineas(args, true));
                }
                // COMANDO TAIL
                else if (cmd.equals("tail")) {
                    flujoSalida.writeUTF(leerLineas(args, false));
                }
                // COMANDO GET (Descarga de Archivos - Requisito PDF)
                else if (cmd.equals("get")) {
                    if (args.isEmpty()) {
                        flujoSalida.writeUTF("Uso: get [archivo]");
                    } else {
                        enviarArchivo(args, flujoSalida);
                    }
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

    private String listarDirectorio() {
        File[] archivos = directorioActual.listFiles();
        if (archivos == null || archivos.length == 0) {
            return "Directorio vacío.";
        }
        StringBuilder sb = new StringBuilder();
        for (File archivo : archivos) {
            sb.append(archivo.isDirectory() ? "[D] " : "[F] ")
              .append(archivo.getName())
              .append("\n");
        }
        return sb.toString().trim();
    }

    private String crearDirectorio(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Uso: mkdir [directorio]";
        }
        File dir = resolverRuta(nombre);
        if (dir.exists()) {
            return "Error: El directorio ya existe.";
        }
        if (dir.mkdirs()) {
            return "Directorio creado: " + dir.getAbsolutePath();
        }
        return "Error: No se pudo crear el directorio.";
    }

    private String eliminarDirectorio(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Uso: rmdir [directorio]";
        }
        File dir = resolverRuta(nombre);
        if (!dir.exists() || !dir.isDirectory()) {
            return "Error: El directorio no existe.";
        }
        File[] contenido = dir.listFiles();
        if (contenido != null && contenido.length > 0) {
            return "Error: El directorio no está vacío.";
        }
        return dir.delete() ? "Directorio eliminado." : "Error: No se pudo eliminar.";
    }

    private String eliminarArchivo(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Uso: rm [archivo]";
        }
        File archivo = resolverRuta(nombre);
        if (!archivo.exists() || !archivo.isFile()) {
            return "Error: El archivo no existe.";
        }
        return archivo.delete() ? "Archivo eliminado." : "Error: No se pudo eliminar.";
    }

    private String crearArchivoVacio(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Uso: touch [archivo]";
        }
        File archivo = resolverRuta(nombre);
        try {
            if (archivo.exists()) {
                return "El archivo ya existe.";
            }
            if (archivo.createNewFile()) {
                return "Archivo creado: " + archivo.getAbsolutePath();
            }
            return "Error: No se pudo crear el archivo.";
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String leerArchivoTexto(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "Uso: cat [archivo]";
        }
        File archivo = resolverRuta(nombre);
        if (!archivo.exists() || !archivo.isFile()) {
            return "Error: El archivo no existe.";
        }
        long limite = 200_000;
        if (archivo.length() > limite) {
            return "Error: Archivo demasiado grande para mostrar (" + archivo.length() + " bytes).";
        }
        try {
            return Files.readString(archivo.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "Error leyendo archivo: " + e.getMessage();
        }
    }

    private String copiarArchivo(String args) {
        String[] partes = dividirDosRutas(args, "cp [origen] [destino]");
        if (partes == null) return "Uso: cp [origen] [destino]";
        File origen = resolverRuta(partes[0]);
        File destino = resolverRuta(partes[1]);
        if (!origen.exists() || !origen.isFile()) {
            return "Error: El archivo de origen no existe.";
        }
        try {
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "Archivo copiado.";
        } catch (IOException e) {
            return "Error al copiar: " + e.getMessage();
        }
    }

    private String moverArchivo(String args) {
        String[] partes = dividirDosRutas(args, "mv [origen] [destino]");
        if (partes == null) return "Uso: mv [origen] [destino]";
        File origen = resolverRuta(partes[0]);
        File destino = resolverRuta(partes[1]);
        if (!origen.exists()) {
            return "Error: El origen no existe.";
        }
        try {
            Files.move(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return "Archivo movido.";
        } catch (IOException e) {
            return "Error al mover: " + e.getMessage();
        }
    }

    private String leerLineas(String args, boolean head) {
        if (args == null || args.isBlank()) {
            return "Uso: " + (head ? "head" : "tail") + " [archivo] [n]";
        }
        String[] partes = args.split("\\s+", 2);
        String nombre = partes[0];
        int n = 10;
        if (partes.length > 1) {
            try {
                n = Integer.parseInt(partes[1].trim());
            } catch (NumberFormatException ignored) {
                n = 10;
            }
        }
        File archivo = resolverRuta(nombre);
        if (!archivo.exists() || !archivo.isFile()) {
            return "Error: El archivo no existe.";
        }
        try {
            List<String> lineas = Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8);
            if (lineas.isEmpty()) {
                return "(archivo vacío)";
            }
            int total = lineas.size();
            int desde = head ? 0 : Math.max(0, total - n);
            int hasta = head ? Math.min(n, total) : total;
            StringBuilder sb = new StringBuilder();
            for (int i = desde; i < hasta; i++) {
                sb.append(lineas.get(i)).append("\n");
            }
            return sb.toString().trim();
        } catch (IOException e) {
            return "Error leyendo archivo: " + e.getMessage();
        }
    }

    private File resolverRuta(String ruta) {
        File prueba = new File(ruta);
        if (prueba.isAbsolute()) {
            return prueba;
        }
        return new File(directorioActual, ruta);
    }

    private String[] dividirDosRutas(String args, String uso) {
        if (args == null || args.isBlank()) {
            return null;
        }
        String[] partes = args.split("\\s+", 2);
        if (partes.length < 2 || partes[0].isBlank() || partes[1].isBlank()) {
            return null;
        }
        return new String[]{partes[0], partes[1]};
    }

    private String obtenerAyuda() {
        return "Comandos disponibles:\n" +
               "- ls/dir: listar archivos\n" +
               "- cd [dir]: cambiar directorio\n" +
               "- cd..: subir un nivel\n" +
               "- pwd: directorio actual\n" +
               "- get [archivo]: descargar archivo\n" +
               "- whoami: usuario del sistema\n" +
               "- date: fecha y hora\n" +
               "- clear/cls: limpiar pantalla\n" +
               "- mkdir [dir]: crear directorio\n" +
               "- rmdir [dir]: eliminar directorio vacío\n" +
               "- rm [archivo]: eliminar archivo\n" +
               "- touch [archivo]: crear archivo vacío\n" +
               "- cat [archivo]: mostrar archivo\n" +
               "- echo [texto]: imprimir texto\n" +
               "- cp [origen] [destino]: copiar archivo\n" +
               "- mv [origen] [destino]: mover archivo\n" +
               "- head [archivo] [n]: primeras n líneas\n" +
               "- tail [archivo] [n]: últimas n líneas\n" +
               "- exit: salir";
    }
}