package scaffoldj;

import scaffoldj.Validacion.Validador;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Controlador {
    
    String recurso();
    
    Map<String, Function<Solicitud, String>> acciones();

    default Map<String, Rol> permisos() {
        return Map.of();
    }
    default Map<String, List<Validador>> validadores() {
        return Map.of();
    }
    
}
