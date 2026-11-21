package scaffoldj.Validacion;

public class ValidadorDecimalPositivo implements Validador {
    @Override
    public void validar(String valor, String nombreCampo) {
        try {
            double n = Double.parseDouble(valor.replace(",", "."));
            if (n <= 0) {
                throw new ValidacionException("El parámetro '" + nombreCampo + "' debe ser un número mayor a 0.");
            }
        } catch (NumberFormatException e) {
            throw new ValidacionException("El parámetro '" + nombreCampo + "' debe ser un decimal válido.");
        }
    }
}