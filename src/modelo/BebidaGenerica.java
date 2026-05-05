package modelo;

public class BebidaGenerica extends Bebida {
    private final String nombre;
    private final double precio;

    public BebidaGenerica(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String obtenerDescripcion() {
        return nombre;
    }

    @Override
    public double obtenerCosto() {
        return precio;
    }
}
