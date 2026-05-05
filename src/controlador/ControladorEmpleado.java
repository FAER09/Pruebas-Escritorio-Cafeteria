package controlador;

import java.util.List;
import java.util.stream.Collectors;
import modelo.ItemMenu;
import modelo.ItemMenu.Tipo;
import modelo.Ticket;
import persistencia.ArchivoDAO;

public class ControladorEmpleado {
    private final ArchivoDAO dao = new ArchivoDAO();

    public List<ItemMenu> bebidasActivas() {
        return dao.cargar(Tipo.BEBIDA).stream()
                .filter(ItemMenu::isActivo)
                .collect(Collectors.toList());
    }

    public List<ItemMenu> condimentosActivos() {
        return dao.cargar(Tipo.CONDIMENTO).stream()
                .filter(ItemMenu::isActivo)
                .collect(Collectors.toList());
    }

    public void guardarTicket(Ticket t) {
        dao.guardarTicket(t);
    }
}
