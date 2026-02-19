package com.refork.api.service;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.client.keyverifier.AcceptAllServerKeyVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@Service
public class SshService {

    @Value("${securefork.ssh.timeoutSeconds:30}")
    private long timeoutSeconds;

    public ClientSession conectar(String host, int port, String user, String password) throws Exception {
        SshClient client = SshClient.setUpDefaultClient();
        client.setServerKeyVerifier(AcceptAllServerKeyVerifier.INSTANCE);
        client.start();

        ClientSession session = client.connect(user, host, port)
                .verify(timeoutSeconds, TimeUnit.SECONDS)
                .getSession();
        session.addPasswordIdentity(password);
        session.auth().verify(timeoutSeconds, TimeUnit.SECONDS);

        return session;
    }

    public String ejecutarComando(ClientSession session, String comando) throws Exception {
        try (ClientChannel channel = session.createExecChannel(comando);
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayOutputStream err = new ByteArrayOutputStream()) {

            channel.setOut(out);
            channel.setErr(err);
            channel.open().verify(10, TimeUnit.SECONDS);
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), TimeUnit.SECONDS.toMillis(30));

            String output = out.toString();
            String error = err.toString();

            if (!error.isEmpty()) {
                return error;
            }
            return output.isEmpty() ? "(comando ejecutado sin salida)" : output;
        }
    }
}
