package scaffoldj;

import java.util.ArrayList;
import java.util.List;

public class ParsearSoli {

    public Solicitud parsear(String linea, Sesion sesion) throws ProtocoloException {
        if (linea == null || linea.trim().isEmpty()) {
            throw new ProtocoloException("Solicitud vacía");
        }

        String[] partes = linea.trim().split("/", -1);
        if (partes.length < 2) {
            throw new ProtocoloException("Formato inválido");
        }

        for (String p : partes) {
            if (p.trim().isEmpty()) {
                throw new ProtocoloException("No se permiten segmentos vacíos entre '/'.");
            }
        }

        String recurso = partes[0].trim().toLowerCase();
        String accion = partes[1].trim().toLowerCase();

        List<String> params = new ArrayList<>();
        for (int i = 2; i < partes.length; i++) {
            params.add(partes[i].trim());
        }

        return new Solicitud(recurso, accion, params, sesion);
    }
}