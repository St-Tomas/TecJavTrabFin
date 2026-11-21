package scaffoldj.Validacion;

public class ValidadorNoVacio implements Validador {

    @Override
    public void validar(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
    }
}
