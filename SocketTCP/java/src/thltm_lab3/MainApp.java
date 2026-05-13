package thltm_lab3;

import javax.swing.SwingUtilities;

import thltm_lab3.ui.MainFrame;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
