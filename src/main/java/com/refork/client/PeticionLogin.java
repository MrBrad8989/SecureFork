package com.refork.client;

import java.io.Serializable;

public class PeticionLogin implements Serializable {
    private static final long serialVersionUID = 1L;

    private String usuario;
    private String password;

    public PeticionLogin(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() { return usuario; }
    public String getPassword() { return password; }
}