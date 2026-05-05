package controlador;

import java.util.List;
import javax.swing.*;
import modelo.ItemMenu;
import modelo.ItemMenu.Tipo;
import persistencia.ArchivoDAO;
import vista.ItemTableModel;

public class ControladorGerente {

    private final ArchivoDAO dao = new ArchivoDAO();

    public List<ItemMenu> lista(Tipo t) {
        return dao.cargar(t);
    }

    public void addNuevo(Tipo t, ItemTableModel modelo) {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        nombre = nombre.trim();

        for (ItemMenu item : modelo.getItems()) {
            if (item.getNombre().trim().equalsIgnoreCase(nombre)) {
                JOptionPane.showMessageDialog(null, "Ya existe un " + t.name().toLowerCase() + " con ese nombre.");
                return;
            }
        }

        String prec = JOptionPane.showInputDialog("Precio:");
        double p;
        try {
            p = Double.parseDouble(prec);
        } catch (NumberFormatException ex) {
            return;
        }

        modelo.addItem(new ItemMenu(nombre, p, true, t));
        dao.guardar(modelo.getItems(), t);
    }

    public void eliminar(int fila, ItemTableModel modelo) {
        if (fila < 0) {
            return;
        }
        modelo.removeItem(fila);
        dao.guardar(modelo.getItems(), modelo.getTipo());
    }

    public void toggle(int fila, ItemTableModel modelo) {
        if (fila < 0) {
            return;
        }
        ItemMenu it = modelo.getItems().get(fila);
        it.setActivo(!it.isActivo());
        modelo.fireTableRowsUpdated(fila, fila);
        dao.guardar(modelo.getItems(), modelo.getTipo());
    }

    public List<String> buscarCompras(String cliente) {
        return dao.ticketsDe(cliente);
    }
}
