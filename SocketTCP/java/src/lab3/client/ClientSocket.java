package lab3.client;
import java.io.*;
import java.net.*;
import javax.swing.JTextArea;

public class ClientSocket {
    private String host;
    private int port;
    private Socket socket;
    private PrintWriter out;
    private JTextArea chatArea;

    public ClientSocket(String host, int port, JTextArea area) {
        this.host = host;
        this.port = port;
        this.chatArea = area;
    }

    public boolean connect() {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            
            
            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException e) { chatArea.append("Mất kết nối Server.\n"); }
            }).start();
            
            return true;
        } catch (IOException e) { return false; }
    }

    public void sendMessage(String msg) {
        if (out != null) out.println(msg);
    }
}