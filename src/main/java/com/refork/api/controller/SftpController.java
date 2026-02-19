package com.refork.api.controller;

import com.refork.api.dto.*;
import com.refork.api.service.*;
import org.apache.sshd.sftp.client.SftpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sftp")
public class SftpController {

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SftpService sftpService;

    @PostMapping("/connect")
    public ResponseEntity<ConexionResponse> conectar(@RequestBody ConexionRequest request) {
        try {
            SftpClient sftp = sftpService.conectar(
                request.getHost(),
                request.getPort(),
                request.getUser(),
                request.getPassword()
            );

            String sessionId = sessionManager.createSession(
                "SFTP",
                request.getHost(),
                request.getPort(),
                request.getUser()
            );

            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            info.setConnection(sftp);

            return ResponseEntity.ok(new ConexionResponse(true, sessionId, "Conectado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.ok(new ConexionResponse(false, null, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<?> listar(@RequestParam String sessionId, @RequestParam String path) {
        try {
            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            if (info == null) {
                return ResponseEntity.badRequest().body("Sesión no encontrada");
            }

            SftpClient sftp = (SftpClient) info.getConnection();
            List<Map<String, Object>> items = sftpService.listar(sftp, path);

            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Void> desconectar(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        sessionManager.removeSession(sessionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/download")
    public ResponseEntity<?> descargarArchivo(@RequestParam String sessionId,
                                               @RequestParam String remotePath,
                                               @RequestParam String localPath) {
        try {
            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            if (info == null) {
                return ResponseEntity.badRequest().body("Sesión no encontrada");
            }

            SftpClient sftp = (SftpClient) info.getConnection();
            sftpService.descargar(sftp, remotePath, java.nio.file.Paths.get(localPath));

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Archivo descargado",
                "localPath", localPath
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<?> subirArchivo(@RequestParam String sessionId,
                                           @RequestParam String localPath,
                                           @RequestParam String remotePath) {
        try {
            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            if (info == null) {
                return ResponseEntity.badRequest().body("Sesión no encontrada");
            }

            SftpClient sftp = (SftpClient) info.getConnection();
            sftpService.subir(sftp, java.nio.file.Paths.get(localPath), remotePath);

            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Archivo subido",
                "remotePath", remotePath
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/read")
    public ResponseEntity<?> leerArchivo(@RequestParam String sessionId,
                                          @RequestParam String remotePath) {
        try {
            SessionManager.SessionInfo info = sessionManager.getSession(sessionId);
            if (info == null) {
                return ResponseEntity.badRequest().body("Sesión no encontrada");
            }

            SftpClient sftp = (SftpClient) info.getConnection();
            String content = sftpService.leerArchivo(sftp, remotePath);

            return ResponseEntity.ok().body(Map.of("success", true, "content", content));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
