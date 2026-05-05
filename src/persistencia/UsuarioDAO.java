package persistencia;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import modelo.Usuario;
import modelo.Usuario.Rol;

public class UsuarioDAO {

    private static final String USUARIOS_FILE = "usuarios.txt";

    public List<Usuario> cargarTodos() {
        List<Usuario> lista = new ArrayList<>();
        Path path = Paths.get(USUARIOS_FILE);
        if (!Files.exists(path)) {
            return lista;
        }
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] parts = line.split("\\|");
                String nombre = parts[0];
                String pass = parts[1];
                Rol rol = Rol.valueOf(parts[2]);
                lista.add(new Usuario(nombre, pass, rol));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public void guardarTodos(List<Usuario> usuarios) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(USUARIOS_FILE))) {
            for (Usuario u : usuarios) {
                bw.write(u.getNombre() + "|" + u.getPassword() + "|" + u.getRol().name());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Usuario> leerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(USUARIOS_FILE))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 3) {
                    String nombre = partes[0].trim();
                    String pass = partes[1].trim();
                    Usuario.Rol rol = Usuario.Rol.valueOf(partes[2].trim().toUpperCase());
                    usuarios.add(new Usuario(nombre, pass, rol));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return usuarios;
    }
}
