Paquete src:  
Paquete controlador:  
package controlador;

import java.util.List;  
import java.util.stream.Collectors;  
import modelo.ItemMenu;  
import modelo.ItemMenu.Tipo;  
import modelo.Ticket;  
import persistencia.ArchivoDAO;

public class ControladorEmpleado {  
    private final ArchivoDAO dao \= new ArchivoDAO();

    public List\<ItemMenu\> bebidasActivas() {  
        return dao.cargar(Tipo.BEBIDA).stream()  
                .filter(ItemMenu::isActivo)  
                .collect(Collectors.toList());  
    }

    public List\<ItemMenu\> condimentosActivos() {  
        return dao.cargar(Tipo.CONDIMENTO).stream()  
                .filter(ItemMenu::isActivo)  
                .collect(Collectors.toList());  
    }

    public void guardarTicket(Ticket t) {  
        dao.guardarTicket(t);  
    }  
}

//  
package controlador;

import java.util.List;  
import javax.swing.\*;  
import modelo.ItemMenu;  
import modelo.ItemMenu.Tipo;  
import persistencia.ArchivoDAO;  
import vista.ItemTableModel;

public class ControladorGerente {

    private final ArchivoDAO dao \= new ArchivoDAO();

    public List\<ItemMenu\> lista(Tipo t) {  
        return dao.cargar(t);  
    }

    public void addNuevo(Tipo t, ItemTableModel modelo) {  
        String nombre \= JOptionPane.showInputDialog("Nombre:");  
        if (nombre \== null || nombre.trim().isEmpty()) {  
            return;  
        }

        nombre \= nombre.trim();

        for (ItemMenu item : modelo.getItems()) {  
            if (item.getNombre().trim().equalsIgnoreCase(nombre)) {  
                JOptionPane.showMessageDialog(null, "Ya existe un " \+ t.name().toLowerCase() \+ " con ese nombre.");  
                return;  
            }  
        }

        String prec \= JOptionPane.showInputDialog("Precio:");  
        double p;  
        try {  
            p \= Double.parseDouble(prec);  
        } catch (NumberFormatException ex) {  
            return;  
        }

        modelo.addItem(new ItemMenu(nombre, p, true, t));  
        dao.guardar(modelo.getItems(), t);  
    }

    public void eliminar(int fila, ItemTableModel modelo) {  
        if (fila \< 0) {  
            return;  
        }  
        modelo.removeItem(fila);  
        dao.guardar(modelo.getItems(), modelo.getTipo());  
    }

    public void toggle(int fila, ItemTableModel modelo) {  
        if (fila \< 0) {  
            return;  
        }  
        ItemMenu it \= modelo.getItems().get(fila);  
        it.setActivo(\!it.isActivo());  
        modelo.fireTableRowsUpdated(fila, fila);  
        dao.guardar(modelo.getItems(), modelo.getTipo());  
    }

    public List\<String\> buscarCompras(String cliente) {  
        return dao.ticketsDe(cliente);  
    }  
}

//  
package controlador;

import java.util.List;  
import modelo.Usuario;  
import modelo.Usuario.Rol;  
import persistencia.UsuarioDAO;

public class ControladorUsuario {

    private final UsuarioDAO dao \= new UsuarioDAO();  
    private List\<Usuario\> cache \= dao.cargarTodos();

    public boolean validarCredenciales(String nombre, String pass, Rol rol) {  
        return cache.stream()  
                .anyMatch(u \-\> u.getNombre().equalsIgnoreCase(nombre)  
                && u.getPassword().equals(pass)  
                && u.getRol() \== rol);  
    }

    public boolean validarNombre(String nombre) {  
        return cache.stream()  
                .anyMatch(u \-\> u.getNombre().equalsIgnoreCase(nombre)  
                && u.getRol() \== Rol.EMPLEADO);  
    }

    public List\<Usuario\> obtenerTodos() {  
        return cache;  
    }

    public void agregar(Usuario u) {  
        cache.add(u);  
        dao.guardarTodos(cache);  
    }

    public void eliminar(int indice) {  
        if (indice \>= 0 && indice \< cache.size()) {  
            cache.remove(indice);  
            dao.guardarTodos(cache);  
        }  
    }

    public Usuario obtenerUsuario(String nombre, String pass) {  
        this.cache \= dao.leerTodos();  
        return cache.stream()  
                .filter(u \-\> u.getNombre().equalsIgnoreCase(nombre)  
                && u.getPassword().equals(pass))  
                .findFirst()  
                .orElse(null);  
    }

}  
Paquete Modelo:  
package modelo;

public abstract class Bebida {  
    public abstract String obtenerDescripcion();

    public abstract double obtenerCosto();  
}

//  
package modelo;

public class BebidaGenerica extends Bebida {  
    private final String nombre;  
    private final double precio;

    public BebidaGenerica(String nombre, double precio) {  
        this.nombre \= nombre;  
        this.precio \= precio;  
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

//  
package modelo;

public class CondimentoGenerico extends DecoradorCondimento {  
    private final String nombre;  
    private final double precio;

    public CondimentoGenerico(Bebida bebida, String nombre, double precio) {  
        super(bebida);  
        this.nombre \= nombre;  
        this.precio \= precio;  
    }

    @Override  
    public String obtenerDescripcion() {  
        return bebida.obtenerDescripcion() \+ " \+ " \+ nombre;  
    }

    @Override  
    public double obtenerCosto() {  
        return bebida.obtenerCosto() \+ precio;  
    }  
}

//  
package modelo;

public abstract class DecoradorCondimento extends Bebida {  
    protected Bebida bebida;

    public DecoradorCondimento(Bebida bebida) {  
        this.bebida \= bebida;  
    }  
}

//  
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
        nombre \= n;  
        precio \= p;  
        activo \= a;  
        tipo \= t;  
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
        precio \= p;  
    }

    public void setActivo(boolean a) {  
        activo \= a;  
    }

    public String toString() {  
        return nombre \+ " ($" \+ String.format("%.2f", precio) \+ ")";  
    }  
}

//  
package modelo;

public class Pedido {  
    private final int cantidad;  
    private final String descripcion;  
    private final double precioTotal;

    public Pedido(int cantidad, String descripcion, double precioTotal) {  
        this.cantidad \= cantidad;  
        this.descripcion \= descripcion;  
        this.precioTotal \= precioTotal;  
    }

    public String getLineaTicket() {  
        return cantidad \+ "× " \+ descripcion \+ " \- $" \+ String.format("%.2f", precioTotal);  
    }

    public double getPrecioTotal() {  
        return precioTotal;  
    }

    public String getDescripcion() {  
        return descripcion;  
    }

}

//  
package modelo;

import java.time.LocalDateTime;  
import java.time.format.DateTimeFormatter;  
import java.util.List;

public class Ticket {  
    private String cliente;  
    private LocalDateTime fechaHora;  
    private List\<Pedido\> pedidos;  
    private double total;

    public Ticket(String cliente, List\<Pedido\> pedidos, double total) {  
        this.cliente \= cliente;  
        this.pedidos \= pedidos;  
        this.total \= total;  
        this.fechaHora \= LocalDateTime.now();  
    }

    /\* \---------------- getters útiles \---------------- \*/  
    public List\<Pedido\> getPedidos() {  
        return pedidos;  
    }

    public double getTotal() {  
        return total;  
    }

    /\* \--------------- Formato legible \--------------- \*/  
    public String formato() {  
        DateTimeFormatter fmt \= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");  
        StringBuilder sb \= new StringBuilder();  
        sb.append("Empleado: ").append(cliente).append("\\n");  
        sb.append("Fecha   : ").append(fechaHora.format(fmt)).append("\\n");

        for (Pedido p : pedidos) {  
            sb.append(p.getLineaTicket()).append("\\n");  
        }

        sb.append("Total: $").append(String.format("%.2f", total)).append("\\n");  
        sb.append("---\\n");  
        return sb.toString();  
    }

}

//  
package modelo;

public class Usuario {  
    public enum Rol {  
        GERENTE, EMPLEADO  
    }

    private String nombre;  
    private String password;  
    private Rol rol;

    public Usuario(String nombre, String password, Rol rol) {  
        this.nombre \= nombre;  
        this.password \= password;  
        this.rol \= rol;  
    }

    public String getNombre() {  
        return nombre;  
    }

    public String getPassword() {  
        return password;  
    }

    public Rol getRol() {  
        return rol;  
    }  
}  
Paquete persistencia:  
package persistencia;

import modelo.ItemMenu;  
import modelo.ItemMenu.Tipo;  
import modelo.Ticket;

import java.io.\*;  
import java.nio.file.\*;  
import java.util.ArrayList;  
import java.util.List;

public class ArchivoDAO {

    private static final String BEB \= "bebidas.txt";  
    private static final String CON \= "condimentos.txt";  
    private static final String COM \= "compras.txt";

    /\* \---------- cargar y guardar productos \---------- \*/

    public List\<ItemMenu\> cargar(Tipo t) {  
        String archivo \= (t \== Tipo.BEBIDA) ? BEB : CON;  
        List\<ItemMenu\> lista \= new ArrayList\<ItemMenu\>();  
        Path p \= Paths.get(archivo);

        if (\!Files.exists(p))  
            return lista;

        try (BufferedReader br \= Files.newBufferedReader(p)) {  
            String linea;  
            while ((linea \= br.readLine()) \!= null) {  
                if (linea.startsWith("\#") || linea.trim().isEmpty())  
                    continue;  
                String\[\] parts \= linea.split("\\\\|");  
                String n \= parts\[0\];  
                double pr \= Double.parseDouble(parts\[1\]);  
                boolean ac \= Boolean.parseBoolean(parts\[2\]);  
                lista.add(new ItemMenu(n, pr, ac, t));  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return lista;  
    }

    public void guardar(List\<ItemMenu\> lista, Tipo t) {  
        String archivo \= (t \== Tipo.BEBIDA) ? BEB : CON;  
        try (BufferedWriter bw \= Files.newBufferedWriter(Paths.get(archivo))) {  
            for (ItemMenu it : lista) {  
                bw.write(it.getNombre() \+ "|" \+ it.getPrecio() \+ "|" \+ it.isActivo());  
                bw.newLine();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    /\* \---------- tickets \---------- \*/

    public void guardarTicket(Ticket tk) {  
        try (BufferedWriter bw \= new BufferedWriter(new FileWriter(COM, true))) {  
            bw.write(tk.formato());  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    public List\<String\> ticketsDe(String cliente) {  
        List\<String\> list \= new ArrayList\<String\>();  
        if (\!Files.exists(Paths.get(COM)))  
            return list;

        try (BufferedReader br \= new BufferedReader(new FileReader(COM))) {  
            StringBuilder bloque \= new StringBuilder();  
            String linea;  
            boolean match \= false;  
            while ((linea \= br.readLine()) \!= null) {  
                if (linea.startsWith("Cliente:"))  
                    match \= linea.toLowerCase().contains(cliente.toLowerCase());

                bloque.append(linea).append('\\n');

                if (linea.equals("---")) {  
                    if (match)  
                        list.add(bloque.toString());  
                    bloque.setLength(0);  
                    match \= false;  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return list;  
    }  
}

//  
package persistencia;

import java.io.\*;  
import java.nio.file.\*;  
import java.util.\*;  
import modelo.Usuario;  
import modelo.Usuario.Rol;

public class UsuarioDAO {

    private static final String USUARIOS\_FILE \= "usuarios.txt";

    public List\<Usuario\> cargarTodos() {  
        List\<Usuario\> lista \= new ArrayList\<\>();  
        Path path \= Paths.get(USUARIOS\_FILE);  
        if (\!Files.exists(path)) {  
            return lista;  
        }  
        try (BufferedReader br \= Files.newBufferedReader(path)) {  
            String line;  
            while ((line \= br.readLine()) \!= null) {  
                if (line.trim().isEmpty() || line.startsWith("\#")) {  
                    continue;  
                }  
                String\[\] parts \= line.split("\\\\|");  
                String nombre \= parts\[0\];  
                String pass \= parts\[1\];  
                Rol rol \= Rol.valueOf(parts\[2\]);  
                lista.add(new Usuario(nombre, pass, rol));  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return lista;  
    }

    public void guardarTodos(List\<Usuario\> usuarios) {  
        try (BufferedWriter bw \= Files.newBufferedWriter(Paths.get(USUARIOS\_FILE))) {  
            for (Usuario u : usuarios) {  
                bw.write(u.getNombre() \+ "|" \+ u.getPassword() \+ "|" \+ u.getRol().name());  
                bw.newLine();  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }

    public List\<Usuario\> leerTodos() {  
        List\<Usuario\> usuarios \= new ArrayList\<\>();  
        try (BufferedReader br \= new BufferedReader(new FileReader("usuarios.txt"))) {  
            String linea;  
            while ((linea \= br.readLine()) \!= null) {  
                String\[\] partes \= linea.split("\\\\|");  
                if (partes.length \== 3) {  
                    String nombre \= partes\[0\].trim();  
                    String pass \= partes\[1\].trim();  
                    Usuario.Rol rol \= Usuario.Rol.valueOf(partes\[2\].trim().toUpperCase());  
                    usuarios.add(new Usuario(nombre, pass, rol));  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return usuarios;  
    }

}  
Paquete util:  
package util;

import java.awt.\*;  
import javax.swing.\*;

public class SpringUtilities {

    public static void makeCompactGrid(Container parent,  
            int rows, int cols,  
            int initialX, int initialY,  
            int xPad, int yPad) {  
        SpringLayout layout \= (SpringLayout) parent.getLayout();  
        Spring xPadSpring \= Spring.constant(xPad);  
        Spring yPadSpring \= Spring.constant(yPad);  
        Spring initialXSpring \= Spring.constant(initialX);  
        Spring initialYSpring \= Spring.constant(initialY);

        int max \= rows \* cols;  
        Spring maxWidthSpring \= Spring.constant(0);  
        Spring maxHeightSpring \= Spring.constant(0);

        for (int i \= 0; i \< max; i\++) {  
            SpringLayout.Constraints cons \= layout.getConstraints(  
                    parent.getComponent(i));  
            maxWidthSpring \= Spring.max(maxWidthSpring, cons.getWidth());  
            maxHeightSpring \= Spring.max(maxHeightSpring, cons.getHeight());  
        }  
        for (int i \= 0; i \< max; i\++) {  
            SpringLayout.Constraints cons \= layout.getConstraints(  
                    parent.getComponent(i));  
            cons.setWidth(maxWidthSpring);  
            cons.setHeight(maxHeightSpring);  
        }

        Spring y \= initialYSpring;  
        for (int r \= 0; r \< rows; r\++) {  
            Spring x \= initialXSpring;  
            for (int c \= 0; c \< cols; c\++) {  
                SpringLayout.Constraints cons \= layout.getConstraints(  
                        parent.getComponent(r \* cols \+ c));  
                cons.setX(x);  
                cons.setY(y);  
                x \= Spring.sum(x, Spring.sum(maxWidthSpring, xPadSpring));  
            }  
            y \= Spring.sum(y, Spring.sum(maxHeightSpring, yPadSpring));  
        }  
    }  
}  
Paquete vista:  
package vista;

import modelo.ItemMenu;

import javax.swing.\*;  
import javax.swing.table.AbstractTableModel;  
import javax.swing.table.JTableHeader;  
import java.awt.\*;  
import java.util.List;

public class ItemTableModel extends AbstractTableModel {  
    private final List\<ItemMenu\> items;  
    private final String\[\] cols \= { "Nombre", "Precio", "Activo" };  
    private final ItemMenu.Tipo tipo;

    public ItemTableModel(List\<ItemMenu\> list, ItemMenu.Tipo tipo) {  
        this.items \= list;  
        this.tipo \= tipo;  
    }

    @Override  
    public int getRowCount() {  
        return items.size();  
    }

    @Override  
    public int getColumnCount() {  
        return cols.length;  
    }

    @Override  
    public String getColumnName(int col) {  
        return cols\[col\];  
    }

    @Override  
    public Object getValueAt(int row, int col) {  
        ItemMenu item \= items.get(row);  
        return switch (col) {  
            case 0 \-\> item.getNombre();  
            case 1 \-\> String.format("$%.2f", item.getPrecio());  
            case 2 \-\> item.isActivo() ? "Sí" : "No";  
            default \-\> "";  
        };  
    }

    public void addItem(ItemMenu item) {  
        items.add(item);  
        fireTableDataChanged();  
    }

    public void removeItem(int row) {  
        items.remove(row);  
        fireTableDataChanged();  
    }

    public List\<ItemMenu\> getItems() {  
        return items;  
    }

    public ItemMenu.Tipo getTipo() {  
        return tipo;  
    }

    // Metodo para aplicar estilo consistente tipo cafetería  
    public static void aplicarEstiloTabla(JTable tabla) {  
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));  
        tabla.setRowHeight(28);  
        tabla.setGridColor(new Color(230, 220, 210));  
        tabla.setShowGrid(true);  
        tabla.setBackground(Color.WHITE);  
        tabla.setForeground(new Color(90, 60, 40));  
        tabla.setSelectionBackground(new Color(210, 180, 140));  
        tabla.setSelectionForeground(Color.WHITE);

        JTableHeader header \= tabla.getTableHeader();  
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));  
        header.setBackground(new Color(240, 230, 220));  
        header.setForeground(new Color(80, 50, 30));  
    }

    // Metodo auxiliar para aplicar estilo a botones  
    public static void aplicarEstiloBoton(JButton boton) {  
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));  
        boton.setBackground(Color.WHITE);  
        boton.setForeground(new Color(80, 50, 30));  
        boton.setFocusPainted(false);  
        boton.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 1));  
    }

}

//  
package vista;

import controlador.ControladorEmpleado;  
import modelo.\*;

import javax.swing.\*;  
import javax.swing.border.EmptyBorder;  
import javax.swing.border.LineBorder;  
import java.awt.\*;  
import java.util.ArrayList;  
import java.util.List;

public class PanelConstruirBebida extends JPanel {  
    private final JComboBox\<ItemMenu\> cbBebidas;  
    private final JCheckBox\[\] chkCond;  
    private final JLabel lblPrecio;  
    private final ControladorEmpleado ctrl;  
    private final List\<ItemMenu\> condimentos;

    public PanelConstruirBebida(ControladorEmpleado ctrl) {  
        this.ctrl \= ctrl;  
        setLayout(new BorderLayout(10, 10));  
        setBackground(Color.WHITE);  
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Titulo  
        JLabel titulo \= new JLabel("Construir Bebida", SwingConstants.CENTER);  
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));  
        titulo.setForeground(new Color(80, 50, 30));  
        add(titulo, BorderLayout.NORTH);

        // Panel de seleccion de bebida  
        JPanel top \= new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));  
        top.setBackground(Color.WHITE);  
        JLabel lblBebida \= new JLabel("Bebida:");  
        lblBebida.setFont(new Font("Segoe UI", Font.PLAIN, 14));  
        lblBebida.setForeground(new Color(80, 50, 30));  
        cbBebidas \= new JComboBox\<\>(ctrl.bebidasActivas().toArray(new ItemMenu\[0\]));  
        cbBebidas.setFont(new Font("Segoe UI", Font.PLAIN, 14));  
        cbBebidas.setBackground(new Color(255, 250, 245));  
        cbBebidas.setForeground(new Color(80, 50, 30));  
        cbBebidas.setMaximumRowCount(5);  
        top.add(lblBebida);  
        top.add(cbBebidas);  
        add(top, BorderLayout.PAGE\_START);

        // Panel de condimentos  
        condimentos \= ctrl.condimentosActivos();  
        chkCond \= new JCheckBox\[condimentos.size()\];  
        JPanel centro \= new JPanel();  
        centro.setLayout(new GridLayout(condimentos.size(), 1, 5, 5));  
        centro.setBackground(Color.WHITE);  
        centro.setBorder(new LineBorder(new Color(200, 180, 150), 1, true));  
        centro.setOpaque(true);

        for (int i \= 0; i \< condimentos.size(); i\++) {  
            chkCond\[i\] \= new JCheckBox(condimentos.get(i).toString());  
            chkCond\[i\].setFont(new Font("Segoe UI", Font.PLAIN, 14));  
            chkCond\[i\].setForeground(new Color(80, 50, 30));  
            chkCond\[i\].setBackground(Color.WHITE);  
            centro.add(chkCond\[i\]);  
        }  
        JScrollPane scroll \= new JScrollPane(centro);  
        scroll.setBorder(BorderFactory.createEmptyBorder());  
        add(scroll, BorderLayout.CENTER);

        // Panel inferior con precio y botones  
        JPanel panelInferior \= new JPanel(new BorderLayout());  
        panelInferior.setBackground(Color.WHITE);

        lblPrecio \= new JLabel("Precio: $0.00", SwingConstants.CENTER);  
        lblPrecio.setFont(new Font("Segoe UI", Font.BOLD, 16));  
        lblPrecio.setForeground(new Color(80, 50, 30));  
        lblPrecio.setBorder(new EmptyBorder(10, 0, 10, 0));  
        panelInferior.add(lblPrecio, BorderLayout.NORTH);

        JPanel panelBotones \= new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));  
        panelBotones.setBackground(Color.WHITE);

        // Listener del boton Reset  
        panelInferior.add(panelBotones, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        // Listeners  
        actualizarPrecio();  
        cbBebidas.addActionListener(e \-\> actualizarPrecio());  
        for (JCheckBox c : chkCond) {  
            c.addActionListener(e \-\> actualizarPrecio());  
        }  
    }

    private void actualizarPrecio() {  
        ItemMenu item \= (ItemMenu) cbBebidas.getSelectedItem();  
        if (item \== null) return;

        Bebida bebida \= instanciarBebida(item.getNombre(), item.getPrecio());  
        for (int i \= 0; i \< chkCond.length; i\++) {  
            if (chkCond\[i\].isSelected()) {  
                ItemMenu cond \= condimentos.get(i);  
                bebida \= decorar(bebida, cond.getNombre(), cond.getPrecio());  
            }  
        }  
        lblPrecio.setText("Precio: $" \+ String.format("%.2f", bebida.obtenerCosto()));  
    }

    private Bebida instanciarBebida(String nombre, double precio) {  
        return new BebidaGenerica(nombre, precio);  
    }

    private Bebida decorar(Bebida bebida, String nombre, double precio) {  
        return new CondimentoGenerico(bebida, nombre, precio);  
    }

    public Pedido generarPedido() {  
        ItemMenu bebidaItem \= (ItemMenu) cbBebidas.getSelectedItem();  
        Bebida bebida \= instanciarBebida(bebidaItem.getNombre(), bebidaItem.getPrecio());  
        List\<String\> nombresCond \= new ArrayList\<\>();  
        for (int i \= 0; i \< chkCond.length; i\++) {  
            if (chkCond\[i\].isSelected()) {  
                ItemMenu cond \= condimentos.get(i);  
                bebida \= decorar(bebida, cond.getNombre(), cond.getPrecio());  
                nombresCond.add(cond.getNombre());  
            }  
        }  
        String desc \= bebidaItem.getNombre();  
        if (\!nombresCond.isEmpty()) {  
            desc \+= " \+ " \+ String.join(" \+ ", nombresCond);  
        }  
        return new Pedido(1, desc, bebida.obtenerCosto());  
    }

    public void reset() {  
        for (JCheckBox c : chkCond) c.setSelected(false);  
        actualizarPrecio();  
    }

    // Metodo auxiliar para aplicar estilo a botones blanco con letras cafe  
    public static void aplicarEstiloBoton(JButton boton) {  
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));  
        boton.setBackground(Color.WHITE); // Fondo  
        boton.setForeground(new Color(80, 50, 30)); // Letras  
        boton.setFocusPainted(false);  
        boton.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 1));  
    }  
}

//  
package vista;

import javax.imageio.ImageIO;  
import javax.swing.\*;  
import javax.swing.border.\*;  
import java.awt.\*;  
import java.io.IOException;  
import java.net.URL;

import controlador.ControladorUsuario;  
import modelo.Usuario;  
import modelo.Usuario.Rol;

public class PantallaLogin extends JFrame {  
    private final JTextField tfUser \= new JTextField(15);  
    private final JPasswordField tfPass \= new JPasswordField(15);  
    private final ControladorUsuario ctrlUsuario \= new ControladorUsuario();

    public PantallaLogin() {  
        super("Login \- Cafetería Decorator");  
        setDefaultCloseOperation(EXIT\_ON\_CLOSE);  
        setSize(600, 300);  
        setLocationRelativeTo(null);  
        setResizable(false);

        // Panel principal  
        JPanel panelPrincipal \= new JPanel(new BorderLayout());  
        panelPrincipal.setBackground(Color.WHITE);  
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));  
        setContentPane(panelPrincipal);

        // Imagen desde URL  
        JLabel lblImagen \= new JLabel();  
        try {  
            URL imageUrl \= new URL("https://img.freepik.com/psd-gratis/ilustracion-cafe-dibujado-mano\_23-2150795456.jpg?semt=ais\_items\_boosted\&w=740");  
            Image image \= ImageIO.read(imageUrl).getScaledInstance(180, 220, Image.SCALE\_SMOOTH);  
            lblImagen.setIcon(new ImageIcon(image));  
        } catch (IOException e) {  
            lblImagen.setText("Imagen no disponible");  
        }  
        lblImagen.setBorder(new EmptyBorder(0, 0, 0, 20));  
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);  
        panelPrincipal.add(lblImagen, BorderLayout.WEST);

        // Panel formulario centrado  
        JPanel contenedorCentro \= new JPanel(new GridBagLayout());  
        contenedorCentro.setBackground(Color.WHITE);  
        panelPrincipal.add(contenedorCentro, BorderLayout.CENTER);

        JPanel panelForm \= new JPanel();  
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y\_AXIS));  
        panelForm.setBackground(Color.WHITE);  
        panelForm.setBorder(new EmptyBorder(10, 30, 10, 30));

        // Campo Usuario  
        panelForm.add(crearCampo("Usuario", tfUser));  
        panelForm.add(Box.createVerticalStrut(15));  
        // Campo Contraseña  
        panelForm.add(crearCampo("Contraseña", tfPass));  
        panelForm.add(Box.createVerticalStrut(25));

        // Boton Iniciar sesion  
        JButton btnLogin \= new JButton("Iniciar sesión");  
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));  
        btnLogin.setBackground(new Color(198, 143, 132));  
        btnLogin.setForeground(new Color(80, 50, 30));  
        btnLogin.setFocusPainted(false);  
        btnLogin.setAlignmentX(Component.CENTER\_ALIGNMENT);  
        btnLogin.setMaximumSize(new Dimension(180, 40));  
        panelForm.add(btnLogin);

        contenedorCentro.add(panelForm);

        // Accion del boton  
        btnLogin.addActionListener(e \-\> intentarLogin());  
    }

    private JPanel crearCampo(String etiqueta, JComponent campo) {  
        JPanel panel \= new JPanel(new BorderLayout(5, 5));  
        panel.setBackground(Color.WHITE);

        JLabel lbl \= new JLabel(etiqueta, SwingConstants.CENTER);  
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));  
        lbl.setForeground(new Color(80, 50, 30));  
        lbl.setAlignmentX(Component.CENTER\_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));  
        campo.setBackground(new Color(255, 250, 245));  
        campo.setBorder(new LineBorder(new Color(200, 180, 150), 1, true));

        panel.add(lbl, BorderLayout.NORTH);  
        panel.add(campo, BorderLayout.CENTER);

        // Alineamos todo el panel al centro  
        panel.setAlignmentX(Component.CENTER\_ALIGNMENT);  
        panel.setMaximumSize(new Dimension(260, 60));

        return panel;  
    }

    private void intentarLogin() {  
        String user \= tfUser.getText().trim();  
        String pass \= new String(tfPass.getPassword());

        Usuario usuario \= ctrlUsuario.obtenerUsuario(user, pass);

        if (usuario \== null) {  
            JOptionPane.showMessageDialog(this,  
                "Credenciales incorrectas", "Error",  
                JOptionPane.ERROR\_MESSAGE);  
            tfUser.setText("");  
            tfPass.setText("");  
            return;  
        }

        tfUser.setText("");  
        tfPass.setText("");  
        dispose();

        if (usuario.getRol() \== Rol.GERENTE) {  
            new VentanaGerente(this).setVisible(true);  
        } else {  
            new VentanaEmpleado(this, user).setVisible(true);  
        }  
    }  
}

//  
package vista;

import controlador.ControladorEmpleado;  
import modelo.Pedido;  
import modelo.Ticket;

import javax.swing.\*;  
import javax.swing.border.EmptyBorder;  
import javax.swing.border.TitledBorder;  
import java.awt.\*;  
import java.time.LocalDateTime;  
import java.util.ArrayList;  
import java.util.LinkedHashMap;  
import java.util.List;  
import java.util.Map;

public class VentanaEmpleado extends JFrame {

    private final JFrame padre;  
    private final ControladorEmpleado ctrl \= new ControladorEmpleado();  
    private final JTextArea pedidosArea \= new JTextArea();  
    private PanelConstruirBebida panel;  
    private final List\<Pedido\> pedidosParciales \= new ArrayList\<\>();

    public VentanaEmpleado(JFrame padre, String empleado) {  
        super("Empleado \- Cafetería Decorator");  
        this.padre \= padre;

        setSize(700, 500);  
        setLocationRelativeTo(padre);  
        setDefaultCloseOperation(DISPOSE\_ON\_CLOSE);  
        setLayout(new BorderLayout(10, 10));  
        getContentPane().setBackground(Color.WHITE);

        panel \= new PanelConstruirBebida(ctrl);  
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
        JScrollPane sc \= new JScrollPane(pedidosArea);  
        sc.setPreferredSize(new Dimension(230, 0));  
        sc.setBorder(BorderFactory.createTitledBorder(  
                BorderFactory.createLineBorder(new Color(200, 180, 150), 1, true),  
                "Pedidos parciales",  
                TitledBorder.LEFT, TitledBorder.TOP,  
                new Font("SansSerif", Font.BOLD, 14), new Color(100, 70, 50)  
        ));  
        add(sc, BorderLayout.EAST);

        JButton agregar \= new JButton("Agregar");  
        JButton finalizar \= new JButton("Finalizar");  
        JButton volver \= new JButton("Volver");

        Font btnFont \= new Font("SansSerif", Font.BOLD, 13);  
        Color cafe \= new Color(100, 70, 50);

        for (JButton btn : List.of(agregar, finalizar, volver)) {  
            btn.setFont(btnFont);  
            btn.setBackground(Color.WHITE);  
            btn.setForeground(cafe);  
            btn.setFocusPainted(false);  
            btn.setBorder(BorderFactory.createLineBorder(cafe));  
        }

        JPanel sur \= new JPanel();  
        sur.setBackground(Color.WHITE);  
        sur.setBorder(new EmptyBorder(10, 0, 10, 0));  
        sur.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));  
        sur.add(agregar);  
        sur.add(finalizar);  
        sur.add(volver);  
        add(sur, BorderLayout.SOUTH);

        agregar.addActionListener(e \-\> {  
            Pedido nuevo \= panel.generarPedido();  
            pedidosParciales.add(nuevo);  
            actualizarTextoPedidos();  
            panel.reset();  
        });

        finalizar.addActionListener(e \-\> {  
            if (pedidosParciales.isEmpty()) {  
                JOptionPane.showMessageDialog(this, "No hay pedidos agregados.");  
                return;  
            }  
            mostrarTicketAgrupado(empleado);  
            double total \= pedidosParciales.stream()  
                    .mapToDouble(Pedido::getPrecioTotal)  
                    .sum();  
            Ticket ticketFinal \= new Ticket(empleado,  
                    new ArrayList\<\>(pedidosParciales),  
                    total);  
            ctrl.guardarTicket(ticketFinal);

            pedidosParciales.clear();  
            pedidosArea.setText("");  
            panel.reset();  
        });

        volver.addActionListener(e \-\> {  
            dispose();  
            if (padre \!= null)  
                padre.setVisible(true);  
        });  
    }

    private void actualizarTextoPedidos() {  
        StringBuilder sb \= new StringBuilder("Pedidos:\\n");  
        int i \= 1;  
        for (Pedido p : pedidosParciales) {  
            sb.append(i\++).append(") ").append(p.getLineaTicket()).append("\\n");  
        }  
        pedidosArea.setText(sb.toString());  
    }

    private void mostrarTicketAgrupado(String empleado) {  
        Map\<String, Integer\> conteo \= new LinkedHashMap\<\>();  
        Map\<String, Double\> precios \= new LinkedHashMap\<\>();  
        double total \= 0.0;

        for (Pedido p : pedidosParciales) {  
            String desc \= p.getDescripcion();  
            conteo.put(desc, conteo.getOrDefault(desc, 0) \+ 1);  
            precios.putIfAbsent(desc, p.getPrecioTotal());  
            total \+= p.getPrecioTotal();  
        }

        StringBuilder out \= new StringBuilder();  
        out.append("Empleado: ").append(empleado).append('\\n');  
        out.append("Fecha   : ")  
                .append(LocalDateTime.now().toString().substring(0, 16))  
                .append("\\n\\n");

        for (String desc : conteo.keySet()) {  
            int q \= conteo.get(desc);  
            double sub \= q \* precios.get(desc);  
            out.append(q).append("× ").append(desc)  
                    .append(" \- $").append(String.format("%.2f", sub))  
                    .append('\\n');  
        }  
        out.append("\\nTotal: $").append(String.format("%.2f", total));

        JOptionPane.showMessageDialog(this, out.toString(),  
                "Ticket", JOptionPane.INFORMATION\_MESSAGE);  
    }  
}

//  
package vista;

import controlador.ControladorGerente;  
import controlador.ControladorUsuario;  
import modelo.ItemMenu;  
import modelo.Usuario;

import javax.swing.\*;  
import javax.swing.border.EmptyBorder;  
import javax.swing.table.JTableHeader;  
import java.awt.\*;  
import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  
import java.util.List;

public class VentanaGerente extends JFrame {  
    private final ControladorGerente ctrl \= new ControladorGerente();  
    private final JFrame padre;

    public VentanaGerente(JFrame padre) {  
        super("Gerente \- Cafetería Decorator");  
        this.padre \= padre;

        setSize(800, 600);  
        setLocationRelativeTo(padre);  
        setDefaultCloseOperation(DISPOSE\_ON\_CLOSE);  
        getContentPane().setBackground(Color.WHITE);  
        setLayout(new BorderLayout(10, 10));

        // Pestañas principales  
        JTabbedPane tabs \= new JTabbedPane();  
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));  
        tabs.setBackground(Color.WHITE);  
        tabs.setBorder(new EmptyBorder(5,5,5,5));

        tabs.addTab("Bebidas", crearPanel(ItemMenu.Tipo.BEBIDA));  
        tabs.addTab("Condimentos", crearPanel(ItemMenu.Tipo.CONDIMENTO));  
        tabs.addTab("Ventas", crearPanelVentas());  
        tabs.addTab("Gestión Usuarios", crearPanelUsuarios());  
        add(tabs, BorderLayout.CENTER);

        // Boton de cerrar sesion  
        JButton cerrar \= new JButton("Cerrar sesión");  
        cerrar.setFont(new Font("SansSerif", Font.BOLD, 13));  
        cerrar.setBackground(Color.WHITE);  
        cerrar.setForeground(new Color(80, 50, 30));  
        cerrar.setFocusPainted(false);  
        JPanel south \= new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));  
        south.setBackground(Color.WHITE);  
        south.add(cerrar);  
        south.setBorder(new EmptyBorder(0,0,10,10));  
        add(south, BorderLayout.SOUTH);

        cerrar.addActionListener(e \-\> {  
            dispose();  
            if (padre \!= null)  
                padre.setVisible(true);  
        });  
    }

    private JPanel crearPanel(ItemMenu.Tipo tipo) {  
        JPanel p \= new JPanel(new BorderLayout(10,10));  
        p.setBackground(Color.WHITE);  
        p.setBorder(new EmptyBorder(10,10,10,10));

        List\<ItemMenu\> lista \= ctrl.lista(tipo);  
        ItemTableModel modelo \= new ItemTableModel(lista, tipo);  
        JTable tabla \= new JTable(modelo);  
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));  
        tabla.setRowHeight(24);  
        tabla.setShowGrid(true);  
        tabla.setGridColor(new Color(230, 220, 210));  
        tabla.setBackground(Color.WHITE);  
        tabla.setSelectionBackground(new Color(210, 180, 140));  
        tabla.setSelectionForeground(Color.WHITE);  
        JTableHeader header \= tabla.getTableHeader();  
        header.setFont(new Font("SansSerif", Font.BOLD, 13));  
        header.setBackground(new Color(240, 230, 220));  
        header.setForeground(new Color(80, 50, 30));

        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton add \= new JButton("Añadir");  
        JButton del \= new JButton("Eliminar");  
        JButton tog \= new JButton("Activar/Desactivar");  
        for (JButton btn : List.of(add, del, tog)) {  
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));  
            btn.setBackground(Color.WHITE);  
            btn.setForeground(new Color(80, 50, 30));  
            btn.setFocusPainted(false);  
        }  
        JPanel btns \= new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));  
        btns.setBackground(Color.WHITE);  
        btns.add(add);  
        btns.add(del);  
        btns.add(tog);  
        p.add(btns, BorderLayout.SOUTH);

        add.addActionListener(e \-\> ctrl.addNuevo(tipo, modelo));  
        del.addActionListener(e \-\> ctrl.eliminar(tabla.getSelectedRow(), modelo));  
        tog.addActionListener(e \-\> ctrl.toggle(tabla.getSelectedRow(), modelo));

        return p;  
    }

    private JPanel crearPanelVentas() {  
        JPanel p \= new JPanel(new BorderLayout(10,10));  
        p.setBackground(Color.WHITE);  
        p.setBorder(new EmptyBorder(10,10,10,10));

        JPanel top \= new JPanel(new BorderLayout(5,5));  
        top.setBackground(Color.WHITE);  
        JTextField buscador \= new JTextField();  
        buscador.setFont(new Font("SansSerif", Font.PLAIN, 13));  
        JButton btnBuscar \= new JButton("Buscar");  
        btnBuscar.setFont(new Font("SansSerif", Font.BOLD, 13));  
        btnBuscar.setBackground(Color.WHITE);  
        btnBuscar.setForeground(new Color(80, 50, 30));  
        btnBuscar.setFocusPainted(false);  
        JLabel lbl \= new JLabel("Buscar por empleado:");  
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));  
        top.add(lbl, BorderLayout.WEST);  
        top.add(buscador, BorderLayout.CENTER);  
        top.add(btnBuscar, BorderLayout.EAST);  
        p.add(top, BorderLayout.NORTH);

        JTextArea area \= new JTextArea();  
        area.setEditable(false);  
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));  
        JScrollPane scroll \= new JScrollPane(area);  
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200,180,150),1));  
        p.add(scroll, BorderLayout.CENTER);

        cargarVentas(area, "");  
        btnBuscar.addActionListener(e \-\> cargarVentas(area, buscador.getText().trim()));

        return p;  
    }

    private void cargarVentas(JTextArea area, String nombreEmpleado) {  
        area.setText("");  
        try (BufferedReader br \= new BufferedReader(new FileReader("compras.txt"))) {  
            String linea;  
            StringBuilder bloque \= new StringBuilder();  
            boolean match \= nombreEmpleado.isEmpty();  
            while ((linea \= br.readLine()) \!= null) {  
                if (linea.startsWith("Empleado:")) {  
                    match \= linea.toLowerCase().contains(nombreEmpleado.toLowerCase());  
                }  
                if (match) bloque.append(linea).append("\\n");  
                if (linea.equals("---")) {  
                    if (match) area.append(bloque.toString() \+ "\\n");  
                    bloque.setLength(0);  
                    match \= nombreEmpleado.isEmpty();  
                }  
            }  
        } catch (IOException e) {  
            area.setText("No se pudo leer el archivo de ventas.");  
        }  
    }

    private JPanel crearPanelUsuarios() {  
        ControladorUsuario ctrlU \= new ControladorUsuario();  
        List\<Usuario\> usuarios \= ctrlU.obtenerTodos();  
        String\[\] cols \= { "Nombre", "Rol" };  
        Object\[\]\[\] datos \= new Object\[usuarios.size()\]\[2\];  
        for (int i \= 0; i \< usuarios.size(); i\++) {  
            datos\[i\]\[0\] \= usuarios.get(i).getNombre();  
            datos\[i\]\[1\] \= usuarios.get(i).getRol().name();  
        }

        JTable tabla \= new JTable(datos, cols);  
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));  
        tabla.setRowHeight(24);  
        tabla.setGridColor(new Color(230, 220, 210));  
        tabla.setShowGrid(true);  
        tabla.setBackground(Color.WHITE);  
        tabla.setSelectionBackground(new Color(210, 180, 140));  
        tabla.setSelectionForeground(Color.WHITE);  
        JTableHeader th \= tabla.getTableHeader();  
        th.setFont(new Font("SansSerif", Font.BOLD, 13));  
        th.setBackground(new Color(240, 230, 220));  
        th.setForeground(new Color(80, 50, 30));

        JPanel p \= new JPanel(new BorderLayout(10,10));  
        p.setBackground(Color.WHITE);  
        p.setBorder(new EmptyBorder(10,10,10,10));  
        p.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnAdd \= new JButton("Añadir");  
        JButton btnDel \= new JButton("Eliminar");  
        for (JButton btn : List.of(btnAdd, btnDel)) {  
            btn.setFont(new Font("SansSerif", Font.BOLD, 13));  
            btn.setBackground(Color.WHITE);  
            btn.setForeground(new Color(80, 50, 30));  
            btn.setFocusPainted(false);  
        }  
        JPanel btns \= new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));  
        btns.setBackground(Color.WHITE);  
        btns.add(btnAdd);  
        btns.add(btnDel);  
        p.add(btns, BorderLayout.SOUTH);

        btnAdd.addActionListener(e \-\> {  
            String nombre \= JOptionPane.showInputDialog(this, "Nombre:");  
            if (nombre \== null || nombre.trim().isEmpty()) return;  
            String pass \= JOptionPane.showInputDialog(this, "Contraseña:");  
            if (pass \== null) return;  
            String rolSel \= (String) JOptionPane.showInputDialog(  
                    this, "Rol:", "Seleccione rol",  
                    JOptionPane.PLAIN\_MESSAGE, null,  
                    new String\[\]{"GERENTE", "EMPLEADO"}, "EMPLEADO");  
            Usuario.Rol rol \= Usuario.Rol.valueOf(rolSel);  
            ctrlU.agregar(new Usuario(nombre, pass, rol));  
            dispose();  
            new VentanaGerente(padre).setVisible(true);  
        });

        btnDel.addActionListener(e \-\> {  
            int fila \= tabla.getSelectedRow();  
            if (fila \< 0) return;  
            ctrlU.eliminar(fila);  
            dispose();  
            new VentanaGerente(padre).setVisible(true);  
        });

        return p;  
    }  
}

Directo en src Main:  
import javax.swing.SwingUtilities;  
import vista.PantallaLogin;

public class Main {  
    public static void main(String\[\] args) {  
        SwingUtilities.invokeLater(() \-\> {  
            new PantallaLogin().setVisible(true);  
        });  
    }  
}

