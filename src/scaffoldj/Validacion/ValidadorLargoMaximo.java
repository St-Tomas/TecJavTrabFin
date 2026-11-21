package scaffoldj.Validacion;

public class ValidadorLargoMaximo implements Validador {
    private final int max;

    public ValidadorLargoMaximo(int max) {
        this.max = max;
    }
    @Override
    public void validar(String valor, String nombreCampo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ValidacionException("El campo '" + nombreCampo + "' no puede estar vacío.");
        }
        if (valor.length() > max) {
            throw new ValidacionException("El campo '" + nombreCampo + "' no puede tener más de " + max + " caracteres.");
        }
    }
}
