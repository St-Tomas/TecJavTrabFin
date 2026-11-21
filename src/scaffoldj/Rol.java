package scaffoldj;

public enum Rol{
    INVITADO(0), CLIENTE(1), ADMINISTRADOR(2);

    private final int nivel;
    Rol(int nivel) {
        this.nivel = nivel;
    }
    public boolean tienePermiso(Rol requerido) {
        return this.nivel >= requerido.nivel;
    }
}

