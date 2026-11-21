package scaffoldj;

import java.util.HashMap;
import java.util.Map;

public class Sesion {
    private String usuario;
    private Rol rol;
    private final Map<String, Object> atributos = new HashMap<>();

    public Sesion(){
        cerrar();
    }
    public void iniciar(String usuario, Rol rol) {
        this.usuario = usuario;
        this.rol = rol;
    }
    public void cerrar() {
        this.usuario = "anonimo";
        this.rol = Rol.INVITADO;
        this.atributos.clear();
    }
    public void verificarPermiso(Rol requerido) {
        if (!rol.tienePermiso(requerido)) {
            throw new AccesoDenegadoException("Acceso denegado. Necesitas el rol " + requerido + " (rol actual: " + this.rol + ")");
        }
    }
    public String getUsuario() {
        return usuario;
    }
    public Rol getRol() {
        return rol;
    }
    public void setAtributo(String clave, Object valor) {
        atributos.put(clave, valor);
    }
    public Object getAtributo(String clave) {
        return atributos.get(clave);
    }
}
