package com.refork.api.dto;

public class ComandoRequest {
    private String sessionId;
    private String comando;

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getComando() { return comando; }
    public void setComando(String comando) { this.comando = comando; }
}

