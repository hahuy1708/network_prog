package lab3.client;

import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientGUI::new);
    }
}
