package vista;

import controlador.ControladorEmpleado;
import modelo.Pedido;
import modelo.Ticket;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VentanaEmpleado extends JFrame {

    private final JFrame padre;
    private final ControladorEmpleado ctrl = new ControladorEmpleado();
    private final JTextArea pedidosArea = new JTextArea();
    private PanelConstruirBebida panel;
    private final List<Pedido> pedidosParciales = new ArrayList<>();

    public VentanaEmpleado(JFrame padre, String empleado) {
        super("Empleado - Cafetería Decorator");
        this.padre = padre;

        setSize(700, 500);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        panel = new PanelConstruirBebida(ctrl);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 150), 1, true),
                "Construir Pedido",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(100, 70, 50)
        ));
        add(panel, BorderLayout.CENTER);

        pedidosArea.setEditable(false);
        pedidosArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        pedidosArea.setBackground(new Color(250, 250, 250));
        pedidosArea.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane sc = new JScrollPane(pedidosArea);
        sc.setPreferredSize(new Dimension(230, 0));
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 150), 1, true),
                "Pedidos parciales",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14), new Color(100, 70, 50)
        ));
        add(sc, BorderLayout.EAST);

        JButton agregar = new JButton("Agregar");
        JButton finalizar = new JButton("Finalizar");
        JButton volver = new JButton("Volver");

        Font btnFont = new Font("SansSerif", Font.BOLD, 13);
        Color cafe = new Color(100, 70, 50);

        for (JButton btn : List.of(agregar, finalizar, volver)) {
            btn.setFont(btnFont);
            btn.setBackground(Color.WHITE);
            btn.setForeground(cafe);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(cafe));
        }

        JPanel sur = new JPanel();
        sur.setBackground(Color.WHITE);
        sur.setBorder(new EmptyBorder(10, 0, 10, 0));
        sur.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        sur.add(agregar);
        sur.add(finalizar);
        sur.add(volver);
        add(sur, BorderLayout.SOUTH);

        agregar.addActionListener(e -> {
            Pedido nuevo = panel.generarPedido();
            pedidosParciales.add(nuevo);
            actualizarTextoPedidos();
            panel.reset();
        });

        finalizar.addActionListener(e -> {
            if (pedidosParciales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay pedidos agregados.");
                return;
            }
            mostrarTicketAgrupado(empleado);
            double total = pedidosParciales.stream()
                    .mapToDouble(Pedido::getPrecioTotal)
                    .sum();
            Ticket ticketFinal = new Ticket(empleado,
                    new ArrayList<>(pedidosParciales),
                    total);
            ctrl.guardarTicket(ticketFinal);

            pedidosParciales.clear();
            pedidosArea.setText("");
            panel.reset();
        });

        volver.addActionListener(e -> {
            dispose();
            if (padre != null)
                padre.setVisible(true);
        });
    }

    private void actualizarTextoPedidos() {
        StringBuilder sb = new StringBuilder("Pedidos:\n");
        int i = 1;
        for (Pedido p : pedidosParciales) {
            sb.append(i++).append(") ").append(p.getLineaTicket()).append("\n");
        }
        pedidosArea.setText(sb.toString());
    }

    private void mostrarTicketAgrupado(String empleado) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        Map<String, Double> precios = new LinkedHashMap<>();
        double total = 0.0;

        for (Pedido p : pedidosParciales) {
            String desc = p.getDescripcion();
            conteo.put(desc, conteo.getOrDefault(desc, 0) + 1);
            precios.putIfAbsent(desc, p.getPrecioTotal());
            total += p.getPrecioTotal();
        }

        StringBuilder out = new StringBuilder();
        out.append("Empleado: ").append(empleado).append('\n');
        out.append("Fecha   : ")
                .append(LocalDateTime.now().toString().substring(0, 16))
                .append("\n\n");

        for (String desc : conteo.keySet()) {
            int q = conteo.get(desc);
            double sub = q * precios.get(desc);
            out.append(q).append("× ").append(desc)
                    .append(" - $").append(String.format("%.2f", sub))
                    .append('\n');
        }
        out.append("\nTotal: $").append(String.format("%.2f", total));

        JOptionPane.showMessageDialog(this, out.toString(),
                "Ticket", JOptionPane.INFORMATION_MESSAGE);
    }
}
