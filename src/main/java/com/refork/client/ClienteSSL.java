package com.refork.client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Scanner;

public class ClienteSSL {
    private static final String HOST = "localhost";
    private static final int PUERTO = 8282;

    public static void main(String[] args) {
        // Confianza en el certificado propio (el mismo que el servidor)
        System.setProperty("javax.net.ssl.trustStore", "servidor_keystore.p12");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        try {
            SSLSocketFactory clientFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) clientFactory.createSocket(HOST, PUERTO);

            // 1. AUTENTICACIÓN (Requisito: Envío de Objetos)
            // IMPORTANTE: Creamos el ObjectOutputStream PRIMERO
            ObjectOutputStream salidaObjetos = new ObjectOutputStream(socket.getOutputStream());

            System.out.println("🔐 Enviando credenciales de acceso...");
            // Enviamos el objeto PeticionLogin (Usuario "admin", pass "1234")
            salidaObjetos.writeObject(new PeticionLogin("admin", "1234"));

            // 2. PREPARAR CANALES DE TEXTO Y DATOS
            DataInputStream flujoEntrada = new DataInputStream(socket.getInputStream());
            DataOutputStream flujoSalida = new DataOutputStream(socket.getOutputStream());
            Scanner teclado = new Scanner(System.in);

            // Leemos el mensaje de bienvenida ("LOGIN OK...")
            // Si el login falla, el servidor enviará LOGIN_ERROR
            try {
                String bienvenida = flujoEntrada.readUTF();
                if (bienvenida.startsWith("LOGIN_ERROR::")) {
                    System.err.println("❌ " + bienvenida.substring("LOGIN_ERROR::".length()));
                    return;
                }
                System.out.println("SERVIDOR: " + bienvenida);
            } catch (EOFException e) {
                System.err.println("❌ Login rechazado por el servidor.");
                return;
            }

            // 3. BUCLE DE COMUNICACIÓN
            boolean salir = false;
            while (!salir) {
                System.out.print("\nIntroduce comando (ls, get [archivo], cd [dir], exit): ");
                String comando = teclado.nextLine();

                flujoSalida.writeUTF(comando);

                if (comando.equalsIgnoreCase("exit")) {
                    try {
                        System.out.println("SERVIDOR: " + flujoEntrada.readUTF());
                    } catch (EOFException e) {
                        // Servidor cerró la conexión
                    }
                    salir = true;
                } else {
                    // LEEMOS RESPUESTA (Puede ser texto O un archivo)
                    String respuesta = flujoEntrada.readUTF();

                    if ("CLEAR_SCREEN".equals(respuesta)) {
                        System.out.println("\n\n\n\n\n\n\n\n\n\n");
                        continue;
                    }

                    // DETECTAMOS SI ES UNA DESCARGA DE ARCHIVO
                    if (respuesta.startsWith("ARCHIVO_VA::")) {
                        recibirArchivo(respuesta, flujoEntrada);
                    } else {
                        // ES TEXTO NORMAL
                        System.out.println(respuesta);
                    }
                }
            }

            flujoSalida.close();
            flujoEntrada.close();
            socket.close();
            System.out.println("Conexión cerrada.");

        } catch (Exception e) {
            System.err.println("❌ Error en el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // MÉTODO AUXILIAR PARA GUARDAR EL ARCHIVO RECIBIDO
    private static void recibirArchivo(String cabecera, DataInputStream flujoEntrada) {
        try {
            // La cabecera es: ARCHIVO_VA::nombre::tamaño
            String[] partes = cabecera.split("::");
            String nombreOriginal = partes[1];
            long tamano = Long.parseLong(partes[2]);

            System.out.println("📥 Iniciando descarga de: " + nombreOriginal + " (" + tamano + " bytes)");

            // Guardamos con prefijo para distinguirlo
            File archivoDestino = new File("DESCARGADO_" + nombreOriginal);
            FileOutputStream fos = new FileOutputStream(archivoDestino);

            byte[] buffer = new byte[4096];
            long totalLeido = 0;
            int leido;

            // Leemos EXACTAMENTE 'tamano' bytes
            while (totalLeido < tamano) {
                // Calculamos cuánto falta para no leer de más (del siguiente mensaje)
                int aLeer = (int) Math.min(buffer.length, tamano - totalLeido);
                leido = flujoEntrada.read(buffer, 0, aLeer);

                if (leido == -1) break; // Fin inesperado del stream

                fos.write(buffer, 0, leido);
                totalLeido += leido;
            }

            fos.close();
            System.out.println("✅ Archivo guardado correctamente en: " + archivoDestino.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("❌ Error al guardar el archivo: " + e.getMessage());
        }
    }
}