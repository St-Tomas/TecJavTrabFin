package app;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import scaffoldj.Controlador;
import scaffoldj.Rol;
import scaffoldj.Servicios;
import scaffoldj.Solicitud;
import scaffoldj.Validacion.Validador;
import scaffoldj.Validacion.ValidadorDecimalPositivo;
import scaffoldj.Validacion.ValidadorEntero;
import scaffoldj.Validacion.ValidadorEnteroPositivo;
import scaffoldj.Validacion.ValidadorLargoMaximo;
import scaffoldj.Validacion.ValidadorNoVacio;

public class ControladorInventario implements Controlador {
    private final ServicioInventario servicioInventario;

    public ControladorInventario(Servicios servicios) {
        this.servicioInventario = servicios.obtener(ServicioInventario.class);
    }

    @Override
    public String recurso() {
        return "inventario";
    }

    @Override
    public Map<String, Function<Solicitud, String>> acciones() {
        return Map.of(
            "alta", this::alta,
            "precio", this::precio,
            "stock", this::stock
        );
    }

    @Override
    public Map<String, Rol> permisos() {
        return Map.of(
            "alta", Rol.ADMINISTRADOR,
            "precio", Rol.ADMINISTRADOR,
            "stock", Rol.ADMINISTRADOR
        );
    }

    @Override
    public Map<String, List<Validador>> validadores() {
        return Map.of(
            "alta", List.of(
                new ValidadorNoVacio(),
                new ValidadorLargoMaximo(40),
                new ValidadorNoVacio(),
                new ValidadorDecimalPositivo(),
                new ValidadorEnteroPositivo()
            ),
            "precio", List.of(new ValidadorNoVacio(), new ValidadorDecimalPositivo()),
            "stock", List.of(new ValidadorNoVacio(), new ValidadorEntero())
        );
    }

    private String alta(Solicitud sol) {
        String codigo = sol.getParametro(0);
        String nombre = sol.getParametro(1);
        String categoria = sol.getParametro(2);
        double precio = Double.parseDouble(sol.getParametro(3).replace(",", "."));
        int stock = Integer.parseInt(sol.getParametro(4));

        Producto creado = servicioInventario.alta(codigo, nombre, categoria, precio, stock);
        return "Producto creado: " + creado;
    }

    private String precio(Solicitud sol) {
        double nuevoPrecio = Double.parseDouble(sol.getParametro(1).replace(",", "."));
        Producto p = servicioInventario.actualizarPrecio(sol.getParametro(0), nuevoPrecio);
        return "Precio actualizado: " + p;
    }

    private String stock(Solicitud sol) {
        int delta = Integer.parseInt(sol.getParametro(1));
        Producto p = servicioInventario.actualizarStock(sol.getParametro(0), delta);
        return "Stock actualizado: " + p;
    }
}