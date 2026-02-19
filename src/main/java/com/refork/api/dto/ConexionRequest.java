package com.refork.api.dto;

public class ConexionRequest {
    private String type;
    private String host;
    private int port;
    private String user;
    private String password;
    private String trustStore;
    private String trustStorePassword;

    // Getters y setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getTrustStore() { return trustStore; }
    public void setTrustStore(String trustStore) { this.trustStore = trustStore; }
    public String getTrustStorePassword() { return trustStorePassword; }
    public void setTrustStorePassword(String trustStorePassword) { this.trustStorePassword = trustStorePassword; }
}

