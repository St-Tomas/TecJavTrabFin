package scaffoldj.Validacion;

public class ValidadorEnteroPositivo implements Validador {
    @Override
    public void validar(String valor, String nombreCampo) {
        try {
            int n = Integer.parseInt(valor);
            if (n <= 0) {
                throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero mayor a 0.");
            }
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero válido.");
        }
    }
}
