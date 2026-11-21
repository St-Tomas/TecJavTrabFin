package scaffoldj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Servicios {
    private final Map<Class<?>, Object> instancias = new ConcurrentHashMap<>();

    public <T> void registrar(Class<T> tipo, T servicio){
        instancias.put(tipo, servicio);
    }
    @SuppressWarnings ("unchecked")
    public <T> T obtener(Class<T> tipo){
        Object servicio = instancias.get(tipo);
        if (servicio == null) {
            throw new IllegalArgumentException("Servicio no registrado: " + tipo.getSimpleName());
        }
        return (T) servicio;
    }
}
