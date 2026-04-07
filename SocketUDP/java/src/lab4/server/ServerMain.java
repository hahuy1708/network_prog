package lab4.server;

import javax.swing.SwingUtilities;

public class ServerMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}
