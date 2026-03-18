package lab1;

import java.io.*;
import java.net.*;

class ProcessingThread extends Thread {
    private Socket socket;
    public ProcessingThread(Socket s) { this.socket = s; start(); }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            
            String input;
            while ((input = in.readLine()) != null) {
                if (input.equalsIgnoreCase("exit")) break;

                
                String reversed = new StringBuilder(input).reverse().toString();
                
                String upper = input.toUpperCase();
                String lower = input.toLowerCase();
                
                String swapped = "";
                for (char c : input.toCharArray()) {
                    if (Character.isUpperCase(c)) swapped += Character.toLowerCase(c);
                    else if (Character.isLowerCase(c)) swapped += Character.toUpperCase(c);
                    else swapped += c;
                }
                
                int vowels = 0;
                for (char c : input.toLowerCase().toCharArray()) 
                    if ("aeiou".indexOf(c) != -1) vowels++;
                int words = input.trim().isEmpty() ? 0 : input.trim().split("\\s+").length;

                out.println("--- Ket qua tu Server ---");
                out.println("Dao nguoc: " + reversed);
                out.println("In hoa: " + upper);
                out.println("In thuong: " + lower);
                out.println("Hoan doi hoa-thuong: " + swapped);
                out.println("So tu: " + words + " | So nguyen am: " + vowels);
                out.println("END");
            }
        } catch (IOException e) { System.out.println("Loi: " + e.getMessage());
        } finally { try { socket.close(); } catch (IOException e) {} }
    }
}

public class lab1_server {

	public static void main(String[] args) {
		ServerSocket server;
		try {
			server = new ServerSocket(1234);
			System.out.println("Server Lab 1 dang cho tai cong 1234...");
	        while (true) {
	            new ProcessingThread(server.accept());
	        }
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

}
