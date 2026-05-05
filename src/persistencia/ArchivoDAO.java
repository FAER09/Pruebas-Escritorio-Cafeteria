package persistencia;

import modelo.ItemMenu;
import modelo.ItemMenu.Tipo;
import modelo.Ticket;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ArchivoDAO {

    private static final String BEB = "bebidas.txt";
    private static final String CON = "condimentos.txt";
    private static final String COM = "compras.txt";

    public List<ItemMenu> cargar(Tipo t) {
        String archivo = (t == Tipo.BEBIDA) ? BEB : CON;
        List<ItemMenu> lista = new ArrayList<ItemMenu>();
        Path p = Paths.get(archivo);

        if (!Files.exists(p))
            return lista;

        try (BufferedReader br = Files.newBufferedReader(p)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("#") || linea.trim().isEmpty())
                    continue;
                String[] parts = linea.split("\\|");
                String n = parts[0];
                double pr = Double.parseDouble(parts[1]);
                boolean ac = Boolean.parseBoolean(parts[2]);
                lista.add(new ItemMenu(n, pr, ac, t));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardar(List<ItemMenu> lista, Tipo t) {
        String archivo = (t == Tipo.BEBIDA) ? BEB : CON;
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(archivo))) {
            for (ItemMenu it : lista) {
                bw.write(it.getNombre() + "|" + it.getPrecio() + "|" + it.isActivo());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void guardarTicket(Ticket tk) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(COM, true))) {
            bw.write(tk.formato());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> ticketsDe(String cliente) {
        List<String> list = new ArrayList<String>();
        if (!Files.exists(Paths.get(COM)))
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(COM))) {
            StringBuilder bloque = new StringBuilder();
            String linea;
            boolean match = false;
            while ((linea = br.readLine()) != null) {
                if (linea.startsWith("Cliente:"))
                    match = linea.toLowerCase().contains(cliente.toLowerCase());

                bloque.append(linea).append('\n');

                if (linea.equals("---")) {
                    if (match)
                        list.add(bloque.toString());
                    bloque.setLength(0);
                    match = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
