package modelo;

public class CondimentoGenerico extends DecoradorCondimento {
    private final String nombre;
    private final double precio;

    public CondimentoGenerico(Bebida bebida, String nombre, double precio) {
        super(bebida);
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String obtenerDescripcion() {
        return bebida.obtenerDescripcion() + " + " + nombre;
    }

    @Override
    public double obtenerCosto() {
        return bebida.obtenerCosto() + precio;
    }
}
