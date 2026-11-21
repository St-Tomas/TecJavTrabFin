package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import scaffoldj.Validacion.ValidacionException;

public class ServicioInventario{
    private final Map<String, Producto> inventario = new ConcurrentHashMap<>();

    public ServicioInventario() {
            agregarInicial(new Producto("MART-01", "Martillo ", "Herramientas", 14500.00, 12));
            agregarInicial(new Producto("DEST-02", "Destornillador Phillips", "Herramientas", 5200.00, 25));
            agregarInicial(new Producto("TALA-03", "Taladro Percutor 650W", "Electricas", 89900.00, 4));
            agregarInicial(new Producto("CINT-04", "Cinta Metrica 5m", "Medicion", 6800.00, 18));
            agregarInicial(new Producto("TORN-05", "Tornillos Autoperforantes x100", "Fijaciones", 4300.00, 40));
        }
    public void agregarInicial(Producto p){
        inventario.put(p.getCodigo(), p);
    }
    public synchronized Producto alta (String codigo, String nombre, String categoria, double precio, int stock){
        String key = codigo.toUpperCase();
        if (inventario.containsKey(key)){
            throw new ValidacionException("Ya existe un producto con el codigo " + key);
        }
        Producto nuevo = new Producto(key, nombre, categoria, precio, stock);
        inventario.put(key, nuevo);
        return nuevo;
    }
    public List <Producto> listar(){
        return new ArrayList<>(inventario.values());
    }
    public List<Producto> buscar(String filtro){
        String texto = filtro.toLowerCase();
        List<Producto> res = new ArrayList<>();
        for (Producto p : inventario.values()){
            if (p.getNombre().toLowerCase().contains(texto) || p.getCategoria().toLowerCase().contains(texto) || p.getCodigo().toLowerCase().contains(texto)){
                res.add(p);
            }
        }return res;
    }
    public Producto buscarPorCodigo(String codigo){
        Producto p = inventario.get(codigo.toUpperCase());
        if (p == null) {
            throw new ValidacionException("No se encontro el producto "+ codigo.toUpperCase());
        } return p;
    }
    public synchronized Producto actualizarPrecio(String codigo, double nuevoPrecio){
        Producto p = buscarPorCodigo(codigo);
        p.setPrecio(nuevoPrecio);
        return p;
    }
    public synchronized Producto actualizarStock(String codigo, int delta){
        Producto p = buscarPorCodigo(codigo);
        int resultante = p.getStock() + delta;
        if (resultante < 0){
            throw new ValidacionException("Stock insuficiente. La cantidad actual es: " + p.getStock());
        }p.setStock(resultante);
        return p;
    }
}
