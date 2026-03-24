package lab1.server;
import javax.swing.*;
public class ServerGUI extends JFrame {
    public ServerGUI() {
        setTitle("Lab 1 Server Status");
        setSize(300, 200);
        add(new JLabel("Server is running...", SwingConstants.CENTER));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}
