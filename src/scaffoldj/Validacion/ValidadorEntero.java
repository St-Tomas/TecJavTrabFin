package scaffoldj.Validacion;

public class ValidadorEntero implements Validador {
    
    @Override
    public void validar(String valor, String nombreCampo) {
        try {
            Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            throw new ValidacionException("El campo '" + nombreCampo + "' debe ser un número entero válido.");
        }
    }
    
}
