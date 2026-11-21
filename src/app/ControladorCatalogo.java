package app;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import scaffoldj.Controlador;
import scaffoldj.Rol;
import scaffoldj.Servicios;
import scaffoldj.Solicitud;
import scaffoldj.Validacion.Validador;
import scaffoldj.Validacion.ValidadorNoVacio;

public class ControladorCatalogo implements Controlador {
    private final ServicioInventario servicioInventario;

    public ControladorCatalogo(Servicios servicios) {
        this.servicioInventario = servicios.obtener(ServicioInventario.class);
    }
    @Override
    public String recurso() {
        return "catalogo";
    }
    @Override
    public Map<String, Function<Solicitud, String>> acciones() {
        return Map.of(
            "listar", this::listar,
            "buscar", this::buscar,
            "stock", this::stock
        );
    }
    @Override
    public Map<String, Rol> permisos() {
        return Map.of(
            "listar", Rol.CLIENTE,
            "buscar", Rol.CLIENTE,
            "stock", Rol.CLIENTE
        );
    }
    @Override
    public Map<String, List<Validador>> validadores() {
        return Map.of(
            "buscar", List.of(new ValidadorNoVacio()),
            "stock", List.of(new ValidadorNoVacio())
        );
    }

    private String listar(Solicitud sol) {
        List<Producto> lista = servicioInventario.listar();
        if (lista.isEmpty()) return "Catalogo vacío.";
        return lista.stream().map(Producto::toString).collect(Collectors.joining(" ; "));
    }

    private String buscar(Solicitud sol) {
        List<Producto> encontrados = servicioInventario.buscar(sol.getParametro(0));
        if (encontrados.isEmpty()) return "Sin resultados para '" + sol.getParametro(0) + "'.";
        return encontrados.stream().map(Producto::toString).collect(Collectors.joining(" ; "));
    }

    private String stock(Solicitud sol) {
        Producto p = servicioInventario.buscarPorCodigo(sol.getParametro(0));
        String disp = p.getStock() > 0 ? "DISPONIBLE (" + p.getStock() + " u.)" : "SIN STOCK";
        return p.getCodigo() + " - " + p.getNombre() + " | " + disp + " | $" + p.getPrecio();
    }
}