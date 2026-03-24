package lab1.client;
import java.io.*;
import java.net.*;

public class ClientSocket {
    public String sendRequest(String data) {
        try (Socket socket = new Socket("localhost", 1234);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(data); // Gửi chuỗi lên Server [12]
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null && !line.equals("END")) {
                response.append(line).append("\n");
            }
            return response.toString();
        } catch (IOException e) {
            return "Lỗi kết nối: " + e.getMessage();
        }
    }
}