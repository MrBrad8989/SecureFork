package com.refork.api.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    public String createSession(String type, String host, int port, String user) {
        String sessionId = UUID.randomUUID().toString();
        SessionInfo info = new SessionInfo(sessionId, type, host, port, user);
        sessions.put(sessionId, info);
        return sessionId;
    }

    public SessionInfo getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        SessionInfo info = sessions.remove(sessionId);
        if (info != null && info.getConnection() != null) {
            try {
                if (info.getConnection() instanceof AutoCloseable) {
                    ((AutoCloseable) info.getConnection()).close();
                }
            } catch (Exception e) {
                // Ignorar
            }
        }
    }

    public static class SessionInfo {
        private final String id;
        private final String type;
        private final String host;
        private final int port;
        private final String user;
        private Object connection;

        public SessionInfo(String id, String type, String host, int port, String user) {
            this.id = id;
            this.type = type;
            this.host = host;
            this.port = port;
            this.user = user;
        }

        public String getId() { return id; }
        public String getType() { return type; }
        public String getHost() { return host; }
        public int getPort() { return port; }
        public String getUser() { return user; }
        public Object getConnection() { return connection; }
        public void setConnection(Object connection) { this.connection = connection; }
    }
}
