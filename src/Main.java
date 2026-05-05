import javax.swing.SwingUtilities;
import vista.PantallaLogin;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PantallaLogin().setVisible(true);
        });
    }
}
