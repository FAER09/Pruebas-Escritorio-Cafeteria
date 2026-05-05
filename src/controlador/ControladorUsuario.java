package controlador;

import java.util.List;
import modelo.Usuario;
import modelo.Usuario.Rol;
import persistencia.UsuarioDAO;

public class ControladorUsuario {

    private final UsuarioDAO dao = new UsuarioDAO();
    private List<Usuario> cache = dao.cargarTodos();

    public boolean validarCredenciales(String nombre, String pass, Rol rol) {
        return cache.stream()
                .anyMatch(u -> u.getNombre().equalsIgnoreCase(nombre)
                && u.getPassword().equals(pass)
                && u.getRol() == rol);
    }

    public boolean validarNombre(String nombre) {
        return cache.stream()
                .anyMatch(u -> u.getNombre().equalsIgnoreCase(nombre)
                && u.getRol() == Rol.EMPLEADO);
    }

    public List<Usuario> obtenerTodos() {
        return cache;
    }

    public void agregar(Usuario u) {
        cache.add(u);
        dao.guardarTodos(cache);
    }

    public void eliminar(int indice) {
        if (indice >= 0 && indice < cache.size()) {
            cache.remove(indice);
            dao.guardarTodos(cache);
        }
    }

    public Usuario obtenerUsuario(String nombre, String pass) {
        this.cache = dao.leerTodos();
        return cache.stream()
                .filter(u -> u.getNombre().equalsIgnoreCase(nombre)
                && u.getPassword().equals(pass))
                .findFirst()
                .orElse(null);
    }
}
