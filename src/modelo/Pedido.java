package modelo;

public class Pedido {
    private final int cantidad;
    private final String descripcion;
    private final double precioTotal;

    public Pedido(int cantidad, String descripcion, double precioTotal) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.precioTotal = precioTotal;
    }

    public String getLineaTicket() {
        return cantidad + "× " + descripcion + " - $" + String.format("%.2f", precioTotal);
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
