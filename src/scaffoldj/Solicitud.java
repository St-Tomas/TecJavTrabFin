package scaffoldj;

import java.util.Collections;
import java.util.List;

public class Solicitud {
    private final String recurso;
    private final String accion;
    private final List<String> parametros;
    private final Sesion sesion;

    public Solicitud(String recurso, String accion, List<String> parametros, Sesion sesion) {
        this.recurso = recurso;
        this.accion = accion;
        this.parametros = Collections.unmodifiableList(parametros);
        this.sesion = sesion;
    }
    public String getRecurso() {
        return recurso;
    }
    public String getAccion() {
        return accion;
    }
    public List<String> getParametros() {
        return parametros;
    }
    public String getParametro(int index){
        return parametros.get(index);
    }
    public Sesion getSesion() {
        return sesion;
    }
}
