package vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

import controlador.ControladorUsuario;
import modelo.Usuario;
import modelo.Usuario.Rol;

public class PantallaLogin extends JFrame {
    private final JTextField tfUser = new JTextField(15);
    private final JPasswordField tfPass = new JPasswordField(15);
    private final ControladorUsuario ctrlUsuario = new ControladorUsuario();

    public PantallaLogin() {
        super("Login - Cafetería Decorator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(panelPrincipal);

        JLabel lblImagen = new JLabel();
        try {
            URL imageUrl = new URL("https://img.freepik.com/psd-gratis/ilustracion-cafe-dibujado-mano_23-2150795456.jpg?semt=ais_items_boosted&w=740");
            Image image = ImageIO.read(imageUrl).getScaledInstance(180, 220, Image.SCALE_SMOOTH);
            lblImagen.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            lblImagen.setText("☕");
            lblImagen.setFont(new Font("SansSerif", Font.PLAIN, 60));
        }
        lblImagen.setBorder(new EmptyBorder(0, 0, 0, 20));
        lblImagen.setHorizontalAlignment(SwingConstants.CENTER);
        panelPrincipal.add(lblImagen, BorderLayout.WEST);

        JPanel contenedorCentro = new JPanel(new GridBagLayout());
        contenedorCentro.setBackground(Color.WHITE);
        panelPrincipal.add(contenedorCentro, BorderLayout.CENTER);

        JPanel panelForm = new JPanel();
        panelForm.setLayout(new BoxLayout(panelForm, BoxLayout.Y_AXIS));
        panelForm.setBackground(Color.WHITE);
        panelForm.setBorder(new EmptyBorder(10, 30, 10, 30));

        panelForm.add(crearCampo("Usuario", tfUser));
        panelForm.add(Box.createVerticalStrut(15));
        panelForm.add(crearCampo("Contraseña", tfPass));
        panelForm.add(Box.createVerticalStrut(25));

        JButton btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(new Color(198, 143, 132));
        btnLogin.setForeground(new Color(80, 50, 30));
        btnLogin.setFocusPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.setMaximumSize(new Dimension(180, 40));
        panelForm.add(btnLogin);

        contenedorCentro.add(panelForm);

        btnLogin.addActionListener(e -> intentarLogin());
    }

    private JPanel crearCampo(String etiqueta, JComponent campo) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(80, 50, 30));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        campo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        campo.setBackground(new Color(255, 250, 245));
        campo.setBorder(new LineBorder(new Color(200, 180, 150), 1, true));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(campo, BorderLayout.CENTER);

        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.setMaximumSize(new Dimension(260, 60));

        return panel;
    }

    private void intentarLogin() {
        String user = tfUser.getText().trim();
        String pass = new String(tfPass.getPassword());

        Usuario usuario = ctrlUsuario.obtenerUsuario(user, pass);

        if (usuario == null) {
            JOptionPane.showMessageDialog(this,
                "Credenciales incorrectas", "Error",
                JOptionPane.ERROR_MESSAGE);
            tfUser.setText("");
            tfPass.setText("");
            return;
        }

        tfUser.setText("");
        tfPass.setText("");
        dispose();

        if (usuario.getRol() == Rol.GERENTE) {
            new VentanaGerente(this).setVisible(true);
        } else {
            new VentanaEmpleado(this, user).setVisible(true);
        }
    }
}
