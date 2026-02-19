package com.refork.api.controller;

import com.refork.api.dto.*;
import com.refork.api.service.*;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ssh")
public class SshController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SshService sshService;

    @PostMapping("/connect")
    public ResponseEntity<ConexionResponse> conectar(@RequestBody ConexionRequest request) {
        try {
            ClientSession session = sshService.conectar(
                request.getHost(),
                request.getPort(),
                request.getUser(),
                request.getPassword()
            );

            String sessionId = sessionManager.createSession(
                "SSH",
                request.getHost(),
                request.getPort(),
                request.getUser()
            );

            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            info.setConnection(session);

            return ResponseEntity.ok(new ConexionResponse(true, sessionId, "Conectado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ConexionResponse(false, null, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/command")
    public ResponseEntity<ComandoResponse> ejecutarComando(@RequestBody ComandoRequest request) {
        try {
            SessionManager.SessionInfo info = sessionManager.getSession(request.getSessionId());
            if (info == null) {
                return ResponseEntity.ok(new ComandoResponse(false, null, "Sesión no encontrada"));
            }

            ClientSession session = (ClientSession) info.getConnection();
            String output = sshService.ejecutarComando(session, request.getComando());

            return ResponseEntity.ok(new ComandoResponse(true, output, null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ComandoResponse(false, null, "Error: " + e.getMessage()));
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Void> desconectar(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        sessionManager.removeSession(sessionId);
        return ResponseEntity.ok().build();
    }
}
