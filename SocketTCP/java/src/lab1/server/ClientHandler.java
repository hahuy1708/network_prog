package lab1.server;
import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {
    private Socket socket;
    public ClientHandler(Socket socket) { this.socket = socket; }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String input = in.readLine();
            if (input != null) {
                StringBuilder response = new StringBuilder();
                response.append("Đảo ngược: ").append(new StringBuilder(input).reverse()).append("\n");
                response.append("In hoa: ").append(input.toUpperCase()).append("\n");
                response.append("In thường: ").append(input.toLowerCase()).append("\n");
                response.append("Hoán vị hoa-thường: ").append(swapCase(input)).append("\n");
                response.append(countWordsAndVowels(input));
                
                out.println(response.toString() + "END"); // Gửi trả kết quả [8]
            }
        } catch (IOException e) { e.printStackTrace();
        } finally { try { socket.close(); } catch (IOException e) {} }
    }

    private String swapCase(String s) { // Logic từ slide 136
        String res = "";
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) res += Character.toLowerCase(c);
            else if (Character.isLowerCase(c)) res += Character.toUpperCase(c);
            else res += c;
        }
        return res;
    }

    private String countWordsAndVowels(String s) {
        int vowels = 0, words = s.trim().isEmpty() ? 0 : s.trim().split("\\s+").length;
        for (char c : s.toLowerCase().toCharArray()) 
            if ("aeiou".indexOf(c) != -1) vowels++;
        return "Số từ: " + words + " | Số nguyên âm: " + vowels;
    }
}