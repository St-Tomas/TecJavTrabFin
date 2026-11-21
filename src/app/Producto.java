package app;

import java.util.Locale;

public class Producto {
    private final String nombre;
    private final String codigo;
    private final String categoria;
    private double precio;
    private int stock;

    public Producto(String codigo, String nombre, String categoria, double precio, int stock){
        this.codigo = codigo.toUpperCase();
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
    }
    public String getCodigo() {return codigo;}
    public String getNombre() {return nombre;}
    public String getCategoria() {return categoria;}
    public double getPrecio() {return precio;}
    public void setPrecio(double precio){this.precio = precio;}
    public int getStock() {return stock;}
    public void setStock(int stock){this.stock= stock;}

    @Override
    public String toString(){
        return String.format(Locale.US, "[%s] %s (%s) - $%.2f - Stock: %d", codigo, nombre, categoria, precio, stock);
    }
}
