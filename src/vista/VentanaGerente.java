package vista;

import controlador.ControladorGerente;
import controlador.ControladorUsuario;
import modelo.ItemMenu;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class VentanaGerente extends JFrame {
    private final ControladorGerente ctrl = new ControladorGerente();
    private final JFrame padre;

    public VentanaGerente(JFrame padre) {
        super("Gerente - Cafetería Decorator");
        this.padre = padre;

        setSize(800, 600);
        setLocationRelativeTo(padre);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.setBackground(Color.WHITE);
        tabs.setBorder(new EmptyBorder(5, 5, 5, 5));

        tabs.addTab("Bebidas", crearPanel(ItemMenu.Tipo.BEBIDA));
        tabs.addTab("Condimentos", crearPanel(ItemMenu.Tipo.CONDIMENTO));
        tabs.addTab("Ventas", crearPanelVentas());
        tabs.addTab("Gestión Usuarios", crearPanelUsuarios());
        add(tabs, BorderLayout.CENTER);

        JButton cerrar = new JButton("Cerrar sesión");
        cerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        cerrar.setBackground(Color.WHITE);
        cerrar.setForeground(new Color(80, 50, 30));
        cerrar.setFocusPainted(false);
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        south.setBackground(Color.WHITE);
        south.add(cerrar);
        south.setBorder(new EmptyBorder(0, 0, 10, 10));
        add(south, BorderLayout.SOUTH);

        cerrar.addActionListener(e -> {
            dispose();
            if (padre != null)
                padre.setVisible(true);
        });
    }

    private JPanel crearPanel(ItemMenu.Tipo tipo) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<ItemMenu> lista = ctrl.lista(tipo);
        ItemTableModel modelo = new ItemTableModel(lista, tipo);
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(24);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 220, 210));
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(210, 180, 140));
        tabla.setSelectionForeground(Color.WHITE);
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(240, 230, 220));
        header.setForeground(new Color(80, 50, 30));

        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton add = new JButton("Añadir");
        JButton del = new JButton("Eliminar");
        JButton tog = new JButton("Activar/Desactivar");
        for (JButton btn : List.of(add, del, tog)) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(80, 50, 30));
            btn.setFocusPainted(false);
        }
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btns.setBackground(Color.WHITE);
        btns.add(add);
        btns.add(del);
        btns.add(tog);
        p.add(btns, BorderLayout.SOUTH);

        add.addActionListener(e -> ctrl.addNuevo(tipo, modelo));
        del.addActionListener(e -> ctrl.eliminar(tabla.getSelectedRow(), modelo));
        tog.addActionListener(e -> ctrl.toggle(tabla.getSelectedRow(), modelo));

        return p;
    }

    private JPanel crearPanelVentas() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout(5, 5));
        top.setBackground(Color.WHITE);
        JTextField buscador = new JTextField();
        buscador.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBuscar.setBackground(Color.WHITE);
        btnBuscar.setForeground(new Color(80, 50, 30));
        btnBuscar.setFocusPainted(false);
        JLabel lbl = new JLabel("Buscar por empleado:");
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        top.add(lbl, BorderLayout.WEST);
        top.add(buscador, BorderLayout.CENTER);
        top.add(btnBuscar, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 180, 150), 1));
        p.add(scroll, BorderLayout.CENTER);

        cargarVentas(area, "");
        btnBuscar.addActionListener(e -> cargarVentas(area, buscador.getText().trim()));

        return p;
    }

    private void cargarVentas(JTextArea area, String nombreEmpleado) {
        area.setText("");
        try (BufferedReader br = new BufferedReader(new FileReader("compras.txt"))) {
            String linea;
            StringBuilder bloque = new StringBuilder();
            boolean match = nombreEmpleado.isEmpty();
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Empleado:")) {
                    match = linea.toLowerCase().contains(nombreEmpleado.toLowerCase());
                }
                if (match) bloque.append(linea).append("\n");
                if (linea.equals("---")) {
                    if (match) area.append(bloque.toString() + "\n");
                    bloque.setLength(0);
                    match = nombreEmpleado.isEmpty();
                }
            }
        } catch (IOException e) {
            area.setText("No se pudo leer el archivo de ventas.");
        }
    }

    private JPanel crearPanelUsuarios() {
        ControladorUsuario ctrlU = new ControladorUsuario();
        List<Usuario> usuarios = ctrlU.obtenerTodos();
        String[] cols = { "Nombre", "Rol" };
        Object[][] datos = new Object[usuarios.size()][2];
        for (int i = 0; i < usuarios.size(); i++) {
            datos[i][0] = usuarios.get(i).getNombre();
            datos[i][1] = usuarios.get(i).getRol().name();
        }

        JTable tabla = new JTable(datos, cols);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(24);
        tabla.setGridColor(new Color(230, 220, 210));
        tabla.setShowGrid(true);
        tabla.setBackground(Color.WHITE);
        tabla.setSelectionBackground(new Color(210, 180, 140));
        tabla.setSelectionForeground(Color.WHITE);
        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("SansSerif", Font.BOLD, 13));
        th.setBackground(new Color(240, 230, 220));
        th.setForeground(new Color(80, 50, 30));

        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Añadir");
        JButton btnDel = new JButton("Eliminar");
        for (JButton btn : List.of(btnAdd, btnDel)) {
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));
            btn.setBackground(Color.WHITE);
            btn.setForeground(new Color(80, 50, 30));
            btn.setFocusPainted(false);
        }
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btns.setBackground(Color.WHITE);
        btns.add(btnAdd);
        btns.add(btnDel);
        p.add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Nombre:");
            if (nombre == null || nombre.trim().isEmpty()) return;
            String pass = JOptionPane.showInputDialog(this, "Contraseña:");
            if (pass == null) return;
            String rolSel = (String) JOptionPane.showInputDialog(
                    this, "Rol:", "Seleccione rol",
                    JOptionPane.PLAIN_MESSAGE, null,
                    new String[]{"GERENTE", "EMPLEADO"}, "EMPLEADO");
            Usuario.Rol rol = Usuario.Rol.valueOf(rolSel);
            ctrlU.agregar(new Usuario(nombre, pass, rol));
            dispose();
            new VentanaGerente(padre).setVisible(true);
        });

        btnDel.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila < 0) return;
            ctrlU.eliminar(fila);
            dispose();
            new VentanaGerente(padre).setVisible(true);
        });

        return p;
    }
}
