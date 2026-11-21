package scaffoldj;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import scaffoldj.Validacion.ValidacionException;
import scaffoldj.Validacion.Validador;
import java.util.Optional;
import java.util.HashMap;

public class Router {

    private final Map<String, Controlador> controladores = new HashMap<>();
    public void registrar(Controlador controlador) {
        controladores.put(controlador.recurso().toLowerCase(), controlador);
    }
    public String despachar(Solicitud solicitud){
        Controlador c = Optional.ofNullable(controladores.get(solicitud.getRecurso()))
        .orElseThrow(() -> new FrameworkException("Recurso no encontrado: " + solicitud.getRecurso()));

        Function<Solicitud, String> accion = Optional.ofNullable(c.acciones().get(solicitud.getAccion()))
        .orElseThrow(()-> new FrameworkException("Acción no encontrada: " + solicitud.getAccion() + "'"));
    
        Rol requerido = c.permisos().get(solicitud.getAccion());
        if (requerido != null){
            solicitud.getSesion().verificarPermiso(requerido);
        }

        List<Validador> reglas = c.validadores().getOrDefault(solicitud.getAccion(), List.of());
        if (solicitud.getParametros().size() != reglas.size()){
            throw new ValidacionException("Cantida de parametros incorrecta. Se esperaban " + reglas.size() + "y se recibieron " + solicitud.getParametros().size());
        }
        for (int i = 0; i < reglas.size(); i++) {
            reglas.get(i).validar(solicitud.getParametro(i), "param" + (i + 1));
        }
        return accion.apply(solicitud);
    }
}
