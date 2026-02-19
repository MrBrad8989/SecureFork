package com.refork.api.service;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SftpService {

    @Value("${securefork.sftp.timeoutSeconds:30}")
    private long timeoutSeconds;

    public SftpClient conectar(String host, int port, String user, String password) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        client.start();

        ClientSession session = client.connect(user, host, port)
                .verify(timeoutSeconds, TimeUnit.SECONDS)
                .getSession();
        session.addPasswordIdentity(password);
        session.auth().verify(timeoutSeconds, TimeUnit.SECONDS);

        return SftpClientFactory.instance().createSftpClient(session);
    }

    public List<Map<String, Object>> listar(SftpClient sftp, String ruta) throws Exception {
        List<Map<String, Object>> items = new ArrayList<>();
        for (SftpClient.DirEntry entry : sftp.readDir(ruta)) {
            String nombre = entry.getFilename();
            if (".".equals(nombre) || "..".equals(nombre)) {
                continue;
            }
            Map<String, Object> item = new HashMap<>();
            item.put("name", nombre);
            item.put("isDirectory", entry.getAttributes().isDirectory());
            item.put("size", entry.getAttributes().getSize());
            items.add(item);
        }
        return items;
    }

    public void descargar(SftpClient sftp, String remoto, Path local) throws Exception {
        Files.createDirectories(local.getParent());
        try (InputStream in = sftp.read(remoto); OutputStream out = Files.newOutputStream(local)) {
            in.transferTo(out);
        }
    }

    public void subir(SftpClient sftp, Path local, String remoto) throws Exception {
        try (InputStream in = Files.newInputStream(local); OutputStream out = sftp.write(remoto)) {
            in.transferTo(out);
        }
    }

    public String leerArchivo(SftpClient sftp, String remoto) throws Exception {
        try (InputStream in = sftp.read(remoto)) {
            return new String(in.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
