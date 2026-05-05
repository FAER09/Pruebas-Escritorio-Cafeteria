package vista;

import controlador.ControladorEmpleado;
import modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PanelConstruirBebida extends JPanel {
    private final JComboBox<ItemMenu> cbBebidas;
    private final JCheckBox[] chkCond;
    private final JLabel lblPrecio;
    private final ControladorEmpleado ctrl;
    private final List<ItemMenu> condimentos;

    public PanelConstruirBebida(ControladorEmpleado ctrl) {
        this.ctrl = ctrl;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Construir Bebida", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(80, 50, 30));
        add(titulo, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        top.setBackground(Color.WHITE);
        JLabel lblBebida = new JLabel("Bebida:");
        lblBebida.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblBebida.setForeground(new Color(80, 50, 30));
        cbBebidas = new JComboBox<>(ctrl.bebidasActivas().toArray(new ItemMenu[0]));
        cbBebidas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbBebidas.setBackground(new Color(255, 250, 245));
        cbBebidas.setForeground(new Color(80, 50, 30));
        cbBebidas.setMaximumRowCount(5);
        top.add(lblBebida);
        top.add(cbBebidas);
        add(top, BorderLayout.PAGE_START);

        condimentos = ctrl.condimentosActivos();
        chkCond = new JCheckBox[condimentos.size()];
        JPanel centro = new JPanel();
        centro.setLayout(new GridLayout(condimentos.size(), 1, 5, 5));
        centro.setBackground(Color.WHITE);
        centro.setBorder(new LineBorder(new Color(200, 180, 150), 1, true));
        centro.setOpaque(true);

        for (int i = 0; i < condimentos.size(); i++) {
            chkCond[i] = new JCheckBox(condimentos.get(i).toString());
            chkCond[i].setFont(new Font("Segoe UI", Font.PLAIN, 14));
            chkCond[i].setForeground(new Color(80, 50, 30));
            chkCond[i].setBackground(Color.WHITE);
            centro.add(chkCond[i]);
        }
        JScrollPane scroll = new JScrollPane(centro);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Color.WHITE);

        lblPrecio = new JLabel("Precio: $0.00", SwingConstants.CENTER);
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPrecio.setForeground(new Color(80, 50, 30));
        lblPrecio.setBorder(new EmptyBorder(10, 0, 10, 0));
        panelInferior.add(lblPrecio, BorderLayout.NORTH);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.WHITE);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        actualizarPrecio();
        cbBebidas.addActionListener(e -> actualizarPrecio());
        for (JCheckBox c : chkCond) {
            c.addActionListener(e -> actualizarPrecio());
        }
    }

    private void actualizarPrecio() {
        ItemMenu item = (ItemMenu) cbBebidas.getSelectedItem();
        if (item == null) return;

        Bebida bebida = instanciarBebida(item.getNombre(), item.getPrecio());
        for (int i = 0; i < chkCond.length; i++) {
            if (chkCond[i].isSelected()) {
                ItemMenu cond = condimentos.get(i);
                bebida = decorar(bebida, cond.getNombre(), cond.getPrecio());
            }
        }
        lblPrecio.setText("Precio: $" + String.format("%.2f", bebida.obtenerCosto()));
    }

    private Bebida instanciarBebida(String nombre, double precio) {
        return new BebidaGenerica(nombre, precio);
    }

    private Bebida decorar(Bebida bebida, String nombre, double precio) {
        return new CondimentoGenerico(bebida, nombre, precio);
    }

    public Pedido generarPedido() {
        ItemMenu bebidaItem = (ItemMenu) cbBebidas.getSelectedItem();
        Bebida bebida = instanciarBebida(bebidaItem.getNombre(), bebidaItem.getPrecio());
        List<String> nombresCond = new ArrayList<>();
        for (int i = 0; i < chkCond.length; i++) {
            if (chkCond[i].isSelected()) {
                ItemMenu cond = condimentos.get(i);
                bebida = decorar(bebida, cond.getNombre(), cond.getPrecio());
                nombresCond.add(cond.getNombre());
            }
        }
        String desc = bebidaItem.getNombre();
        if (!nombresCond.isEmpty()) {
            desc += " + " + String.join(" + ", nombresCond);
        }
        return new Pedido(1, desc, bebida.obtenerCosto());
    }

    public void reset() {
        for (JCheckBox c : chkCond) c.setSelected(false);
        actualizarPrecio();
    }

    public static void aplicarEstiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(80, 50, 30));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 1));
    }
}
