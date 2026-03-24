package lab2.client;
import java.io.*;
import java.net.*;

public class ClientSocket {
    private String host;
    private int port;

    public ClientSocket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendExpression(String expression) {
        try (Socket socket = new Socket(host, port); 
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(expression); // Gửi chuỗi biểu thức [10]
            return in.readLine();    // Nhận kết quả từ Server [11]
            
        } catch (IOException e) {
            return "Lỗi kết nối: " + e.getMessage();
        }
    }
}