package modelo;

public class ItemMenu {
    public enum Tipo {
        BEBIDA, CONDIMENTO
    }

    private String nombre;
    private double precio;
    private boolean activo;
    private Tipo tipo;

    public ItemMenu(String n, double p, boolean a, Tipo t) {
        nombre = n;
        precio = p;
        activo = a;
        tipo = t;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isActivo() {
        return activo;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setPrecio(double p) {
        precio = p;
    }

    public void setActivo(boolean a) {
        activo = a;
    }

    public String toString() {
        return nombre + " ($" + String.format("%.2f", precio) + ")";
    }
}
