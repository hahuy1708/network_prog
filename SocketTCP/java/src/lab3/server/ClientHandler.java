package lab3.server;
import java.io.*;
import java.net.*;

import javax.swing.JTextArea;

public class ClientHandler extends Thread {
    private Socket socket;
    private JTextArea chatArea;
    private PrintWriter out;

    public ClientHandler(Socket s, JTextArea area) {
        this.socket = s;
        this.chatArea = area;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out = new PrintWriter(socket.getOutputStream(), true);
            synchronized (ServerGUI.clientWriters) {
                ServerGUI.clientWriters.add(this.out);
            }
            
            String msg;
            while ((msg = in.readLine()) != null) {
            	chatArea.append("Client: " + msg + "\n");
            	ServerGUI.broadcast(msg, this.out);
            }
        } catch (IOException e) {
        	chatArea.append("Một kết nối đã đóng.\n");
        } finally {
            if (out != null) {
                synchronized (ServerGUI.clientWriters) { ServerGUI.clientWriters.remove(out); }
            }
            try { socket.close(); } catch (IOException e) {}
        }
    }
}