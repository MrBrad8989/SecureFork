package com.refork.server;

import com.refork.client.GestorCliente;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorSSL {
    private static final int PUERTO = 8282;
    private static final int MAX_CONEXIONES = 5;

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.keyStore", "servidor_keystore.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456");

        // REQUISITO PDF: Pool de Hilos
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CONEXIONES);

        try {
            SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(PUERTO);

            System.out.println("🚀 SERVIDOR INICIADO (Pool de " + MAX_CONEXIONES + " hilos)");

            while (true) {
                SSLSocket clienteSocket = (SSLSocket) serverSocket.accept();
                System.out.println("✅ Cliente conectado: " + clienteSocket.getInetAddress());

                // CAMBIO CLAVE: En vez de .start(), usamos el pool
                pool.execute(new GestorCliente(clienteSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Buena práctica para cierre controlado
            if (pool != null) pool.shutdown();
        }
    }
}