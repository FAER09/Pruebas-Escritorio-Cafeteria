import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import controlador.ControladorUsuario;
import modelo.Bebida;
import modelo.BebidaGenerica;
import modelo.CondimentoGenerico;
import modelo.ItemMenu;
import modelo.Pedido;
import modelo.Usuario;
import persistencia.ArchivoDAO;

import java.util.List;

public class CafeteriaTest {

    // UC-01: Login con credenciales válidas de Gerente
    @Test
    @DisplayName("UC-01 — Login con credenciales válidas de Gerente")
    void testLoginGerente() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario usuario = ctrl.obtenerUsuario("admin", "admin123");

        assertNotNull(usuario, "Debe retornar un usuario, no null");
        assertEquals(Usuario.Rol.GERENTE, usuario.getRol(), "El rol debe ser GERENTE");
    }

    // UC-02: Login con contraseña incorrecta retorna null
    @Test
    @DisplayName("UC-02 — Login con credenciales inválidas retorna null")
    void testLoginInvalido() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario usuario = ctrl.obtenerUsuario("admin", "contraseñaIncorrecta");

        assertNull(usuario, "Debe retornar null con contraseña incorrecta");
    }

    // UC-03: Decorator suma precio y concatena descripción
    @Test
    @DisplayName("UC-03 — Precio y descripción correctos con Decorator")
    void testDecoratorPrecio() {
        Bebida bebida = new BebidaGenerica("Café", 25.0);
        bebida = new CondimentoGenerico(bebida, "Leche", 5.0);

        assertEquals(30.0, bebida.obtenerCosto(), 0.001,
            "El precio debe ser 30.00 (25 + 5)");
        assertEquals("Café + Leche", bebida.obtenerDescripcion(),
            "La descripción debe ser 'Café + Leche'");
    }

    // UC-04: ArchivoDAO lee bebidas.txt correctamente
    @Test
    @DisplayName("UC-04 — Lectura correcta de bebidas desde bebidas.txt")
    void testLecturaBebidas() {
        ArchivoDAO dao = new ArchivoDAO();
        List<ItemMenu> lista = dao.cargar(ItemMenu.Tipo.BEBIDA);

        assertFalse(lista.isEmpty(), "La lista no debe estar vacía");
        assertTrue(lista.get(0).getPrecio() > 0,
            "El precio debe ser mayor a cero");
        assertEquals(ItemMenu.Tipo.BEBIDA, lista.get(0).getTipo(),
            "El tipo debe ser BEBIDA");
    }

    // UC-05: Pedido genera línea de ticket con formato correcto
    @Test
    @DisplayName("UC-05 — Línea de ticket con formato correcto")
    void testLineaTicket() {
        Pedido pedido = new Pedido(2, "Café + Leche", 60.0);
        String linea = pedido.getLineaTicket();

        assertEquals("2× Café + Leche - $60.00", linea,
            "El formato debe ser: cantidad× descripción - $precio");
    }
}
