package modelo;

public class Usuario {
    public enum Rol {
        GERENTE, EMPLEADO
    }

    private String nombre;
    private String password;
    private Rol rol;

    public Usuario(String nombre, String password, Rol rol) {
        this.nombre = nombre;
        this.password = password;
        this.rol = rol;
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
