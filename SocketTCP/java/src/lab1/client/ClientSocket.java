package lab1.client;
import java.io.*;
import java.net.*;

public class ClientSocket {
	private String host;
	private int port;
	
	public ClientSocket(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
    public String sendRequest(String data) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(data);
            
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