import static org.junit.Assert.*;
import org.junit.Test;

import modelo.Bebida;
import modelo.BebidaGenerica;
import modelo.CondimentoGenerico;
import modelo.ItemMenu;
import modelo.ItemMenu.Tipo;
import modelo.Pedido;
import modelo.Usuario;
import modelo.Usuario.Rol;
import controlador.ControladorUsuario;


public class CafeteriaTest {

    // UC-01: Autenticacion con credenciales validas (Gerente)

    @Test
    public void uc01a_usuarioInexistente() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("usuariofalso", "admin123");
        assertNull("Un usuario que no existe no deberia poder ingresar", u);
    }

    @Test
    public void uc01b_contrasenaIncorrecta() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("admin", "mal123");
        assertNull("Una contrasena incorrecta no deberia dar acceso", u);
    }

    @Test
    public void uc01c_rolIncorrecto() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("empleado1", "emp123");
        assertNotNull("El empleado debe existir en el sistema", u);
        assertNotEquals("Un empleado no debe tener rol de Gerente",
            Rol.GERENTE, u.getRol());
    }

    @Test
    public void uc01d_credencialesCorrectasGerente() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("admin", "admin123");
        assertNotNull("El gerente debe ser autenticado correctamente", u);
        assertEquals("El rol del usuario debe ser GERENTE",
            Rol.GERENTE, u.getRol());
    }

    // UC-02: Autenticacion con credenciales invalidas

    @Test
    public void uc02a_contrasenaErronea() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("admin", "passwordIncorrecto");
        assertNull("No se debe dar acceso con contrasena incorrecta", u);
    }

    @Test
    public void uc02b_usuarioNoExiste() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("fantasma", "cualquiera");
        assertNull("Un usuario que no existe no debe poder ingresar", u);
    }

    @Test
    public void uc02c_credencialesValidas() {
        ControladorUsuario ctrl = new ControladorUsuario();
        Usuario u = ctrl.obtenerUsuario("admin", "admin123");
        assertNotNull("Con credenciales correctas el acceso debe ser permitido", u);
    }


    // UC-03: Calculo de precio con bebida y condimento

    @Test
    public void uc03a_condimentoPrecioCero() {
        Bebida bebida = new BebidaGenerica("Cafe", 25.0);
        bebida = new CondimentoGenerico(bebida, "Extra", 0.0);
        assertEquals("Si el condimento cuesta cero, el precio no debe cambiar",
            25.0, bebida.obtenerCosto(), 0.001);
    }

    @Test
    public void uc03b_sinCondimento() {
        Bebida bebida = new BebidaGenerica("Cafe", 25.0);
        assertEquals("El precio sin condimentos debe ser igual al precio base",
            25.0, bebida.obtenerCosto(), 0.001);
        assertEquals("La descripcion debe ser solo el nombre de la bebida",
            "Cafe", bebida.obtenerDescripcion());
    }

    @Test
    public void uc03c_unCondimento() {
        Bebida bebida = new BebidaGenerica("Cafe", 25.0);
        bebida = new CondimentoGenerico(bebida, "Leche", 5.0);
        assertEquals("El precio debe ser la suma de la bebida y el condimento",
            30.0, bebida.obtenerCosto(), 0.001);
        assertTrue("La descripcion debe incluir el nombre de la bebida",
            bebida.obtenerDescripcion().contains("Cafe"));
        assertTrue("La descripcion debe incluir el nombre del condimento",
            bebida.obtenerDescripcion().contains("Leche"));
    }

    @Test
    public void uc03d_dosCondimentos() {
        Bebida bebida = new BebidaGenerica("Cafe", 25.0);
        bebida = new CondimentoGenerico(bebida, "Leche", 5.0);
        bebida = new CondimentoGenerico(bebida, "Vainilla", 5.0);
        assertEquals("El precio debe sumar la bebida y los dos condimentos",
            35.0, bebida.obtenerCosto(), 0.001);
        assertTrue("La descripcion debe incluir todos los condimentos",
            bebida.obtenerDescripcion().contains("Vainilla"));
    }


    // UC-04: Lectura de bebidas desde archivo

    @Test
    public void uc04a_archivoNoExiste() {
        persistencia.ArchivoDAO dao = new persistencia.ArchivoDAO();
        java.io.File f   = new java.io.File("bebidas.txt");
        java.io.File tmp = new java.io.File("bebidas_backup_test.txt");
        boolean existia = f.exists();
        if (existia) f.renameTo(tmp);
        try {
            java.util.List<ItemMenu> lista = dao.cargar(Tipo.BEBIDA);
            assertNotNull("La lista no debe ser null aunque el archivo no exista", lista);
            assertTrue("La lista debe estar vacia si el archivo no existe", lista.isEmpty());
        } finally {
            if (existia) tmp.renameTo(f);
        }
    }

    @Test
    public void uc04c_archivoCorrecto() {
        persistencia.ArchivoDAO dao = new persistencia.ArchivoDAO();
        java.util.List<ItemMenu> lista = dao.cargar(Tipo.BEBIDA);
        assertFalse("La lista no debe estar vacia si el archivo tiene registros",
            lista.isEmpty());
        assertTrue("Cada bebida debe tener un precio mayor a cero",
            lista.get(0).getPrecio() > 0);
        assertEquals("Cada elemento debe estar identificado como BEBIDA",
            Tipo.BEBIDA, lista.get(0).getTipo());
    }


    // UC-05: Generacion correcta de la linea del ticket

    @Test
    public void uc05a_descripcionVacia() {
        Pedido pedido = new Pedido(1, "", 25.0);
        String linea = pedido.getLineaTicket();
        assertNotNull("La linea del ticket no debe ser null", linea);
        assertFalse("La linea no debe contener la palabra null",
            linea.contains("null"));
    }

    @Test
    public void uc05b_cantidadCero() {
        Pedido pedido = new Pedido(0, "Cafe", 0.0);
        String linea = pedido.getLineaTicket();
        assertTrue("La linea debe reflejar que la cantidad es cero",
            linea.contains("0"));
    }

    @Test
    public void uc05c_pedidoSimple() {
        Pedido pedido = new Pedido(1, "Espresso", 25.0);
        String linea = pedido.getLineaTicket();
        assertTrue("La linea debe incluir el nombre de la bebida",
            linea.contains("Espresso"));
        assertTrue("La linea debe incluir el precio",
            linea.contains("25"));
    }

    @Test
    public void uc05d_pedidoConCondimentos() {
        Pedido pedido = new Pedido(2, "Cafe + Leche", 60.0);
        String linea = pedido.getLineaTicket();
        assertTrue("La linea debe incluir la descripcion completa",
            linea.contains("Cafe + Leche"));
        assertTrue("La linea debe incluir el precio total",
            linea.contains("60"));
        assertTrue("La linea debe comenzar con la cantidad",
            linea.startsWith("2"));
    }
}
