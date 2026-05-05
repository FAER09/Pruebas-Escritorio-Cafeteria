package modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Ticket {
    private String cliente;
    private LocalDateTime fechaHora;
    private List<Pedido> pedidos;
    private double total;

    public Ticket(String cliente, List<Pedido> pedidos, double total) {
        this.cliente = cliente;
        this.pedidos = pedidos;
        this.total = total;
        this.fechaHora = LocalDateTime.now();
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public double getTotal() {
        return total;
    }

    public String formato() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("Empleado: ").append(cliente).append("\n");
        sb.append("Fecha   : ").append(fechaHora.format(fmt)).append("\n");

        for (Pedido p : pedidos) {
            sb.append(p.getLineaTicket()).append("\n");
        }

        sb.append("Total: $").append(String.format("%.2f", total)).append("\n");
        sb.append("---\n");
        return sb.toString();
    }
}
