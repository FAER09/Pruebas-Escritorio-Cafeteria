package vista;

import modelo.ItemMenu;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {
    private final List<ItemMenu> items;
    private final String[] cols = { "Nombre", "Precio", "Activo" };
    private final ItemMenu.Tipo tipo;

    public ItemTableModel(List<ItemMenu> list, ItemMenu.Tipo tipo) {
        this.items = list;
        this.tipo = tipo;
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
        return cols[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        ItemMenu item = items.get(row);
        return switch (col) {
            case 0 -> item.getNombre();
            case 1 -> String.format("$%.2f", item.getPrecio());
            case 2 -> item.isActivo() ? "Sí" : "No";
            default -> "";
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

    public List<ItemMenu> getItems() {
        return items;
    }

    public ItemMenu.Tipo getTipo() {
        return tipo;
    }

    public static void aplicarEstiloTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabla.setRowHeight(28);
        tabla.setGridColor(new Color(230, 220, 210));
        tabla.setShowGrid(true);
        tabla.setBackground(Color.WHITE);
        tabla.setForeground(new Color(90, 60, 40));
        tabla.setSelectionBackground(new Color(210, 180, 140));
        tabla.setSelectionForeground(Color.WHITE);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 230, 220));
        header.setForeground(new Color(80, 50, 30));
    }

    public static void aplicarEstiloBoton(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(Color.WHITE);
        boton.setForeground(new Color(80, 50, 30));
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(new Color(180, 160, 140), 1));
    }
}
