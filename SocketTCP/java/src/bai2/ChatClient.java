package bai2;
import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            Socket s = new Socket("localhost", 6000);
      
            PrintWriter out = new PrintWriter(s.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            BufferedReader kb = new BufferedReader(new InputStreamReader(System.in));

            String send, receive;
            while (true) {
                System.out.print("Client: ");
                send = kb.readLine();
                out.println(send);
                if (send.equalsIgnoreCase("exit")) break;

                if ((receive = in.readLine()) != null) {
                    System.out.println("Server: " + receive);
                }
            }
            s.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}