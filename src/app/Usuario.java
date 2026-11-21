package app;

import scaffoldj.Rol;

public class Usuario {

    private final String user;
    private final String contraseña;
    private final Rol rol;

    public Usuario(String user, String contraseña, Rol rol){
        this.user = user;
        this.contraseña = contraseña;
        this.rol = rol;
    }
    public String getUser(){return user;}
    public String getContraseña(){return contraseña;}
    public Rol getRol(){return rol;}
}
